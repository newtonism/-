package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.CeramicCatalog
import com.example.data.CeramicProduct
import com.example.data.OrderEntity
import com.example.ui.CartDisplayItem
import com.example.ui.CartSummaryState
import com.example.ui.components.CeramicProductIllustration
import com.example.ui.formatWon
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CartScreen(
    cartSummary: CartSummaryState,
    showQuickCheckout: Boolean,
    onUpdateQuantity: (CartDisplayItem, Int) -> Unit,
    onRemoveItem: (Int) -> Unit,
    onClearCart: () -> Unit,
    onOpenProductDetail: (CeramicProduct) -> Unit,
    onOpenCheckout: () -> Unit,
    onCloseCheckout: () -> Unit,
    onSubmitOrder: (String, String, String) -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToSetBuilder: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onNavigateToHome)

    if (showQuickCheckout) {
        QuickCheckoutDialog(
            cartSummary = cartSummary,
            onDismiss = onCloseCheckout,
            onConfirmOrder = onSubmitOrder
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("cart_screen"),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 12.dp,
            bottom = 104.dp
        ),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Bundle Discount & Free Shipping Progress Card
        item {
            DiscountAndShippingProgressCard(cartSummary = cartSummary)
        }

        if (cartSummary.items.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(44.dp)
                        )
                        Text(
                            text = stringResource(R.string.empty_cart_title),
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = stringResource(R.string.empty_cart_subtitle),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(
                                onClick = onNavigateToHome,
                                modifier = Modifier.testTag("empty_cart_go_home_btn")
                            ) {
                                Text("인기 도자기 보러가기")
                            }
                            OutlinedButton(
                                onClick = onNavigateToSetBuilder,
                                modifier = Modifier.testTag("empty_cart_go_set_btn")
                            ) {
                                Text("1인 세트 맞추기")
                            }
                        }
                    }
                }
            }
        } else {
            // Header with total count & Clear All
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "담은 도자기 (${cartSummary.totalQuantity}개)",
                        style = MaterialTheme.typography.titleMedium
                    )
                    TextButton(
                        onClick = onClearCart,
                        modifier = Modifier.testTag("clear_cart_btn")
                    ) {
                        Text(
                            text = "전체 비우기",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Cart Item Cards
            items(cartSummary.items, key = { it.entity.id }) { item ->
                CartItemRowCard(
                    item = item,
                    onClickProduct = { onOpenProductDetail(item.product) },
                    onQuantityChange = { newQty -> onUpdateQuantity(item, newQty) },
                    onRemove = { onRemoveItem(item.entity.id) }
                )
            }

            // Price Breakdown & Checkout CTA Card
            item {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "결제 예정 금액 안내",
                            style = MaterialTheme.typography.titleMedium
                        )

                        PriceSummaryLine(
                            label = "총 상품 금액 (${cartSummary.totalQuantity}개)",
                            value = formatWon(cartSummary.subtotalPrice)
                        )

                        PriceSummaryLine(
                            label = if (cartSummary.isBundleDiscountActive) {
                                "3개 이상 골라담기 15% 자동할인"
                            } else {
                                "골라담기 할인 (${cartSummary.remainingCountForDiscount}개 더 담으면 15%↓)"
                            },
                            value = if (cartSummary.isBundleDiscountActive) {
                                "-${formatWon(cartSummary.bundleDiscountAmount)}"
                            } else {
                                "0원"
                            },
                            highlight = cartSummary.isBundleDiscountActive
                        )

                        PriceSummaryLine(
                            label = "3중 에어캡 안심포장 배송비",
                            value = if (cartSummary.shippingFee == 0) "무료배송" else formatWon(cartSummary.shippingFee),
                            highlight = cartSummary.shippingFee == 0
                        )

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "최종 결제 금액",
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = formatWon(cartSummary.finalPrice),
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Button(
                            onClick = onOpenCheckout,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("open_checkout_btn"),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.VerifiedUser,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${formatWon(cartSummary.finalPrice)} 안심포장 주문하기",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DiscountAndShippingProgressCard(cartSummary: CartSummaryState) {
    val discountProgress = (cartSummary.totalQuantity.toFloat() / 3f).coerceIn(0f, 1f)
    val discountedSubtotal = cartSummary.subtotalPrice - cartSummary.bundleDiscountAmount
    val freeShipProgress = (discountedSubtotal.toFloat() / 25000f).coerceIn(0f, 1f)

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.65f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (cartSummary.isBundleDiscountActive) {
                        "3개 이상 골라담기 15% 할인이 적용되었어요!"
                    } else {
                        "그릇 ${cartSummary.remainingCountForDiscount}개만 더 담으면 전체 15% 자동 할인!"
                    },
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = "${cartSummary.totalQuantity.coerceAtMost(3)}/3개",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            LinearProgressIndicator(
                progress = { discountProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(7.dp)
                    .clip(RoundedCornerShape(50)),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
            )

            Text(
                text = if (cartSummary.remainingAmountForFreeShipping == 0 && cartSummary.totalQuantity > 0) {
                    "25,000원 이상 무료 안심배송 혜택까지 모두 달성!"
                } else {
                    "무료배송까지 ${formatWon(cartSummary.remainingAmountForFreeShipping)} 남음 (현재 배송비 3,000원)"
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.85f)
            )
            LinearProgressIndicator(
                progress = { freeShipProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(50)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun CartItemRowCard(
    item: CartDisplayItem,
    onClickProduct: () -> Unit,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier
                    .size(76.dp)
                    .clickable(onClick = onClickProduct)
            ) {
                CeramicProductIllustration(
                    vesselShape = item.product.vesselShape,
                    glazeStyle = item.glaze,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(6.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = item.product.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .weight(1f)
                            .clickable(onClick = onClickProduct)
                    )
                    IconButton(
                        onClick = onRemove,
                        modifier = Modifier.size(26.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "삭제",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(item.glaze.colorHex))
                    )
                    Text(
                        text = "유약: ${item.glaze.label}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatWon(item.product.price * item.entity.quantity),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { onQuantityChange(item.entity.quantity - 1) },
                                modifier = Modifier.size(30.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Remove,
                                    contentDescription = "감소",
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Text(
                                text = "${item.entity.quantity}",
                                style = MaterialTheme.typography.labelLarge,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                            IconButton(
                                onClick = { onQuantityChange(item.entity.quantity + 1) },
                                modifier = Modifier.size(30.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "증가",
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PriceSummaryLine(
    label: String,
    value: String,
    highlight: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = if (highlight) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (highlight) FontWeight.Bold else FontWeight.Normal,
            color = if (highlight) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun QuickCheckoutDialog(
    cartSummary: CartSummaryState,
    onDismiss: () -> Unit,
    onConfirmOrder: (String, String, String) -> Unit
) {
    var recipientName by remember { mutableStateOf("김도자") }
    var address by remember { mutableStateOf("서울특별시 마포구 연남로 42, 302호 (문앞 배송)") }
    val paymentMethods = listOf("카카오페이 간편결제", "네이버페이", "토스페이", "신용·체크카드")
    var selectedPayment by remember { mutableStateOf(paymentMethods.first()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "3초 간편 안심 주문",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)
                ) {
                    Text(
                        text = "총 ${cartSummary.totalQuantity}개 도자기 · 3중 에어캡 완충 포장 후 발송됩니다.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(10.dp)
                    )
                }

                OutlinedTextField(
                    value = recipientName,
                    onValueChange = { recipientName = it },
                    label = { Text("받는 분 성함") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("배송 주소 (개인통관고유부호 자동연동)") },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "결제 수단 선택",
                    style = MaterialTheme.typography.labelLarge
                )

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    paymentMethods.chunked(2).forEach { rowMethods ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            rowMethods.forEach { method ->
                                FilterChip(
                                    selected = selectedPayment == method,
                                    onClick = { selectedPayment = method },
                                    label = {
                                        Text(
                                            text = method,
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirmOrder(recipientName, address, selectedPayment) },
                modifier = Modifier.testTag("confirm_order_submit_btn")
            ) {
                Text("${formatWon(cartSummary.finalPrice)} 결제 완료하기")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소")
            }
        }
    )
}

@Composable
fun MyPageScreen(
    subTab: Int,
    orders: List<OrderEntity>,
    wishlistIds: Set<Int>,
    lastCompletedOrderNumber: String?,
    onSelectSubTab: (Int) -> Unit,
    onDismissOrderBanner: () -> Unit,
    onOpenProductDetail: (CeramicProduct) -> Unit,
    onAddToCart: (CeramicProduct) -> Unit,
    onToggleWishlist: (CeramicProduct) -> Unit,
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onNavigateToHome)

    val wishlistedProducts = CeramicCatalog.products.filter { wishlistIds.contains(it.id) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("mypage_screen"),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 12.dp,
            bottom = 104.dp
        ),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Order Completion Celebration Banner
        if (lastCompletedOrderNumber != null) {
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                            Column {
                                Text(
                                    text = "주문이 정상 접수되었어요!",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "주문번호 $lastCompletedOrderNumber · 징더전 공방에서 에어캡 3중 포장 중입니다.",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                        TextButton(onClick = onDismissOrderBanner) {
                            Text("확인")
                        }
                    }
                }
            }
        }

        // Sub-tab Switcher (주문·배송 내역 vs 찜한 도자기)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(16.dp)
                    )
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val tabs = listOf(
                    "주문·배송 내역 (${orders.size})" to 0,
                    "찜한 도자기 (${wishlistedProducts.size})" to 1
                )
                tabs.forEach { (title, index) ->
                    val selected = subTab == index
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
                        contentColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onSelectSubTab(index) }
                            .testTag("mypage_subtab_$index")
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }
        }

        if (subTab == 0) {
            if (orders.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalShipping,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(40.dp)
                            )
                            Text(
                                text = "아직 주문한 내역이 없어요",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "장바구니에 마음에 드는 그릇을 담고 3초 간편 주문을 경험해보세요.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Button(onClick = onNavigateToHome) {
                                Text("도자기 장터 둘러보기")
                            }
                        }
                    }
                }
            } else {
                items(orders, key = { it.orderId }) { order ->
                    OrderHistoryCard(order = order)
                }
            }
        } else {
            if (wishlistedProducts.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = Color(0xFFD94336),
                                modifier = Modifier.size(40.dp)
                            )
                            Text(
                                text = stringResource(R.string.empty_wishlist_title),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = stringResource(R.string.empty_wishlist_subtitle),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Button(onClick = onNavigateToHome) {
                                Text("마음에 드는 그릇 찾으러 가기")
                            }
                        }
                    }
                }
            } else {
                items(wishlistedProducts, key = { it.id }) { product ->
                    WishlistProductRowCard(
                        product = product,
                        onClick = { onOpenProductDetail(product) },
                        onAddToCart = { onAddToCart(product) },
                        onRemoveWishlist = { onToggleWishlist(product) }
                    )
                }
            }
        }
    }
}

@Composable
private fun OrderHistoryCard(order: OrderEntity) {
    val formattedDate = remember(order.orderedAt) {
        SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.KOREA).format(Date(order.orderedAt))
    }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "주문번호 ${order.orderNumber}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = order.itemsSummary,
                style = MaterialTheme.typography.titleMedium
            )

            // 4-step delivery tracker badge
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "현재 상태: ${order.deliveryStatus}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf("1. 3중 에어캡포장", "2. 직구특송 출발", "3. 국내통관", "4. 문앞배송").forEachIndexed { idx, step ->
                            Text(
                                text = step,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (idx == 0) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                },
                                fontWeight = if (idx == 0) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${order.recipientName} · ${order.paymentMethod}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = order.address,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = formatWon(order.finalPrice),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun WishlistProductRowCard(
    product: CeramicProduct,
    onClick: () -> Unit,
    onAddToCart: () -> Unit,
    onRemoveWishlist: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.size(74.dp)
            ) {
                CeramicProductIllustration(
                    vesselShape = product.vesselShape,
                    glazeStyle = product.defaultGlaze,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(6.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = product.kilnRegion.badgeText,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = formatWon(product.price),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    onClick = onAddToCart,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "+ 담기",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
                TextButton(
                    onClick = onRemoveWishlist,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "찜 해제",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
