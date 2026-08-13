package com.gothickayw.auctionhelper.mobile

import java.text.DecimalFormat

fun parseCoins(raw: String): Long? {
    val cleaned = raw.trim().lowercase().replace(",", "").replace(" ", "")
    if (cleaned.isBlank()) return 0L
    val suffix = cleaned.lastOrNull()?.takeIf { it in "kmbt" }
    val number = if (suffix == null) cleaned else cleaned.dropLast(1)
    val base = number.toDoubleOrNull() ?: return null
    val multiplier = when (suffix) {
        'k' -> 1_000.0
        'm' -> 1_000_000.0
        'b' -> 1_000_000_000.0
        't' -> 1_000_000_000_000.0
        else -> 1.0
    }
    return (base * multiplier).toLong()
}

fun formatCoins(value: Long): String {
    val abs = kotlin.math.abs(value.toDouble())
    val options = listOf(
        1_000_000_000_000.0 to "t",
        1_000_000_000.0 to "b",
        1_000_000.0 to "m",
        1_000.0 to "k",
    )
    for ((size, suffix) in options) {
        if (abs >= size) {
            val scaled = value / size
            val pattern = when {
                kotlin.math.abs(scaled) >= 100 -> "0"
                kotlin.math.abs(scaled) >= 10 -> "0.#"
                else -> "0.##"
            }
            return DecimalFormat(pattern).format(scaled) + suffix
        }
    }
    return DecimalFormat("#,##0").format(value)
}
