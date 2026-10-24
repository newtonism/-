package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.CeramicCategory
import com.example.data.CeramicProduct
import com.example.data.GlazeStyle
import com.example.data.KilnRegion
import com.example.data.PriceFilter
import com.example.data.VesselShape
import com.example.ui.CartSummaryState
import com.example.ui.CeramicFilterState
import com.example.ui.components.CeramicProductIllustration
import com.example.ui.formatWon

@Composable
fun HomeStoreScreen(
    filterState: CeramicFilterState,
    products: List<CeramicProduct>,
    wishlistIds: Set<Int>,
    cartSummary: CartSummaryState,
    onSearchChange: (String) -> Unit,
    onSelectCategory: (CeramicCategory) -> Unit,
    onSelectPriceFilter: (PriceFilter) -> Unit,
    onToggleKilnRegion: (KilnRegion?) -> Unit,
    onResetFilters: () -> Unit,
    onOpenProductDetail: (CeramicProduct) -> Unit,
    onQuickAddToCart: (CeramicProduct) -> Unit,
    onUpdateCartQuantity: (CeramicProduct, Int) -> Unit,
    onToggleWishlist: (CeramicProduct) -> Unit,
    onNavigateToSetBuilder: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Map product IDs to total quantity in cart for instant card steppers
    val cartCountByProduct = cartSummary.items
        .groupBy { it.product.id }
        .mapValues { entry -> entry.value.sumOf { it.entity.quantity } }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 162.dp),
        modifier = modifier
            .fillMaxSize()
            .testTag("home_product_grid"),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 8.dp,
            bottom = 96.dp
        ),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Hero Banner
        item(span = { GridItemSpan(maxLineSpan) }) {
            HeroPorcelainBanner(
                onNavigateToSetBuilder = onNavigateToSetBuilder,
                onFilterUnder5k = { onSelectPriceFilter(PriceFilter.UNDER_5K) }
            )
        }

        // 2. Search Bar & Safe Packaging Assurance Strip
        item(span = { GridItemSpan(maxLineSpan) }) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = filterState.searchQuery,
                    onValueChange = onSearchChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_input"),
                    placeholder = {
                        Text(
                            text = "밥공기, 면기, 청화백자, 개완 검색...",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "검색"
                        )
                    },
                    trailingIcon = {
                        if (filterState.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchChange("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "검색어 지우기"
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    )
                )

                // Safe-pack & Bundle discount bar
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.65f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.25f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "3중 에어캡 안심포장 · 파손 시 사진 1장으로 100% 무료 재발송",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }
        }

        // 3. Kiln Region Quick Selector ("어느 도자기 마을에서 왔나요?")
        item(span = { GridItemSpan(maxLineSpan) }) {
            KilnRegionSelectorSection(
                selectedRegion = filterState.selectedKilnRegion,
                onToggleRegion = onToggleKilnRegion
            )
        }

        // 4. Category & Budget Price Filter Bar
        item(span = { GridItemSpan(maxLineSpan) }) {
            CategoryAndPriceFilterSection(
                selectedCategory = filterState.selectedCategory,
                selectedPriceFilter = filterState.selectedPriceFilter,
                totalMatchingCount = products.size,
                onSelectCategory = onSelectCategory,
                onSelectPriceFilter = onSelectPriceFilter
            )
        }

        // 5. Empty State or Product Cards
        if (products.isEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "조건에 맞는 도자기를 찾지 못했어요",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "가격 필터나 산지 선택을 초기화하고 전체 도자기를 둘러보세요.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        OutlinedButton(
                            onClick = onResetFilters,
                            modifier = Modifier.testTag("reset_filters_button")
                        ) {
                            Text("필터 전체 초기화")
                        }
                    }
                }
            }
        } else {
            items(products, key = { it.id }) { product ->
                CeramicProductCard(
                    product = product,
                    isWishlisted = wishlistIds.contains(product.id),
                    quantityInCart = cartCountByProduct[product.id] ?: 0,
                    onClick = { onOpenProductDetail(product) },
                    onQuickAdd = { onQuickAddToCart(product) },
                    onChangeQuantity = { newQty -> onUpdateCartQuantity(product, newQty) },
                    onToggleWishlist = { onToggleWishlist(product) }
                )
            }
        }
    }
}

@Composable
private fun HeroPorcelainBanner(
    onNavigateToSetBuilder: () -> Unit,
    onFilterUnder5k: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("hero_banner_card"),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF1A3354),
                            Color(0xFF284D7A),
                            Color(0xFF356358)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1.35f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = Color(0xFFFFF3E0).copy(alpha = 0.2f),
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.35f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalOffer,
                                contentDescription = null,
                                tint = Color(0xFFFFD584),
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "징더전·롱취안 공방 직송 가성비 그릇",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFFFF8EE)
                            )
                        }
                    }

                    Text(
                        text = "비싸지 않아 매일 쓰는\n우리 집 중국 도자기",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White
                    )

                    Text(
                        text = "개당 2,900원부터 · 아무거나 3개 이상 담으면\n장바구니에서 15% 자동 할인해 드려요!",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFE3ECF8)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = onNavigateToSetBuilder,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE87A52),
                                contentColor = Color.White
                            ),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                            modifier = Modifier.testTag("hero_set_builder_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "1인·신혼 세트 맞춤",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }

                        OutlinedButton(
                            onClick = onFilterUnder5k,
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.6f)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                            modifier = Modifier.testTag("hero_under_5k_btn")
                        ) {
                            Text(
                                text = "5천원 이하 보기",
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }

                // Decorative stacked ceramic artwork preview on right
                Box(
                    modifier = Modifier
                        .weight(0.75f)
                        .height(132.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CeramicProductIllustration(
                        vesselShape = VesselShape.NOODLE_BOWL,
                        glazeStyle = GlazeStyle.QINGHUA_BLUE,
                        modifier = Modifier.size(128.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun KilnRegionSelectorSection(
    selectedRegion: KilnRegion?,
    onToggleRegion: (KilnRegion?) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "산지별 도자기 골라보기",
                style = MaterialTheme.typography.titleMedium
            )
            if (selectedRegion != null) {
                Text(
                    text = "전체 산지 보기",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onToggleRegion(null) }
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            KilnRegion.entries.forEach { region ->
                val isSelected = selectedRegion == region
                FilterChip(
                    selected = isSelected,
                    onClick = { onToggleRegion(region) },
                    label = {
                        Text(
                            text = "${region.badgeText} (${region.chineseName})",
                            style = MaterialTheme.typography.labelMedium
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier.testTag("kiln_chip_${region.name}")
                )
            }
        }

        AnimatedVisibility(visible = selectedRegion != null) {
            selectedRegion?.let { region ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "${region.koreanName}: ${region.description}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryAndPriceFilterSection(
    selectedCategory: CeramicCategory,
    selectedPriceFilter: PriceFilter,
    totalMatchingCount: Int,
    onSelectCategory: (CeramicCategory) -> Unit,
    onSelectPriceFilter: (PriceFilter) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Category Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CeramicCategory.entries.forEach { category ->
                val selected = selectedCategory == category
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (selected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    },
                    contentColor = if (selected) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onSelectCategory(category) }
                        .testTag("category_tab_${category.name}")
                ) {
                    Text(
                        text = category.label,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp)
                    )
                }
            }
        }

        // Price Quick Filter Row + Count
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            PriceFilter.entries.forEach { priceFilter ->
                val selected = selectedPriceFilter == priceFilter
                FilterChip(
                    selected = selected,
                    onClick = { onSelectPriceFilter(priceFilter) },
                    label = {
                        Text(
                            text = priceFilter.label,
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onTertiaryContainer
                    ),
                    modifier = Modifier.testTag("price_chip_${priceFilter.name}")
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "총 ${totalMatchingCount}종",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CeramicProductCard(
    product: CeramicProduct,
    isWishlisted: Boolean,
    quantityInCart: Int,
    onClick: () -> Unit,
    onQuickAdd: () -> Unit,
    onChangeQuantity: (Int) -> Unit,
    onToggleWishlist: () -> Unit
) {
    val discountPercent = (((product.originalPrice - product.price).toFloat() / product.originalPrice) * 100).toInt()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("product_card_${product.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Top Artwork Box with Badges & Wishlist Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(146.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f))
            ) {
                CeramicProductIllustration(
                    vesselShape = product.vesselShape,
                    glazeStyle = product.defaultGlaze,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp)
                )

                // Top-left Best / Origin badge
                product.bestTag?.let { tag ->
                    Surface(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                        shape = RoundedCornerShape(bottomEnd = 12.dp, topStart = 18.dp),
                        modifier = Modifier.align(Alignment.TopStart)
                    ) {
                        Text(
                            text = tag,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                        )
                    }
                }

                // Top-right Wishlist Heart IconButton
                IconButton(
                    onClick = onToggleWishlist,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .testTag("wishlist_btn_${product.id}")
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
                    ) {
                        Icon(
                            imageVector = if (isWishlisted) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (isWishlisted) "찜 해제" else "찜하기",
                            tint = if (isWishlisted) Color(0xFFD94336) else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .padding(6.dp)
                                .size(18.dp)
                        )
                    }
                }

                // Bottom-left Glaze color dots preview
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.82f),
                            shape = RoundedCornerShape(50)
                        )
                        .padding(horizontal = 6.dp, vertical = 3.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    product.availableGlazes.forEach { glaze ->
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Color(glaze.colorHex))
                        )
                    }
                    Text(
                        text = "${product.availableGlazes.size}색",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Product Info Column
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                // Kiln origin & Appliance badges
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = product.kilnRegion.badgeText,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )
                    if (product.microwaveSafe) {
                        Text(
                            text = "전자레인지 OK",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Product Title
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.height(40.dp)
                )

                // Easy Subtitle / Dimensions
                Text(
                    text = product.dimensions,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Price Row
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = "${discountPercent}%",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = formatWon(product.price),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = formatWon(product.originalPrice),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textDecoration = TextDecoration.LineThrough
                    )
                }

                // Rating & Review count
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFF5A623),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "${product.rating} (${product.reviewCount})",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                // Direct 1-Tap Add to Cart OR Quantity Stepper
                if (quantityInCart == 0) {
                    Button(
                        onClick = onQuickAdd,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .testTag("add_cart_btn_${product.id}"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "바로 담기",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            IconButton(
                                onClick = { onChangeQuantity(quantityInCart - 1) },
                                modifier = Modifier.size(38.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Remove,
                                    contentDescription = "수량 감소",
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "${quantityInCart}개 담김",
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                            IconButton(
                                onClick = { onChangeQuantity(quantityInCart + 1) },
                                modifier = Modifier.size(38.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "수량 추가",
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
