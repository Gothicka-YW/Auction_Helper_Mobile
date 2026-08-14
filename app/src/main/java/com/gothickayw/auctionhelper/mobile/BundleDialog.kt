package com.gothickayw.auctionhelper.mobile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun BundleDialog(
    items: List<BundleItem>,
    onDismiss: () -> Unit,
    onSave: (String, Long, String) -> Unit,
) {
    val count = items.size
    var name by remember { mutableStateOf("Bundle ($count items)") }
    var reserve by remember { mutableStateOf("") }
    var selectedId by remember(items) { mutableStateOf(items.firstOrNull()?.id.orEmpty()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Bundle Auction") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("$count items will be sold as one auction lot.")
                OutlinedTextField(name, { name = it }, label = { Text("Bundle name") })
                OutlinedTextField(reserve, { reserve = it }, label = { Text("Reserve / Starting Bid") })
                CopyAllNamesButton(items.map { it.name })
                Text("Bundle cover icon", style = MaterialTheme.typography.labelLarge)
                items.forEach { item ->
                    val selected = item.id == selectedId
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (selected) MaterialTheme.colorScheme.primary.copy(alpha = .14f)
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = .45f),
                                RoundedCornerShape(12.dp),
                            )
                            .clickable { selectedId = item.id }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        ItemBadge(item.name, item.iconUrl, Modifier.size(40.dp))
                        Text(
                            item.name,
                            modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        CopyNameButton(item.name)
                        RadioButton(selected = selected, onClick = { selectedId = item.id })
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = parseCoins(reserve) != null && items.size >= 2,
                onClick = {
                    val selectedIcon = items.firstOrNull { it.id == selectedId }?.iconUrl
                        ?: items.firstOrNull()?.iconUrl.orEmpty()
                    onSave(name.trim(), parseCoins(reserve) ?: 0L, selectedIcon)
                },
            ) { Text("Create") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}
