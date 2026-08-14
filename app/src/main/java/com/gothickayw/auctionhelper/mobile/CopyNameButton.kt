package com.gothickayw.auctionhelper.mobile

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

private fun copyToClipboard(context: Context, text: String, message: String) {
    val value = text.trim()
    if (value.isBlank()) return
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText("YoWorld item name", value))
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

@Composable
fun CopyNameButton(name: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    IconButton(
        modifier = modifier,
        onClick = { copyToClipboard(context, name, "Item name copied") },
    ) {
        Icon(Icons.Default.ContentCopy, contentDescription = "Copy item name")
    }
}

@Composable
fun CopyAllNamesButton(names: List<String>, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val cleanNames = names.map { it.trim() }.filter { it.isNotBlank() }
    OutlinedButton(
        modifier = modifier.fillMaxWidth(),
        enabled = cleanNames.isNotEmpty(),
        onClick = {
            copyToClipboard(
                context,
                cleanNames.joinToString("\n"),
                "${cleanNames.size} item names copied",
            )
        },
    ) {
        Icon(Icons.Default.ContentCopy, contentDescription = null)
        Text("Copy all item names")
    }
}
