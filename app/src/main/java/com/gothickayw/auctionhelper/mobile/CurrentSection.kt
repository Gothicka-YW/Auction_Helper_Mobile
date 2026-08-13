package com.gothickayw.auctionhelper.mobile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CurrentSection(
    lot: AuctionLot?,
    onComplete: () -> Unit,
    onNoSale: () -> Unit,
    onSkip: () -> Unit,
) {
    SectionCard("ON THE BLOCK", "Current Lot") {
        if (lot == null) {
            Text("No ready auction lots.")
        } else {
            LotSummary(lot)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onComplete, modifier = Modifier.weight(1f)) { Text("Sold") }
                FilledTonalButton(onClick = onNoSale, modifier = Modifier.weight(1f)) { Text("No Sale") }
                OutlinedButton(onClick = onSkip, modifier = Modifier.weight(1f)) { Text("Skip") }
            }
        }
    }
}
