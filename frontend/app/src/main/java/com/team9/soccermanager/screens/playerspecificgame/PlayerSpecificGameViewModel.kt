package com.team9.soccermanager.screens.playerspecificgame

import android.content.Context
import android.location.Geocoder
import androidx.compose.runtime.mutableStateOf

import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.team9.soccermanager.model.GS
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
    context: Context
) : PlayerHomeViewModel() {
    private val geocoder = Geocoder(context, Locale.getDefault())

    private val _locationState = MutableStateFlow<LatLng?>(null)
    val locationState: StateFlow<LatLng?> = _locationState.asStateFlow()

    private val editing = mutableStateOf(false)
    private val coachsNotes = mutableStateOf("")
    private val coachsNotesEditing = mutableStateOf("")
    private val game = LeagueAccessor.getGameFromLoaded(gameIndex)

    init {
        loadGameLocation()
        coachsNotes.value = if (game.team1CoachsNotes.isNotEmpty() && (GS.user!!.teamID == game.team1ID)) {
            game.team1CoachsNotes
        } else if (game.team2CoachsNotes.isNotEmpty() && (GS.user!!.teamID == game.team2ID)) {
            game.team2CoachsNotes
        } else ""
        coachsNotesEditing.value = coachsNotes.value
    }

    fun getEditing() = editing
    fun getCoachsNotes() = coachsNotes
    fun getCoachsNotesEditing() = coachsNotesEditing
    fun getGame() = game

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

    fun cancelNotesEditing() {
        coachsNotesEditing.value = coachsNotes.value
        editing.value = false
    }

    fun updateCoachsNotes() {
        viewModelScope.launch {
            if (GS.user!!.teamID == game.team1ID) {
                game.team1CoachsNotes = coachsNotesEditing.value
            } else {
                game.team2CoachsNotes = coachsNotesEditing.value
            }
            println(coachsNotesEditing.value)
            println("Game is:")
            println(game)
            println(LeagueAccessor.writeGame(game))
            editing.value = false
            coachsNotes.value = coachsNotesEditing.value
        }
    }
}
