package com.gothickayw.auctionhelper.mobile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun AuctionScreen(viewModel: AuctionViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val search by viewModel.search.collectAsStateWithLifecycle()
    val draft by viewModel.bundleDraft.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var query by remember { mutableStateOf("") }
    var manualOpen by remember { mutableStateOf(false) }
    var bundleOpen by remember { mutableStateOf(false) }
    var pricedResult by remember { mutableStateOf<YoWorldSearchResult?>(null) }
    var completeLot by remember { mutableStateOf<AuctionLot?>(null) }
    var importMessage by remember { mutableStateOf<String?>(null) }

    val exportJson = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        uri?.let { writeText(context, it, AuctionCodec.encodeState(state, pretty = true)) }
    }
    val exportCsv = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/csv")) { uri ->
        uri?.let { writeText(context, it, "\uFEFF" + AuctionCodec.toCsv(state)) }
    }
    val importJson = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let {
            runCatching { readText(context, it) }
                .onSuccess { raw ->
                    viewModel.importJson(raw) { result ->
                        importMessage = if (result.isSuccess) "Backup imported."
                        else "Import failed: ${result.exceptionOrNull()?.message ?: "Unknown error"}"
                    }
                }
                .onFailure { importMessage = "Import failed: ${it.message}" }
        }
    }

    val ready = state.items.filter { it.status == LotStatus.READY }
    val current = ready.firstOrNull()
    val reserveTotal = state.items.sumOf {
        it.reserve * if (it.lotType == LotType.BUNDLE) 1L else it.quantity.toLong()
    }
    val actualSales = state.items.filter { it.status == LotStatus.SOLD }.sumOf { it.soldPrice }

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Spacer(Modifier.height(8.dp))
                TopBar(
                    onExportJson = { exportJson.launch("Auction_Helper_Backup.json") },
                    onExportCsv = { exportCsv.launch("Auction_Helper_Data.csv") },
                    onImportJson = { importJson.launch(arrayOf("application/json", "text/plain")) },
                )
            }
            item {
                SectionCard("AUCTION", state.auctionTitle) {
                    var title by remember(state.auctionTitle) { mutableStateOf(state.auctionTitle) }
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Auction title") },
                        trailingIcon = {
                            IconButton(onClick = { viewModel.setTitle(title) }) {
                                Icon(Icons.Default.Check, "Save title")
                            }
                        },
                        singleLine = true,
                    )
                }
            }
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item { StatCard("Remaining", ready.size.toString()) }
                    item { StatCard("Sold", state.items.count { it.status == LotStatus.SOLD }.toString()) }
                    item { StatCard("Reserve", formatCoins(reserveTotal)) }
                    item { StatCard("Sales", formatCoins(actualSales)) }
                }
            }
            item {
                SearchSection(
                    ui = search,
                    query = query,
                    onQueryChange = { query = it },
                    onSearch = { viewModel.search(query) },
                    onManual = { manualOpen = true },
                    onItem = { pricedResult = it },
                    onBundle = viewModel::addToBundleDraft,
                )
            }
            if (draft.isNotEmpty()) {
                item {
                    BundleSection(
                        draft = draft,
                        onRemove = viewModel::removeDraft,
                        onClear = viewModel::clearDraft,
                        onCreate = { bundleOpen = true },
                    )
                }
            }
            item {
                CurrentSection(
                    lot = current,
                    onComplete = { if (current != null) completeLot = current },
                    onNoSale = { if (current != null) viewModel.noSale(current) },
                    onSkip = { if (current != null) viewModel.skip(current) },
                )
            }
            item {
                SectionCard("QUEUE", "Next Up") {
                    val next = ready.drop(1).take(3)
                    if (next.isEmpty()) Text("Nothing waiting.")
                    next.forEach { LotSummary(it, compact = true) }
                }
            }
            item { AuctionListSection(state.items, viewModel::delete) }
            item {
                Text(
                    "Manual organizer only · YoWorld.info search only · No YoWorld gameplay automation.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 24.dp),
                )
            }
        }
    }

    if (manualOpen) {
        ManualLotDialog(
            onDismiss = { manualOpen = false },
            onSave = { name, reserve, quantity, notes ->
                viewModel.addManual(name, reserve, quantity, notes)
                manualOpen = false
            },
        )
    }
    pricedResult?.let { result ->
        PriceDialog(
            title = result.name,
            onDismiss = { pricedResult = null },
            onSave = { reserve ->
                viewModel.addSearchItem(result, reserve)
                pricedResult = null
            },
        )
    }
    if (bundleOpen) {
        BundleDialog(
            count = draft.size,
            onDismiss = { bundleOpen = false },
            onSave = { name, reserve ->
                viewModel.createBundle(name, reserve)
                bundleOpen = false
            },
        )
    }
    completeLot?.let { lot ->
        CompleteLotDialog(
            lot = lot,
            onDismiss = { completeLot = null },
            onSave = { winner, price ->
                viewModel.sold(lot, winner, price)
                completeLot = null
            },
        )
    }
    importMessage?.let { message ->
        AlertDialog(
            onDismissRequest = { importMessage = null },
            title = { Text("Auction Helper") },
            text = { Text(message) },
            confirmButton = { TextButton(onClick = { importMessage = null }) { Text("OK") } },
        )
    }
}
