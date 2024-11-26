package com.team9.soccermanager.screens.playerspecificgame

import android.content.Context
import android.location.Geocoder

import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.team9.soccermanager.model.accessor.Game
import com.team9.soccermanager.model.accessor.LeagueAccessor
import com.team9.soccermanager.screens.playerhome.PlayerHomeViewModel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Locale


// The ViewModel stays the same
class PlayerSpecificGameViewModel(
    val gameIndex: Int,
    private val context: Context
) : PlayerHomeViewModel() {
    private val geocoder = Geocoder(context, Locale.getDefault())

    private val _locationState = MutableStateFlow<LatLng?>(null)
    val locationState: StateFlow<LatLng?> = _locationState.asStateFlow()

    init {
        loadGameLocation()
    }

    fun getGame(): Game {
        return LeagueAccessor.getGameFromLoaded(gameIndex)
    }

    private fun loadGameLocation() {
        viewModelScope.launch {
            val game = getGame()
            // First try to use existing geopoint if available

                // Fall back to geocoding the address
                val coordinates = getCoordinatesFromAddress(game.address)
                coordinates?.let { (lat, lng) ->
                    _locationState.value = LatLng(lat, lng)

            }
        }
    }

    private suspend fun getCoordinatesFromAddress(address: String): Pair<Double, Double>? {
        return withContext(Dispatchers.IO) {
            try {
                val addresses = geocoder.getFromLocationName(address, 1)
                if (!addresses.isNullOrEmpty()) {
                    val location = addresses[0]
                    Pair(location.latitude, location.longitude)
                } else null
            } catch (e: IOException) {
                null
            }
        }
    }
}
