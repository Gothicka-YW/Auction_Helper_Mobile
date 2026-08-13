package com.gothickayw.auctionhelper.mobile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp

@Composable
fun BundleDialog(count: Int, onDismiss: () -> Unit, onSave: (String, Long) -> Unit) {
    var name by remember { mutableStateOf("Bundle ($count items)") }
    var reserve by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Bundle Auction") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("$count items will be sold as one auction lot.")
                OutlinedTextField(name, { name = it }, label = { Text("Bundle name") })
                OutlinedTextField(reserve, { reserve = it }, label = { Text("Reserve / Starting Bid") })
            }
        },
        confirmButton = {
            TextButton(
                enabled = parseCoins(reserve) != null,
                onClick = { onSave(name.trim(), parseCoins(reserve) ?: 0L) },
            ) { Text("Create") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}
