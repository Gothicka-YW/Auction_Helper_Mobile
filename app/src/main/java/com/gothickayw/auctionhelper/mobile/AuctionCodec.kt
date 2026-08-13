package com.gothickayw.auctionhelper.mobile

import org.json.JSONArray
import org.json.JSONObject
import java.time.Instant
import java.util.UUID

object AuctionCodec {
    fun encodeState(state: AuctionState, pretty: Boolean = false): String {
        val stateJson = JSONObject().apply {
            put("auctionTitle", state.auctionTitle)
            put("filter", "all")
            put("bundleDraft", JSONArray())
            put("schemaVersion", 1)
            put("items", JSONArray().apply {
                state.items.forEach { put(encodeLot(it)) }
            })
        }

        val root = JSONObject().apply {
            put("format", "Auction Helper Backup")
            put("version", 4)
            put("exportedAt", Instant.now().toString())
            put("state", stateJson)
        }
        return if (pretty) root.toString(2) else root.toString()
    }

    fun decodeState(raw: String): AuctionState {
        val root = JSONObject(raw)
        val stateJson = root.optJSONObject("state") ?: root
        val itemsJson = stateJson.optJSONArray("items") ?: JSONArray()
        val items = buildList {
            for (i in 0 until itemsJson.length()) {
                add(decodeLot(itemsJson.getJSONObject(i)))
            }
        }
        return AuctionState(
            auctionTitle = stateJson.optString("auctionTitle", "Weekend Closed Auction"),
            items = items,
        )
    }

    fun toCsv(state: AuctionState): String {
        val headers = listOf(
            "Auction", "Order", "Lot Type", "Lot Name", "Bundle Item Count",
            "Bundle Contents", "Quantity", "Reserve / Starting Bid", "Status",
            "Buyer", "Sold Price", "Difference From Reserve", "Notes", "YoWorld Item ID"
        )
        val lines = mutableListOf(headers.joinToString(",") { csvCell(it) })

        state.items.forEachIndexed { index, lot ->
            val row = listOf(
                state.auctionTitle,
                (index + 1).toString(),
                if (lot.lotType == LotType.BUNDLE) "Bundle" else "Single Item",
                lot.name,
                if (lot.lotType == LotType.BUNDLE) lot.bundleItems.size.toString() else "",
                if (lot.lotType == LotType.BUNDLE) lot.bundleItems.joinToString(" | ") { it.name } else "",
                if (lot.lotType == LotType.BUNDLE) "1" else lot.quantity.toString(),
                lot.reserve.toString(),
                lot.status.wire,
                lot.buyer,
                if (lot.status == LotStatus.SOLD) lot.soldPrice.toString() else "",
                if (lot.status == LotStatus.SOLD) (lot.soldPrice - lot.reserve).toString() else "",
                lot.notes,
                if (lot.lotType == LotType.SINGLE) lot.sourceItemId else "",
            )
            lines += row.joinToString(",") { csvCell(it) }
        }
        return lines.joinToString("\r\n")
    }

    private fun csvCell(value: String): String =
        if (value.any { it == ',' || it == '"' || it == '\n' || it == '\r' }) {
            "\"" + value.replace("\"", "\"\"") + "\""
        } else value

    private fun encodeLot(lot: AuctionLot) = JSONObject().apply {
        put("id", lot.id)
        put("lotType", lot.lotType.wire)
        put("name", lot.name)
        put("reserve", lot.reserve)
        put("quantity", if (lot.lotType == LotType.BUNDLE) 1 else lot.quantity)
        put("notes", lot.notes)
        put("icon", lot.iconUrl)
        put("sourceItemId", lot.sourceItemId)
        put("bundleItems", JSONArray().apply {
            lot.bundleItems.forEach { item ->
                put(JSONObject().apply {
                    put("draftId", item.id)
                    put("name", item.name)
                    put("icon", item.iconUrl)
                    put("sourceItemId", item.sourceItemId)
                })
            }
        })
        put("status", lot.status.wire)
        put("buyer", lot.buyer)
        put("soldPrice", lot.soldPrice)
        put("createdAt", Instant.ofEpochMilli(lot.createdAt).toString())
        put("completedAt", lot.completedAt?.let { Instant.ofEpochMilli(it).toString() })
    }

    private fun decodeLot(json: JSONObject): AuctionLot {
        val bundleJson = json.optJSONArray("bundleItems") ?: JSONArray()
        val bundle = buildList {
            for (i in 0 until bundleJson.length()) {
                val item = bundleJson.getJSONObject(i)
                add(
                    BundleItem(
                        id = item.optString("draftId").ifBlank { UUID.randomUUID().toString() },
                        name = item.optString("name", "Item"),
                        iconUrl = item.optString("icon"),
                        sourceItemId = item.optString("sourceItemId"),
                    )
                )
            }
        }
        return AuctionLot(
            id = json.optString("id").ifBlank { UUID.randomUUID().toString() },
            lotType = LotType.fromWire(json.optString("lotType")),
            name = json.optString("name", "Auction Lot"),
            reserve = json.optLong("reserve", 0L),
            quantity = json.optInt("quantity", 1).coerceAtLeast(1),
            notes = json.optString("notes"),
            iconUrl = json.optString("icon"),
            sourceItemId = json.optString("sourceItemId"),
            bundleItems = bundle,
            status = LotStatus.fromWire(json.optString("status")),
            buyer = json.optString("buyer"),
            soldPrice = json.optLong("soldPrice", 0L),
            createdAt = parseInstant(json.optString("createdAt")) ?: System.currentTimeMillis(),
            completedAt = parseInstant(json.optString("completedAt")),
        )
    }

    private fun parseInstant(value: String?): Long? =
        value?.takeIf { it.isNotBlank() && it != "null" }?.let {
            runCatching { Instant.parse(it).toEpochMilli() }.getOrNull()
        }
}
