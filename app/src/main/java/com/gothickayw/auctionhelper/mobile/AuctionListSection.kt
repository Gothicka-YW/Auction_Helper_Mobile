package com.gothickayw.auctionhelper.mobile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun AuctionListSection(lots: List<AuctionLot>, onDelete: (AuctionLot) -> Unit) {
    SectionCard("AUCTION LIST", "All Auction Lots") {
        if (lots.isEmpty()) Text("No lots yet.")
        lots.forEach { lot ->
            Row(
                modifier = Modifier.fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = .45f), RoundedCornerShape(12.dp))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ItemBadge(lot.name, lot.iconUrl, Modifier.size(44.dp))
                Column(modifier = Modifier.weight(1f).padding(horizontal = 8.dp)) {
                    Text(lot.name, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text("${lot.status.wire} · reserve ${formatCoins(lot.reserve)}",
                        style = MaterialTheme.typography.labelSmall)
                }
                CopyNameButton(lot.name)
                IconButton(onClick = { onDelete(lot) }) { Icon(Icons.Default.Delete, "Delete") }
            }
        }
    }
}
