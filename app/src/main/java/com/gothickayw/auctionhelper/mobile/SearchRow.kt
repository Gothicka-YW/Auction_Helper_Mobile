package com.gothickayw.auctionhelper.mobile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun SearchRow(
    result: YoWorldSearchResult,
    onItem: () -> Unit,
    onBundle: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = .45f), RoundedCornerShape(12.dp))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ItemBadge(result.name, result.iconUrl)
        Column(modifier = Modifier.weight(1f).padding(horizontal = 8.dp)) {
            Text(result.name, fontWeight = FontWeight.Bold, maxLines = 2)
            Text("ID ${result.id}", style = MaterialTheme.typography.labelSmall)
        }
        Column {
            TextButton(onClick = onItem) { Text("Item") }
            TextButton(onClick = onBundle) { Text("Bundle") }
        }
    }
}
