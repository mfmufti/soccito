package com.team9.soccermanager.screens.gamespecific

import android.content.Context
import android.location.Geocoder
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.team9.soccermanager.model.GS
import com.team9.soccermanager.model.GameError
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

/*
 View model for the game-specific screen.
 It handles fetching and displaying game details, including location. time, score and coach's notes.
 */


// The ViewModel stays the same
class GameSpecificViewModel(gameIndex: Int) : PlayerHomeViewModel() {
    private val locationState = mutableStateOf(LatLng(0.0, 0.0))
    private val cameraState = CameraPositionState(position = CameraPosition.fromLatLngZoom(locationState.value, 10f))
    private val game = LeagueAccessor.getGameFromLoaded(gameIndex)
    private val editing = mutableStateOf(false)
    private val coachsNotes = mutableStateOf("")
    private val coachsNotesEditing = mutableStateOf("")
    private val errorEditing = mutableStateOf("")

    //Initializes the view model and loads game location and coach's notes.

    init {
        loadGameLocation()
        coachsNotes.value = if (game.team1CoachsNotes.isNotEmpty() && (GS.user!!.teamID == game.team1ID)) {
            game.team1CoachsNotes
        } else if (game.team2CoachsNotes.isNotEmpty() && (GS.user!!.teamID == game.team2ID)) {
            game.team2CoachsNotes
        } else ""
        coachsNotesEditing.value = coachsNotes.value
    }

    fun getLocationState() = locationState
    fun getCameraState() = cameraState
    fun getGame() = game
    fun getEditing() = editing
    fun getCoachsNotes() = coachsNotes
    fun getCoachsNotesEditing() = coachsNotesEditing
    fun getErrorEditing() = errorEditing

    private fun loadGameLocation() {
        locationState.value = LatLng(game.geopoint.latitude, game.geopoint.longitude)
        cameraState.position = CameraPosition.fromLatLngZoom(locationState.value, 10f)
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
            when (LeagueAccessor.writeGame(game)) {
                GameError.NONE -> coachsNotes.value = coachsNotesEditing.value
                GameError.NETWORK -> {
                    errorEditing.value = "Please check your network connection."
                    coachsNotesEditing.value = coachsNotes.value
                }
                GameError.UNKNOWN -> {
                    errorEditing.value = "Unknown error occurred."
                    coachsNotesEditing.value = coachsNotes.value
                }
            }
            editing.value = false
        }
    }
}
