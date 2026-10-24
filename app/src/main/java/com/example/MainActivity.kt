package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.CeramicCatalog
import com.example.ui.CartSummaryState
import com.example.ui.CeramicViewModel
import com.example.ui.MainTab
import com.example.ui.formatWon
import com.example.ui.screens.CartScreen
import com.example.ui.screens.HomeStoreScreen
import com.example.ui.screens.MyPageScreen
import com.example.ui.screens.ProductDetailScreen
import com.example.ui.screens.SetBuilderScreen
import com.example.ui.theme.DailyCeramicsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DailyCeramicsTheme {
                DailyCeramicsApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyCeramicsApp(
    viewModel: CeramicViewModel = viewModel()
) {
    val filterState by viewModel.filterState.collectAsStateWithLifecycle()
    val products by viewModel.filteredProducts.collectAsStateWithLifecycle()
    val wishlistIds by viewModel.wishlistIds.collectAsStateWithLifecycle()
    val cartSummary by viewModel.cartSummary.collectAsStateWithLifecycle()
    val orders by viewModel.orders.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(filterState.toastMessage) {
        val msg = filterState.toastMessage
        if (msg != null) {
            snackbarHostState.showSnackbar(msg)
            viewModel.clearToast()
        }
    }

    // Check if Product Detail screen is open
    val activeDetailProduct = filterState.selectedProductId?.let { id ->
        CeramicCatalog.getProductById(id)
    }

    if (activeDetailProduct != null) {
        Scaffold(
            contentWindowInsets = WindowInsets.safeDrawing,
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
            ProductDetailScreen(
                product = activeDetailProduct,
                selectedGlaze = filterState.detailSelectedGlaze,
                isWishlisted = wishlistIds.contains(activeDetailProduct.id),
                onSelectGlaze = viewModel::selectDetailGlaze,
                onToggleWishlist = { viewModel.toggleWishlist(activeDetailProduct) },
                onAddToCart = { glaze, qty ->
                    viewModel.addToCart(activeDetailProduct, glaze, qty)
                },
                onBuyNow = { glaze, qty ->
                    viewModel.addToCart(activeDetailProduct, glaze, qty)
                    viewModel.closeProductDetail()
                    viewModel.selectTab(MainTab.CART)
                    viewModel.openQuickCheckout()
                },
                onBack = viewModel::closeProductDetail,
                modifier = Modifier.padding(innerPadding)
            )
        }
        return
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "瓷",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        Column {
                            Text(
                                text = stringResource(R.string.app_name),
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    }
                },
                actions = {
                    // Quick 3+ 15% Bundle Status Pill in TopAppBar
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = if (cartSummary.isBundleDiscountActive) {
                            MaterialTheme.colorScheme.tertiaryContainer
                        } else {
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                        },
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .clip(RoundedCornerShape(50))
                            .clickable { viewModel.selectTab(MainTab.CART) }
                            .testTag("top_cart_pill")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingBag,
                                contentDescription = "장바구니",
                                tint = if (cartSummary.isBundleDiscountActive) {
                                    MaterialTheme.colorScheme.tertiary
                                } else {
                                    MaterialTheme.colorScheme.primary
                                },
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (cartSummary.totalQuantity == 0) {
                                    "3개+ 15%할인"
                                } else if (cartSummary.isBundleDiscountActive) {
                                    "${cartSummary.totalQuantity}개 · 15%할인중"
                                } else {
                                    "${cartSummary.totalQuantity}개 (${cartSummary.remainingCountForDiscount}개 더 담으면 15%↓)"
                                },
                                style = MaterialTheme.typography.labelMedium,
                                color = if (cartSummary.isBundleDiscountActive) {
                                    MaterialTheme.colorScheme.onTertiaryContainer
                                } else {
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                },
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            Column {
                // Floating Mini-Cart Bar on Home screen when cart is non-empty
                AnimatedVisibility(
                    visible = filterState.currentTab == MainTab.HOME && cartSummary.totalQuantity > 0,
                    enter = slideInVertically { it } + fadeIn(),
                    exit = slideOutVertically { it } + fadeOut()
                ) {
                    FloatingMiniCartBanner(
                        cartSummary = cartSummary,
                        onGoToCart = { viewModel.selectTab(MainTab.CART) }
                    )
                }

                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp
                ) {
                    NavigationBarItem(
                        selected = filterState.currentTab == MainTab.HOME,
                        onClick = { viewModel.selectTab(MainTab.HOME) },
                        icon = {
                            Icon(
                                imageVector = if (filterState.currentTab == MainTab.HOME) {
                                    Icons.Filled.Storefront
                                } else {
                                    Icons.Outlined.Storefront
                                },
                                contentDescription = stringResource(R.string.tab_home)
                            )
                        },
                        label = { Text(stringResource(R.string.tab_home)) },
                        modifier = Modifier.testTag("nav_tab_home")
                    )

                    NavigationBarItem(
                        selected = filterState.currentTab == MainTab.SET_BUILDER,
                        onClick = { viewModel.selectTab(MainTab.SET_BUILDER) },
                        icon = {
                            Icon(
                                imageVector = if (filterState.currentTab == MainTab.SET_BUILDER) {
                                    Icons.Filled.AutoAwesome
                                } else {
                                    Icons.Outlined.AutoAwesome
                                },
                                contentDescription = stringResource(R.string.tab_set_builder)
                            )
                        },
                        label = { Text(stringResource(R.string.tab_set_builder)) },
                        modifier = Modifier.testTag("nav_tab_set_builder")
                    )

                    NavigationBarItem(
                        selected = filterState.currentTab == MainTab.CART,
                        onClick = { viewModel.selectTab(MainTab.CART) },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (cartSummary.totalQuantity > 0) {
                                        Badge {
                                            Text("${cartSummary.totalQuantity}")
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (filterState.currentTab == MainTab.CART) {
                                        Icons.Filled.ShoppingBag
                                    } else {
                                        Icons.Outlined.ShoppingBag
                                    },
                                    contentDescription = stringResource(R.string.tab_cart)
                                )
                            }
                        },
                        label = { Text(stringResource(R.string.tab_cart)) },
                        modifier = Modifier.testTag("nav_tab_cart")
                    )

                    NavigationBarItem(
                        selected = filterState.currentTab == MainTab.MY_PAGE,
                        onClick = { viewModel.selectTab(MainTab.MY_PAGE) },
                        icon = {
                            BadgedBox(
                                badge = {
                                    val badgeCount = wishlistIds.size + orders.size
                                    if (badgeCount > 0) {
                                        Badge {
                                            Text("$badgeCount")
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (filterState.currentTab == MainTab.MY_PAGE) {
                                        Icons.Filled.Favorite
                                    } else {
                                        Icons.Outlined.FavoriteBorder
                                    },
                                    contentDescription = stringResource(R.string.tab_mypage)
                                )
                            }
                        },
                        label = { Text(stringResource(R.string.tab_mypage)) },
                        modifier = Modifier.testTag("nav_tab_mypage")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (filterState.currentTab) {
                MainTab.HOME -> {
                    HomeStoreScreen(
                        filterState = filterState,
                        products = products,
                        wishlistIds = wishlistIds,
                        cartSummary = cartSummary,
                        onSearchChange = viewModel::updateSearchQuery,
                        onSelectCategory = viewModel::selectCategory,
                        onSelectPriceFilter = viewModel::selectPriceFilter,
                        onToggleKilnRegion = viewModel::toggleKilnRegion,
                        onResetFilters = viewModel::resetFilters,
                        onOpenProductDetail = viewModel::openProductDetail,
                        onQuickAddToCart = { product -> viewModel.addToCart(product) },
                        onUpdateCartQuantity = { product, newQty ->
                            val matching = cartSummary.items.firstOrNull { it.product.id == product.id }
                            if (matching != null) {
                                viewModel.updateCartItemQuantity(matching, newQty)
                            } else if (newQty > 0) {
                                viewModel.addToCart(product, product.defaultGlaze, newQty)
                            }
                        },
                        onToggleWishlist = viewModel::toggleWishlist,
                        onNavigateToSetBuilder = { viewModel.selectTab(MainTab.SET_BUILDER) }
                    )
                }

                MainTab.SET_BUILDER -> {
                    SetBuilderScreen(
                        selectedPresetId = filterState.selectedBundlePresetId,
                        selectedGlaze = filterState.bundleGlazeStyle,
                        onSelectPreset = viewModel::selectBundlePreset,
                        onSelectGlaze = viewModel::selectBundleGlaze,
                        onAddBundleToCart = viewModel::addBundlePresetToCart,
                        onOpenProductDetail = viewModel::openProductDetail,
                        onBackToHome = { viewModel.selectTab(MainTab.HOME) }
                    )
                }

                MainTab.CART -> {
                    CartScreen(
                        cartSummary = cartSummary,
                        showQuickCheckout = filterState.showQuickCheckoutSheet,
                        onUpdateQuantity = viewModel::updateCartItemQuantity,
                        onRemoveItem = viewModel::removeCartItem,
                        onClearCart = viewModel::clearAllCart,
                        onOpenProductDetail = viewModel::openProductDetail,
                        onOpenCheckout = viewModel::openQuickCheckout,
                        onCloseCheckout = viewModel::closeQuickCheckout,
                        onSubmitOrder = viewModel::submitQuickOrder,
                        onNavigateToHome = { viewModel.selectTab(MainTab.HOME) },
                        onNavigateToSetBuilder = { viewModel.selectTab(MainTab.SET_BUILDER) }
                    )
                }

                MainTab.MY_PAGE -> {
                    MyPageScreen(
                        subTab = filterState.myPageSubTab,
                        orders = orders,
                        wishlistIds = wishlistIds,
                        lastCompletedOrderNumber = filterState.lastCompletedOrderNumber,
                        onSelectSubTab = viewModel::setMyPageSubTab,
                        onDismissOrderBanner = viewModel::dismissOrderSuccessBanner,
                        onOpenProductDetail = viewModel::openProductDetail,
                        onAddToCart = { product -> viewModel.addToCart(product) },
                        onToggleWishlist = viewModel::toggleWishlist,
                        onNavigateToHome = { viewModel.selectTab(MainTab.HOME) }
                    )
                }
            }
        }
    }
}

@Composable
private fun FloatingMiniCartBanner(
    cartSummary: CartSummaryState,
    onGoToCart: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 6.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onGoToCart)
            .testTag("floating_mini_cart_banner")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (cartSummary.isBundleDiscountActive) {
                        "3개+ 골라담기 15% 자동할인 적용 완료!"
                    } else {
                        "그릇 ${cartSummary.remainingCountForDiscount}개만 더 담으면 15% 할인!"
                    },
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                )
                Text(
                    text = "장바구니 ${cartSummary.totalQuantity}개 · 결제예정 ${formatWon(cartSummary.finalPrice)}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.onPrimary
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "바로 주문",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
