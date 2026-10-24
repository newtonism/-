package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.CartItemEntity
import com.example.data.CeramicCatalog
import com.example.data.CeramicCategory
import com.example.data.CeramicDatabase
import com.example.data.CeramicProduct
import com.example.data.CeramicRepository
import com.example.data.GlazeStyle
import com.example.data.KilnRegion
import com.example.data.OrderEntity
import com.example.data.PriceFilter
import com.example.data.TablewareBundlePreset
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class MainTab {
    HOME,
    SET_BUILDER,
    CART,
    MY_PAGE
}

data class CartDisplayItem(
    val entity: CartItemEntity,
    val product: CeramicProduct,
    val glaze: GlazeStyle
)

data class CartSummaryState(
    val items: List<CartDisplayItem> = emptyList(),
    val totalQuantity: Int = 0,
    val subtotalPrice: Int = 0,
    val isBundleDiscountActive: Boolean = false,
    val bundleDiscountAmount: Int = 0,
    val shippingFee: Int = 0,
    val finalPrice: Int = 0,
    val remainingCountForDiscount: Int = 3,
    val remainingAmountForFreeShipping: Int = 25000
)

data class CeramicFilterState(
    val currentTab: MainTab = MainTab.HOME,
    val selectedCategory: CeramicCategory = CeramicCategory.ALL,
    val selectedPriceFilter: PriceFilter = PriceFilter.ALL,
    val selectedKilnRegion: KilnRegion? = null,
    val searchQuery: String = "",
    val selectedProductId: Int? = null,
    val detailSelectedGlaze: GlazeStyle = GlazeStyle.QINGHUA_BLUE,
    val selectedBundlePresetId: Int = CeramicCatalog.bundlePresets.first().id,
    val bundleGlazeStyle: GlazeStyle = GlazeStyle.QINGHUA_BLUE,
    val myPageSubTab: Int = 0, // 0 = Orders, 1 = Wishlist
    val toastMessage: String? = null,
    val showQuickCheckoutSheet: Boolean = false,
    val lastCompletedOrderNumber: String? = null
)

class CeramicViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: CeramicRepository

    init {
        val dao = CeramicDatabase.getInstance(application).ceramicDao()
        repository = CeramicRepository(dao)
    }

    private val _filterState = MutableStateFlow(CeramicFilterState())
    val filterState: StateFlow<CeramicFilterState> = _filterState.asStateFlow()

    val wishlistIds: StateFlow<Set<Int>> = repository.wishlistItems
        .combine(_filterState) { list, _ -> list.map { it.productId }.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val orders: StateFlow<List<OrderEntity>> = repository.orders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredProducts: StateFlow<List<CeramicProduct>> = _filterState
        .combine(wishlistIds) { state, _ ->
            CeramicCatalog.products.filter { product ->
                val matchesCategory = state.selectedCategory == CeramicCategory.ALL ||
                    product.category == state.selectedCategory
                val matchesPrice = state.selectedPriceFilter.maxPrice == null ||
                    product.price <= state.selectedPriceFilter.maxPrice
                val matchesRegion = state.selectedKilnRegion == null ||
                    product.kilnRegion == state.selectedKilnRegion
                val query = state.searchQuery.trim()
                val matchesQuery = query.isEmpty() ||
                    product.name.contains(query, ignoreCase = true) ||
                    product.subtitle.contains(query, ignoreCase = true) ||
                    product.kilnRegion.koreanName.contains(query, ignoreCase = true) ||
                    product.easyTip.contains(query, ignoreCase = true)

                matchesCategory && matchesPrice && matchesRegion && matchesQuery
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CeramicCatalog.products)

    val cartSummary: StateFlow<CartSummaryState> = repository.cartItems
        .combine(_filterState) { entities, _ ->
            val displayItems = entities.mapNotNull { entity ->
                val product = CeramicCatalog.getProductById(entity.productId) ?: return@mapNotNull null
                val glaze = GlazeStyle.entries.find { it.name == entity.selectedGlaze }
                    ?: product.defaultGlaze
                CartDisplayItem(entity = entity, product = product, glaze = glaze)
            }
            val totalQty = displayItems.sumOf { it.entity.quantity }
            val subtotal = displayItems.sumOf { it.product.price * it.entity.quantity }
            val isDiscountActive = totalQty >= 3
            val discountAmount = if (isDiscountActive) (subtotal * 0.15).toInt() else 0
            val discountedSubtotal = subtotal - discountAmount
            val shipping = when {
                totalQty == 0 -> 0
                discountedSubtotal >= 25000 -> 0
                else -> 3000
            }
            val finalTotal = discountedSubtotal + shipping
            val remCount = (3 - totalQty).coerceAtLeast(0)
            val remFreeShip = (25000 - discountedSubtotal).coerceAtLeast(0)

            CartSummaryState(
                items = displayItems,
                totalQuantity = totalQty,
                subtotalPrice = subtotal,
                isBundleDiscountActive = isDiscountActive,
                bundleDiscountAmount = discountAmount,
                shippingFee = shipping,
                finalPrice = finalTotal,
                remainingCountForDiscount = remCount,
                remainingAmountForFreeShipping = remFreeShip
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CartSummaryState())

    fun selectTab(tab: MainTab) {
        _filterState.update {
            it.copy(
                currentTab = tab,
                selectedProductId = null,
                showQuickCheckoutSheet = false
            )
        }
    }

    fun selectCategory(category: CeramicCategory) {
        _filterState.update { it.copy(selectedCategory = category) }
    }

    fun selectPriceFilter(priceFilter: PriceFilter) {
        _filterState.update { it.copy(selectedPriceFilter = priceFilter) }
    }

    fun toggleKilnRegion(region: KilnRegion?) {
        _filterState.update { current ->
            val next = if (current.selectedKilnRegion == region) null else region
            current.copy(selectedKilnRegion = next)
        }
    }

    fun updateSearchQuery(query: String) {
        _filterState.update { it.copy(searchQuery = query) }
    }

    fun resetFilters() {
        _filterState.update {
            it.copy(
                selectedCategory = CeramicCategory.ALL,
                selectedPriceFilter = PriceFilter.ALL,
                selectedKilnRegion = null,
                searchQuery = ""
            )
        }
    }

    fun openProductDetail(product: CeramicProduct) {
        _filterState.update {
            it.copy(
                selectedProductId = product.id,
                detailSelectedGlaze = product.defaultGlaze
            )
        }
    }

    fun closeProductDetail() {
        _filterState.update { it.copy(selectedProductId = null) }
    }

    fun selectDetailGlaze(glaze: GlazeStyle) {
        _filterState.update { it.copy(detailSelectedGlaze = glaze) }
    }

    fun selectBundlePreset(presetId: Int) {
        _filterState.update { it.copy(selectedBundlePresetId = presetId) }
    }

    fun selectBundleGlaze(glaze: GlazeStyle) {
        _filterState.update { it.copy(bundleGlazeStyle = glaze) }
    }

    fun setMyPageSubTab(index: Int) {
        _filterState.update { it.copy(myPageSubTab = index) }
    }

    fun addToCart(
        product: CeramicProduct,
        glaze: GlazeStyle = product.defaultGlaze,
        quantity: Int = 1
    ) {
        viewModelScope.launch {
            repository.addOrIncrementCart(product.id, glaze, quantity)
            _filterState.update {
                it.copy(toastMessage = "'${product.name}' (${glaze.label}) ${quantity}개를 장바구니에 담았어요")
            }
        }
    }

    fun addBundlePresetToCart(preset: TablewareBundlePreset, preferredGlaze: GlazeStyle) {
        viewModelScope.launch {
            var totalAdded = 0
            preset.includedProductIds.forEach { (productId, count) ->
                val product = CeramicCatalog.getProductById(productId) ?: return@forEach
                val chosenGlaze = if (product.availableGlazes.contains(preferredGlaze)) {
                    preferredGlaze
                } else {
                    product.defaultGlaze
                }
                repository.addOrIncrementCart(product.id, chosenGlaze, count)
                totalAdded += count
            }
            _filterState.update {
                it.copy(
                    toastMessage = "'${preset.title}' 총 ${totalAdded}개 구성을 장바구니에 한 번에 담았어요!",
                    currentTab = MainTab.CART
                )
            }
        }
    }

    fun updateCartItemQuantity(item: CartDisplayItem, newQty: Int) {
        viewModelScope.launch {
            repository.updateCartQuantity(item.entity, newQty)
        }
    }

    fun removeCartItem(cartItemId: Int) {
        viewModelScope.launch {
            repository.removeCartItem(cartItemId)
        }
    }

    fun clearAllCart() {
        viewModelScope.launch {
            repository.clearCart()
        }
    }

    fun toggleWishlist(product: CeramicProduct) {
        viewModelScope.launch {
            val added = repository.toggleWishlist(product.id)
            val msg = if (added) {
                "'${product.name}' 상품을 찜 목록에 추가했어요"
            } else {
                "찜 목록에서 해제했어요"
            }
            _filterState.update { it.copy(toastMessage = msg) }
        }
    }

    fun openQuickCheckout() {
        _filterState.update { it.copy(showQuickCheckoutSheet = true) }
    }

    fun closeQuickCheckout() {
        _filterState.update { it.copy(showQuickCheckoutSheet = false) }
    }

    fun submitQuickOrder(
        recipientName: String,
        address: String,
        paymentMethod: String
    ) {
        val currentSummary = cartSummary.value
        if (currentSummary.items.isEmpty()) return

        viewModelScope.launch {
            val datePrefix = SimpleDateFormat("yyyyMMdd", Locale.KOREA).format(Date())
            val randomSuffix = (1000..9999).random()
            val orderNo = "JDZ-$datePrefix-$randomSuffix"

            val firstItemName = currentSummary.items.first().product.name
            val summaryText = if (currentSummary.items.size == 1) {
                "$firstItemName (${currentSummary.totalQuantity}개)"
            } else {
                "$firstItemName 외 ${currentSummary.items.size - 1}종 (총 ${currentSummary.totalQuantity}개)"
            }

            val order = OrderEntity(
                orderNumber = orderNo,
                itemsSummary = summaryText,
                totalItemCount = currentSummary.totalQuantity,
                subtotalPrice = currentSummary.subtotalPrice,
                bundleDiscount = currentSummary.bundleDiscountAmount,
                shippingFee = currentSummary.shippingFee,
                finalPrice = currentSummary.finalPrice,
                recipientName = recipientName.ifBlank { "김도자 (기본 배송지)" },
                address = address.ifBlank { "서울특별시 마포구 연남로 42, 302호 (문앞 에어캡 안심배송)" },
                paymentMethod = paymentMethod,
                deliveryStatus = "징더전 공방 3중 에어캡 검수·포장 완료"
            )
            repository.placeOrder(order)
            _filterState.update {
                it.copy(
                    showQuickCheckoutSheet = false,
                    lastCompletedOrderNumber = orderNo,
                    currentTab = MainTab.MY_PAGE,
                    myPageSubTab = 0,
                    toastMessage = "주문이 완료되었습니다! (주문번호: $orderNo)"
                )
            }
        }
    }

    fun dismissOrderSuccessBanner() {
        _filterState.update { it.copy(lastCompletedOrderNumber = null) }
    }

    fun clearToast() {
        _filterState.update { it.copy(toastMessage = null) }
    }
}

fun formatWon(amount: Int): String {
    return NumberFormat.getNumberInstance(Locale.KOREA).format(amount) + "원"
}
