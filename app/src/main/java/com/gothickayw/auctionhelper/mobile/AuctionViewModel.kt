package com.gothickayw.auctionhelper.mobile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

data class SearchState(
    val loading: Boolean = false,
    val results: List<YoWorldSearchResult> = emptyList(),
    val error: String? = null,
)

class AuctionViewModel(application: Application) : AndroidViewModel(application) {
    private val store = AuctionStore(application)
    private val yoWorld = YoWorldInfo()

    val state: StateFlow<AuctionState> = store.state.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        AuctionState(),
    )

    private val _search = MutableStateFlow(SearchState())
    val search: StateFlow<SearchState> = _search.asStateFlow()

    private val _bundleDraft = MutableStateFlow<List<BundleItem>>(emptyList())
    val bundleDraft: StateFlow<List<BundleItem>> = _bundleDraft.asStateFlow()

    fun setTitle(title: String) = mutate { it.copy(auctionTitle = title.ifBlank { "Weekend Closed Auction" }) }

    fun addManual(name: String, reserve: Long, quantity: Int, notes: String) = mutate { current ->
        current.copy(
            items = current.items + AuctionLot(
                id = UUID.randomUUID().toString(),
                lotType = LotType.SINGLE,
                name = name,
                reserve = reserve,
                quantity = quantity.coerceAtLeast(1),
                notes = notes,
            )
        )
    }

    fun addSearchItem(result: YoWorldSearchResult, reserve: Long) = mutate { current ->
        current.copy(
            items = current.items + AuctionLot(
                id = UUID.randomUUID().toString(),
                lotType = LotType.SINGLE,
                name = result.name,
                reserve = reserve,
                iconUrl = result.iconUrl,
                sourceItemId = result.id,
            )
        )
    }

    fun addToBundleDraft(result: YoWorldSearchResult) {
        _bundleDraft.value = _bundleDraft.value + BundleItem(
            id = UUID.randomUUID().toString(),
            name = result.name,
            iconUrl = result.iconUrl,
            sourceItemId = result.id,
        )
    }

    fun removeDraft(id: String) {
        _bundleDraft.value = _bundleDraft.value.filterNot { it.id == id }
    }

    fun clearDraft() {
        _bundleDraft.value = emptyList()
    }

    fun createBundle(name: String, reserve: Long) {
        val draft = _bundleDraft.value
        if (draft.size < 2) return
        mutate { current ->
            current.copy(
                items = current.items + AuctionLot(
                    id = UUID.randomUUID().toString(),
                    lotType = LotType.BUNDLE,
                    name = name.ifBlank { "Bundle (${draft.size} items)" },
                    reserve = reserve,
                    iconUrl = draft.firstOrNull()?.iconUrl.orEmpty(),
                    bundleItems = draft,
                )
            )
        }
        clearDraft()
    }

    fun sold(lot: AuctionLot, buyer: String, price: Long) = mutate { current ->
        current.copy(items = current.items.map {
            if (it.id == lot.id) it.copy(
                status = LotStatus.SOLD,
                buyer = buyer,
                soldPrice = price,
                completedAt = System.currentTimeMillis(),
            ) else it
        })
    }

    fun noSale(lot: AuctionLot) = mutate { current ->
        current.copy(items = current.items.map {
            if (it.id == lot.id) it.copy(
                status = LotStatus.NO_SALE,
                completedAt = System.currentTimeMillis(),
            ) else it
        })
    }

    fun skip(lot: AuctionLot) = mutate { current ->
        val remaining = current.items.filterNot { it.id == lot.id }
        current.copy(items = remaining + lot)
    }

    fun delete(lot: AuctionLot) = mutate { current ->
        current.copy(items = current.items.filterNot { it.id == lot.id })
    }

    fun search(query: String) {
        if (query.isBlank()) return
        viewModelScope.launch {
            _search.value = SearchState(loading = true)
            runCatching { yoWorld.search(query) }
                .onSuccess { _search.value = SearchState(results = it) }
                .onFailure { _search.value = SearchState(error = it.message ?: "Search failed") }
        }
    }

    fun importJson(raw: String, done: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val result = runCatching { store.save(AuctionCodec.decodeState(raw)) }
            done(result)
        }
    }

    private fun mutate(block: (AuctionState) -> AuctionState) {
        viewModelScope.launch {
            store.save(block(state.value))
        }
    }
}
