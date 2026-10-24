package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class CeramicCategory(val label: String, val shortDesc: String) {
    ALL("전체보기", "모든 일상 도자기"),
    BOWLS("밥공기·면기", "매일 쓰는 밥그릇·국그릇·우동면기"),
    PLATES("접시·찬기", "나눔접시·만두접시·반찬종지"),
    TEAWARE("머그·다기", "사무실 거름망 머그·개완·찻잔"),
    CUTLERY_SETS("수저·세트", "도자기 숟가락·받침·가성비 세트")
}

enum class PriceFilter(val label: String, val maxPrice: Int?) {
    ALL("전체 가격", null),
    UNDER_5K("5천원 이하", 5000),
    UNDER_10K("1만원 이하", 10000),
    UNDER_15K("1만5천원 이하", 15000)
}

enum class KilnRegion(
    val koreanName: String,
    val chineseName: String,
    val badgeText: String,
    val description: String
) {
    JINGDEZHEN(
        koreanName = "징더전 (경덕진)",
        chineseName = "景德镇",
        badgeText = "징더전 청화·백자",
        description = "1,000년 도자기 수도. 얇고 단단하며 푸른 코발트 문양이 선명한 매일 식기입니다."
    ),
    LONGQUAN(
        koreanName = "롱취안 (용천)",
        chineseName = "龙泉",
        badgeText = "롱취안 비취청자",
        description = "은은한 옥빛 유약이 음식을 정갈하게 돋보이게 하는 전통 청자 산지입니다."
    ),
    DEHUA(
        koreanName = "더화 (덕화)",
        chineseName = "德化",
        badgeText = "더화 크림백자",
        description = "따뜻한 우윳빛 색감과 부드러운 촉감으로 다기와 머그컵으로 유명한 산지입니다."
    ),
    YIXING(
        koreanName = "이싱·차오저우",
        chineseName = "宜兴·潮州",
        badgeText = "생활 도기·자사",
        description = "보온성이 뛰어나고 내구성이 좋아 매일 편하게 쓰기 좋은 실용 도자기 산지입니다."
    )
}

enum class GlazeStyle(val label: String, val colorHex: Long) {
    QINGHUA_BLUE("청화 코발트", 0xFF1E3A5F),
    CELADON_JADE("롱취안 옥빛청자", 0xFF568B78),
    DEHUA_WHITE("더화 우윳빛백자", 0xFFF5EFE4),
    TERRACOTTA_CLAY("이싱 자사 브라운", 0xFFB85A3A),
    STARRY_KILN("요변 밤하늘블루", 0xFF28466E),
    PASTEL_FLORAL("분채 산수국화", 0xFFD97F6C)
}

enum class VesselShape {
    RICE_BOWL,
    NOODLE_BOWL,
    PETAL_PLATE,
    DUMPLING_PLATTER,
    GAIWAN_SET,
    DAILY_MUG,
    SPOON_CHOPSTICK,
    TEAPOT_SET
}

data class CeramicProduct(
    val id: Int,
    val name: String,
    val subtitle: String,
    val category: CeramicCategory,
    val kilnRegion: KilnRegion,
    val price: Int,
    val originalPrice: Int,
    val rating: Float,
    val reviewCount: Int,
    val dimensions: String,
    val weightInfo: String,
    val microwaveSafe: Boolean,
    val dishwasherSafe: Boolean,
    val defaultGlaze: GlazeStyle,
    val availableGlazes: List<GlazeStyle>,
    val vesselShape: VesselShape,
    val bestTag: String?,
    val easyTip: String,
    val componentsDesc: String
)

data class TablewareBundlePreset(
    val id: Int,
    val title: String,
    val badge: String,
    val targetAudience: String,
    val description: String,
    val includedProductIds: List<Pair<Int, Int>>, // Pair(productId, count)
    val extraSetDiscountRate: Int // e.g., 20%
)

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productId: Int,
    val selectedGlaze: String,
    val quantity: Int,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "wishlist_items")
data class WishlistEntity(
    @PrimaryKey val productId: Int,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val orderId: Int = 0,
    val orderNumber: String,
    val itemsSummary: String,
    val totalItemCount: Int,
    val subtotalPrice: Int,
    val bundleDiscount: Int,
    val shippingFee: Int,
    val finalPrice: Int,
    val recipientName: String,
    val address: String,
    val paymentMethod: String,
    val deliveryStatus: String,
    val orderedAt: Long = System.currentTimeMillis()
)

object CeramicCatalog {
    val products: List<CeramicProduct> = listOf(
        CeramicProduct(
            id = 1,
            name = "징더전 청화 연꽃 데일리 밥공기",
            subtitle = "손에 착 감기는 가벼운 고백자 밥그릇",
            category = CeramicCategory.BOWLS,
            kilnRegion = KilnRegion.JINGDEZHEN,
            price = 3900,
            originalPrice = 5900,
            rating = 4.9f,
            reviewCount = 428,
            dimensions = "지름 11.5cm × 높이 6.0cm (300ml)",
            weightInfo = "약 185g · 손목 부담 없는 경량",
            microwaveSafe = true,
            dishwasherSafe = true,
            defaultGlaze = GlazeStyle.QINGHUA_BLUE,
            availableGlazes = listOf(GlazeStyle.QINGHUA_BLUE, GlazeStyle.DEHUA_WHITE, GlazeStyle.CELADON_JADE),
            vesselShape = VesselShape.RICE_BOWL,
            bestTag = "누적 1만개 돌파",
            easyTip = "굽이 높아 뜨거운 밥을 담아도 손이 뜨겁지 않고, 식기세척기에 겹쳐 넣기 딱 좋아요.",
            componentsDesc = "단품 1개 (3개 이상 구매 시 15% 자동 할인)"
        ),
        CeramicProduct(
            id = 2,
            name = "롱취안 비취청자 꽃잎 찬기·앞접시",
            subtitle = "반찬부터 디저트까지 어디나 어울리는 옥빛 접시",
            category = CeramicCategory.PLATES,
            kilnRegion = KilnRegion.LONGQUAN,
            price = 4500,
            originalPrice = 6800,
            rating = 4.8f,
            reviewCount = 312,
            dimensions = "지름 15.5cm × 높이 2.8cm",
            weightInfo = "약 210g · 가장자리 꽃잎 림",
            microwaveSafe = true,
            dishwasherSafe = true,
            defaultGlaze = GlazeStyle.CELADON_JADE,
            availableGlazes = listOf(GlazeStyle.CELADON_JADE, GlazeStyle.DEHUA_WHITE, GlazeStyle.PASTEL_FLORAL),
            vesselShape = VesselShape.PETAL_PLATE,
            bestTag = "식탁 분위기 업",
            easyTip = "국물이 살짝 있는 나물 반찬이나 만두, 과일 디저트를 담기 좋은 완만한 깊이감입니다.",
            componentsDesc = "단품 1개"
        ),
        CeramicProduct(
            id = 3,
            name = "징더전 청화 파도문 넉넉한 대면기",
            subtitle = "라면·칼국수·덮밥을 한 그릇에 담는 깊숙한 면기",
            category = CeramicCategory.BOWLS,
            kilnRegion = KilnRegion.JINGDEZHEN,
            price = 7900,
            originalPrice = 11500,
            rating = 4.9f,
            reviewCount = 519,
            dimensions = "지름 19.5cm × 높이 8.2cm (1,050ml)",
            weightInfo = "약 460g · 튼튼한 두께감",
            microwaveSafe = true,
            dishwasherSafe = true,
            defaultGlaze = GlazeStyle.QINGHUA_BLUE,
            availableGlazes = listOf(GlazeStyle.QINGHUA_BLUE, GlazeStyle.STARRY_KILN, GlazeStyle.CELADON_JADE),
            vesselShape = VesselShape.NOODLE_BOWL,
            bestTag = "혼밥 필수템",
            easyTip = "봉지라면 2개도 여유 있게 들어가며, 국물이 쉽게 식지 않는 고온 소성 도자기입니다.",
            componentsDesc = "대면기 단품 1개"
        ),
        CeramicProduct(
            id = 4,
            name = "더화 백자 거름망 뚜껑 오피스 티머그",
            subtitle = "찻잎 거름망과 받침 겸용 뚜껑이 포함된 3단 머그컵",
            category = CeramicCategory.TEAWARE,
            kilnRegion = KilnRegion.DEHUA,
            price = 8900,
            originalPrice = 13900,
            rating = 4.9f,
            reviewCount = 276,
            dimensions = "지름 8.5cm × 높이 11.2cm (380ml)",
            weightInfo = "약 340g (본체+세라믹 거름망+뚜껑)",
            microwaveSafe = true,
            dishwasherSafe = true,
            defaultGlaze = GlazeStyle.DEHUA_WHITE,
            availableGlazes = listOf(GlazeStyle.DEHUA_WHITE, GlazeStyle.CELADON_JADE, GlazeStyle.QINGHUA_BLUE),
            vesselShape = VesselShape.DAILY_MUG,
            bestTag = "직장인 추천",
            easyTip = "티백이나 잎차를 우린 뒤 뚜껑을 뒤집어 거름망 받침으로 쓰면 책상 위가 깔끔해요.",
            componentsDesc = "머그 본체 + 도자기 거름망 + 뚜껑 3종 세트"
        ),
        CeramicProduct(
            id = 5,
            name = "징더전 우동·국밥 우묵 도자기 숟가락 & 받침",
            subtitle = "입에 닿는 감촉이 매끈하고 국물 뜨기 편한 만두·우동 스푼",
            category = CeramicCategory.CUTLERY_SETS,
            kilnRegion = KilnRegion.JINGDEZHEN,
            price = 2900,
            originalPrice = 4500,
            rating = 4.8f,
            reviewCount = 640,
            dimensions = "길이 14.2cm × 폭 4.3cm + 물방울 받침",
            weightInfo = "약 55g · 숟가락 끝에 미끄럼 방지 홈",
            microwaveSafe = true,
            dishwasherSafe = true,
            defaultGlaze = GlazeStyle.QINGHUA_BLUE,
            availableGlazes = listOf(GlazeStyle.QINGHUA_BLUE, GlazeStyle.CELADON_JADE, GlazeStyle.PASTEL_FLORAL),
            vesselShape = VesselShape.SPOON_CHOPSTICK,
            bestTag = "2,900원 소확행",
            easyTip = "손잡이 끝에 작은 갈고리 홈이 있어 깊은 면기 안으로 숟가락이 빠지지 않습니다.",
            componentsDesc = "도자기 스푼 1P + 수저받침 1P"
        ),
        CeramicProduct(
            id = 6,
            name = "차오저우 만두·딤섬 소스칸 나눔 타원접시",
            subtitle = "간장 종지가 따로 필요 없는 일체형 만두·튀김 접시",
            category = CeramicCategory.PLATES,
            kilnRegion = KilnRegion.YIXING,
            price = 6500,
            originalPrice = 9500,
            rating = 4.9f,
            reviewCount = 198,
            dimensions = "가로 23.5cm × 세로 15.0cm × 높이 3.2cm",
            weightInfo = "약 390g · 소스칸 분리형",
            microwaveSafe = true,
            dishwasherSafe = true,
            defaultGlaze = GlazeStyle.DEHUA_WHITE,
            availableGlazes = listOf(GlazeStyle.DEHUA_WHITE, GlazeStyle.QINGHUA_BLUE, GlazeStyle.CELADON_JADE),
            vesselShape = VesselShape.DUMPLING_PLATTER,
            bestTag = "설거지 절약",
            easyTip = "군만두, 탕수육, 회, 전을 담고 한쪽 소스칸에 간장·초장을 바로 부어 설거지를 줄여보세요.",
            componentsDesc = "소스칸 일체형 타원접시 1개"
        ),
        CeramicProduct(
            id = 7,
            name = "더화 양지백옥 입문용 삼재 개완(盖碗) 다기",
            subtitle = "차 입문자도 손 데일 걱정 없이 쉽게 우리는 전통 개완",
            category = CeramicCategory.TEAWARE,
            kilnRegion = KilnRegion.DEHUA,
            price = 9900,
            originalPrice = 15000,
            rating = 4.8f,
            reviewCount = 154,
            dimensions = "지름 9.8cm × 높이 8.6cm (160ml)",
            weightInfo = "약 220g · 넓은 테두리 방열 설계",
            microwaveSafe = false,
            dishwasherSafe = true,
            defaultGlaze = GlazeStyle.DEHUA_WHITE,
            availableGlazes = listOf(GlazeStyle.DEHUA_WHITE, GlazeStyle.CELADON_JADE, GlazeStyle.PASTEL_FLORAL),
            vesselShape = VesselShape.GAIWAN_SET,
            bestTag = "홈티타임 입문",
            easyTip = "그릇 테두리가 밖으로 넓게 벌어져 있어 뜨거운 물을 부어도 손끝이 뜨겁지 않아요.",
            componentsDesc = "개완 본체 + 뚜껑 + 찻받침 3합 구성"
        ),
        CeramicProduct(
            id = 8,
            name = "요변(窑变) 밤하늘 유약 딥 파스타·카레 볼",
            subtitle = "가마 속 온도 변화로 완성된 은하수 같은 깊이감의 볼",
            category = CeramicCategory.PLATES,
            kilnRegion = KilnRegion.JINGDEZHEN,
            price = 8500,
            originalPrice = 12800,
            rating = 4.9f,
            reviewCount = 241,
            dimensions = "지름 21.0cm × 높이 4.8cm (680ml)",
            weightInfo = "약 430g · 도톰한 호텔식 림",
            microwaveSafe = true,
            dishwasherSafe = true,
            defaultGlaze = GlazeStyle.STARRY_KILN,
            availableGlazes = listOf(GlazeStyle.STARRY_KILN, GlazeStyle.CELADON_JADE, GlazeStyle.TERRACOTTA_CLAY),
            vesselShape = VesselShape.PETAL_PLATE,
            bestTag = "레스토랑 무드",
            easyTip = "카레라이스, 파스타, 마파두부처럼 소스가 넉넉한 요리를 담아내기 가장 좋은 깊이입니다.",
            componentsDesc = "딥 파스타볼 단품 1개"
        ),
        CeramicProduct(
            id = 9,
            name = "이싱 자사풍 휴대용 1~2인 여행 다기 세트",
            subtitle = "다관 1개와 찻잔 2개가 파우치에 쏙 들어가는 실속 세트",
            category = CeramicCategory.TEAWARE,
            kilnRegion = KilnRegion.YIXING,
            price = 14900,
            originalPrice = 22000,
            rating = 4.9f,
            reviewCount = 183,
            dimensions = "다관 180ml + 찻잔 55ml × 2개",
            weightInfo = "약 380g · 완충 전용 파우치 포함",
            microwaveSafe = false,
            dishwasherSafe = false,
            defaultGlaze = GlazeStyle.TERRACOTTA_CLAY,
            availableGlazes = listOf(GlazeStyle.TERRACOTTA_CLAY, GlazeStyle.CELADON_JADE, GlazeStyle.DEHUA_WHITE),
            vesselShape = VesselShape.TEAPOT_SET,
            bestTag = "선물 베스트",
            easyTip = "보이차, 우롱차, 녹차를 간편하게 우리기 좋으며 충격 방지 케이스가 함께 제공됩니다.",
            componentsDesc = "미니 다관 1P + 찻잔 2P + 여행 파우치"
        ),
        CeramicProduct(
            id = 10,
            name = "롱취안 매화청자 국공기·시리얼 볼",
            subtitle = "밥공기보다 한 치수 커서 국·요거트·시리얼로 활용 만점",
            category = CeramicCategory.BOWLS,
            kilnRegion = KilnRegion.LONGQUAN,
            price = 4900,
            originalPrice = 7500,
            rating = 4.8f,
            reviewCount = 290,
            dimensions = "지름 13.8cm × 높이 6.5cm (480ml)",
            weightInfo = "약 245g · 양손에 따뜻한 온기",
            microwaveSafe = true,
            dishwasherSafe = true,
            defaultGlaze = GlazeStyle.CELADON_JADE,
            availableGlazes = listOf(GlazeStyle.CELADON_JADE, GlazeStyle.QINGHUA_BLUE, GlazeStyle.DEHUA_WHITE),
            vesselShape = VesselShape.RICE_BOWL,
            bestTag = "만능 국그릇",
            easyTip = "한국 식탁의 국그릇 표준 사이즈로, 아침엔 시리얼·그릭요거트 볼로도 자주 손이 갑니다.",
            componentsDesc = "국공기 단품 1개"
        ),
        CeramicProduct(
            id = 11,
            name = "징더전 분채 복숭아꽃 디저트 미니 종지 4P 세트",
            subtitle = "간장·피클·견과류·티푸드를 담는 화사한 미니 찬기",
            category = CeramicCategory.CUTLERY_SETS,
            kilnRegion = KilnRegion.JINGDEZHEN,
            price = 7900,
            originalPrice = 12000,
            rating = 4.9f,
            reviewCount = 345,
            dimensions = "지름 9.2cm × 높이 2.5cm (4개 세트)",
            weightInfo = "개당 75g · 4개 세트 구성",
            microwaveSafe = true,
            dishwasherSafe = true,
            defaultGlaze = GlazeStyle.PASTEL_FLORAL,
            availableGlazes = listOf(GlazeStyle.PASTEL_FLORAL, GlazeStyle.QINGHUA_BLUE, GlazeStyle.CELADON_JADE),
            vesselShape = VesselShape.PETAL_PLATE,
            bestTag = "개당 1,970원꼴",
            easyTip = "작은 반찬이나 양념장, 다과를 조금씩 나눠 담으면 식탁이 한결 다채로워집니다.",
            componentsDesc = "미니 찬기 4개 1세트"
        ),
        CeramicProduct(
            id = 12,
            name = "자취·신혼 1인 완비 6피스 올인원 식기 세트",
            subtitle = "밥공기·국공기·대면기·찬기·타원접시·스푼을 한 번에!",
            category = CeramicCategory.CUTLERY_SETS,
            kilnRegion = KilnRegion.JINGDEZHEN,
            price = 18900,
            originalPrice = 28500,
            rating = 5.0f,
            reviewCount = 412,
            dimensions = "밥공기+국공기+면기+찬기+만두접시+스푼 (총 6종)",
            weightInfo = "전 구성 전자레인지·식기세척기 사용 가능",
            microwaveSafe = true,
            dishwasherSafe = true,
            defaultGlaze = GlazeStyle.QINGHUA_BLUE,
            availableGlazes = listOf(GlazeStyle.QINGHUA_BLUE, GlazeStyle.CELADON_JADE, GlazeStyle.DEHUA_WHITE),
            vesselShape = VesselShape.NOODLE_BOWL,
            bestTag = "초특가 올인원",
            easyTip = "어떤 그릇을 사야 할지 고민될 때 이 세트 하나면 1인 식탁 준비가 바로 끝납니다.",
            componentsDesc = "1인 필수 도자기 6종 풀세트"
        )
    )

    val bundlePresets: List<TablewareBundlePreset> = listOf(
        TablewareBundlePreset(
            id = 101,
            title = "자취 1인 든든 한끼 세트",
            badge = "가장 많이 찾는 1인 구성",
            targetAudience = "자취생 · 1인 가구 · 기숙사",
            description = "밥공기, 국공기, 만능 면기, 꽃잎 앞접시, 도자기 스푼까지 매일 쓰는 5종 알짜 구성",
            includedProductIds = listOf(1 to 1, 10 to 1, 3 to 1, 2 to 1, 5 to 1),
            extraSetDiscountRate = 20
        ),
        TablewareBundlePreset(
            id = 102,
            title = "2인 신혼·커플 주 주말 식탁 세트",
            badge = "커플 홈다이닝 추천",
            targetAudience = "2인 가구 · 신혼부부 · 집들이 선물",
            description = "2인용 밥·국그릇 세트에 만두 나눔접시와 파스타볼, 스푼 2세트까지 갖춘 10종 구성",
            includedProductIds = listOf(1 to 2, 10 to 2, 2 to 2, 6 to 1, 8 to 1, 5 to 2),
            extraSetDiscountRate = 22
        ),
        TablewareBundlePreset(
            id = 103,
            title = "책상 위 힐링 홈티타임 세트",
            badge = "차를 좋아하는 분께",
            targetAudience = "사무실 티타임 · 다도 입문자",
            description = "거름망 머그컵과 입문용 개완, 다과를 담을 미니 종지 4P까지 포함된 티타임 풀세트",
            includedProductIds = listOf(4 to 1, 7 to 1, 11 to 1),
            extraSetDiscountRate = 18
        ),
        TablewareBundlePreset(
            id = 104,
            title = "4인 온가족 넉넉 반상 세트",
            badge = "최대 25% 특별 할인",
            targetAudience = "3~4인 가족 · 손님 초대용",
            description = "온 가족 밥공기·국공기 4조와 대면기 2개, 꽃잎 접시 4개, 만두접시 2개로 식탁을 통일해보세요",
            includedProductIds = listOf(1 to 4, 10 to 4, 2 to 4, 3 to 2, 6 to 2),
            extraSetDiscountRate = 25
        )
    )

    fun getProductById(id: Int): CeramicProduct? = products.find { it.id == id }
}
