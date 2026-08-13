package com.gothickayw.auctionhelper.mobile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight

@Composable
fun TopBar(onExportJson: () -> Unit, onExportCsv: () -> Unit, onImportJson: () -> Unit) {
    var menuOpen by remember { mutableStateOf(false) }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text("GOTHICKA'S", color = MaterialTheme.colorScheme.tertiary,
                style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            Text("Auction Helper", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
            Text("MOBILE", color = MaterialTheme.colorScheme.secondary, style = MaterialTheme.typography.labelSmall)
        }
        IconButton(onClick = { menuOpen = true }) { Icon(Icons.Default.MoreVert, "Menu") }
        DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
            DropdownMenuItem(
                text = { Text("Export JSON Backup") },
                leadingIcon = { Icon(Icons.Default.Download, null) },
                onClick = { menuOpen = false; onExportJson() },
            )
            DropdownMenuItem(
                text = { Text("Export Auction Data (CSV)") },
                leadingIcon = { Icon(Icons.Default.Download, null) },
                onClick = { menuOpen = false; onExportCsv() },
            )
            DropdownMenuItem(
                text = { Text("Import JSON Backup") },
                leadingIcon = { Icon(Icons.Default.UploadFile, null) },
                onClick = { menuOpen = false; onImportJson() },
            )
        }
    }
}
