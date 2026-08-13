package com.gothickayw.auctionhelper.mobile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun BundleSection(
    draft: List<BundleItem>,
    onRemove: (String) -> Unit,
    onClear: () -> Unit,
    onCreate: () -> Unit,
) {
    SectionCard("BUNDLE DRAFT", "${draft.size} items") {
        draft.forEach { item ->
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                ItemBadge(item.name, Modifier.size(40.dp))
                Text(
                    item.name,
                    modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                IconButton(onClick = { onRemove(item.id) }) { Icon(Icons.Default.Close, "Remove") }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = onClear, modifier = Modifier.weight(1f)) { Text("Clear") }
            Button(onClick = onCreate, enabled = draft.size >= 2, modifier = Modifier.weight(1f)) {
                Text("Create Bundle")
            }
        }
    }
}
