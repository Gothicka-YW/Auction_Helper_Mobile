package com.gothickayw.auctionhelper.mobile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun LotSummary(lot: AuctionLot, compact: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        ItemBadge(lot.name, Modifier.size(if (compact) 42.dp else 68.dp))
        Column(modifier = Modifier.weight(1f).padding(start = 10.dp)) {
            Text(lot.name, fontWeight = FontWeight.Black)
            Text(
                if (lot.lotType == LotType.BUNDLE) "Bundle · ${lot.bundleItems.size} items"
                else "Single Item · Qty ${lot.quantity}",
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.labelMedium,
            )
            Text("Reserve / Start ${formatCoins(lot.reserve)}")
            if (!compact && lot.lotType == LotType.BUNDLE) {
                Text(
                    lot.bundleItems.joinToString(" · ") { it.name },
                    maxLines = 3, overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
