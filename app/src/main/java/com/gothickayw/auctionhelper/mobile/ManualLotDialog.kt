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
fun ManualLotDialog(onDismiss: () -> Unit, onSave: (String, Long, Int, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var reserve by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("1") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Manual Item") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(name, { name = it }, label = { Text("Item name") })
                OutlinedTextField(reserve, { reserve = it }, label = { Text("Reserve / Starting Bid") })
                OutlinedTextField(quantity, { quantity = it }, label = { Text("Quantity") })
                OutlinedTextField(notes, { notes = it }, label = { Text("Notes") })
            }
        },
        confirmButton = {
            TextButton(
                enabled = name.isNotBlank() && parseCoins(reserve) != null,
                onClick = {
                    onSave(
                        name.trim(),
                        parseCoins(reserve) ?: 0L,
                        quantity.toIntOrNull()?.coerceAtLeast(1) ?: 1,
                        notes.trim(),
                    )
                },
            ) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}
