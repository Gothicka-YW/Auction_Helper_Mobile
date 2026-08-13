package com.gothickayw.auctionhelper.mobile

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun SearchSection(
    ui: SearchState,
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onManual: () -> Unit,
    onItem: (YoWorldSearchResult) -> Unit,
    onBundle: (YoWorldSearchResult) -> Unit,
) {
    SectionCard("YOWORLD.INFO", "Find Auction Items") {
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                label = { Text("Search items") },
                modifier = Modifier.weight(1f),
                singleLine = true,
            )
            IconButton(onClick = onSearch) { Icon(Icons.Default.Search, "Search") }
        }
        if (ui.loading) Text("Searching…")
        ui.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        ui.results.forEach { result ->
            SearchRow(result, onItem = { onItem(result) }, onBundle = { onBundle(result) })
        }
        OutlinedButton(onClick = onManual, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.Add, null)
            Text("Manual Item")
        }
    }
}
