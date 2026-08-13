package com.gothickayw.auctionhelper.mobile

enum class LotType(val wire: String) {
    SINGLE("single"), BUNDLE("bundle");
    companion object {
        fun fromWire(value: String?) = entries.firstOrNull { it.wire == value } ?: SINGLE
    }
}

enum class LotStatus(val wire: String) {
    READY("ready"), SOLD("sold"), NO_SALE("no-sale");
    companion object {
        fun fromWire(value: String?) = entries.firstOrNull { it.wire == value } ?: READY
    }
}

data class BundleItem(
    val id: String,
    val name: String,
    val iconUrl: String = "",
    val sourceItemId: String = "",
)

data class AuctionLot(
    val id: String,
    val lotType: LotType,
    val name: String,
    val reserve: Long,
    val quantity: Int = 1,
    val notes: String = "",
    val iconUrl: String = "",
    val sourceItemId: String = "",
    val bundleItems: List<BundleItem> = emptyList(),
    val status: LotStatus = LotStatus.READY,
    val buyer: String = "",
    val soldPrice: Long = 0L,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
)

data class AuctionState(
    val auctionTitle: String = "Weekend Closed Auction",
    val items: List<AuctionLot> = emptyList(),
)

data class YoWorldSearchResult(
    val id: String,
    val name: String,
    val activeInStore: Boolean,
    val iconUrl: String,
)
