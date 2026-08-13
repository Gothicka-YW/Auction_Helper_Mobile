package com.gothickayw.auctionhelper.mobile

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URLEncoder
import java.net.URL
import java.nio.charset.StandardCharsets

class YoWorldInfo {
    suspend fun search(query: String): List<YoWorldSearchResult> = withContext(Dispatchers.IO) {
        val encoded = URLEncoder.encode(query.trim(), StandardCharsets.UTF_8.toString())
        val endpoint = "https://api.yoworld.info/api/items/search?query=$encoded&page=1&itemsPerPage=12&itemCategoryId=-1"
        val connection = (URL(endpoint).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 10_000
            readTimeout = 15_000
            setRequestProperty("Accept", "application/json")
        }
        try {
            if (connection.responseCode !in 200..299) {
                error("YoWorld.info returned HTTP ${connection.responseCode}")
            }
            val body = connection.inputStream.bufferedReader().use { it.readText() }
            val data = JSONObject(body)
                .getJSONObject("data")
                .getJSONObject("pagination")
                .getJSONArray("data")

            buildList {
                for (i in 0 until data.length()) {
                    val item = data.getJSONObject(i)
                    val id = item.optLong("id").toString()
                    if (id == "0") continue
                    add(
                        YoWorldSearchResult(
                            id = id,
                            name = item.optString("name", "Unnamed item"),
                            activeInStore = item.optBoolean("active_in_store", false),
                            iconUrl = iconUrl(id),
                        )
                    )
                }
            }
        } finally {
            connection.disconnect()
        }
    }

    companion object {
        fun iconUrl(itemId: String): String {
            val id = itemId.toLongOrNull() ?: return ""
            val g1 = (id / 10_000).toString().padStart(2, '0')
            val g2 = ((id % 10_000) / 100).toString().padStart(2, '0')
            return "https://yw-web.yoworld.com/cdn/items/$g1/$g2/$id/$id.png"
        }
    }
}
