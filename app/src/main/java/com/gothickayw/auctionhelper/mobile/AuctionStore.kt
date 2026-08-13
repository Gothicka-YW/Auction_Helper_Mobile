package com.gothickayw.auctionhelper.mobile

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.auctionDataStore by preferencesDataStore(name = "auction_helper_mobile")

class AuctionStore(private val context: Context) {
    private val stateKey = stringPreferencesKey("auction_state_json")

    val state: Flow<AuctionState> = context.auctionDataStore.data.map { prefs ->
        val raw = prefs[stateKey]
        if (raw.isNullOrBlank()) AuctionState()
        else runCatching { AuctionCodec.decodeState(raw) }.getOrElse { AuctionState() }
    }

    suspend fun save(state: AuctionState) {
        context.auctionDataStore.edit { prefs ->
            prefs[stateKey] = AuctionCodec.encodeState(state)
        }
    }
}
