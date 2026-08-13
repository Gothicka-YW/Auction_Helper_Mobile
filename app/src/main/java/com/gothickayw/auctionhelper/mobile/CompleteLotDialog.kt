package com.gothickayw.auctionhelper.mobile

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

@Composable
fun CompleteLotDialog(lot: AuctionLot, onDismiss: () -> Unit, onSave: (String, Long) -> Unit) {
    var winner by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Complete Auction Lot") },
        text = {
            Column {
                Text(lot.name)
                OutlinedTextField(winner, { winner = it }, label = { Text("Winner") })
                OutlinedTextField(amount, { amount = it }, label = { Text("Final price") })
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(winner.trim(), parseCoins(amount) ?: 0L) }) { Text("Complete") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}
