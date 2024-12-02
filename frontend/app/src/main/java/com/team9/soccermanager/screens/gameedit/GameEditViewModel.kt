package com.team9.soccermanager.screens.gameedit

import android.content.Context
import android.location.Geocoder
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import com.team9.soccermanager.model.GS
import com.team9.soccermanager.model.GameError
import com.team9.soccermanager.model.GameStatus
import com.team9.soccermanager.model.accessor.Game
import com.team9.soccermanager.model.accessor.LeagueAccessor
import com.team9.soccermanager.screens.playerhome.PlayerHomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/*
 View model for the game edit screen.
 It handles the admin creating, editing, and deleting game data.
 */

data class SimpleTeam(val id: String = "", val name: String = "") {
    override fun toString(): String {
        return name
    }
}

data class DisplayableStatus(val status: GameStatus? = null) {
    override fun toString(): String {
        if (status == null) {
            return ""
        }
        var ans = status.toString().lowercase()
        if (ans.isNotEmpty()) {
            ans = ans.replaceFirstChar { it.uppercase() }
        }
        return ans
    }
}

class GameEditViewModel(private var gameId: Int, private var newGame: Boolean): PlayerHomeViewModel() {
    private val defaultErrorTitle = "Error Saving Changes"
    private val team1Score = mutableStateOf("")
    private val team2Score = mutableStateOf("")
    private val date: MutableState<Date?> = mutableStateOf(null)
    private val hours: MutableState<Int?> = mutableStateOf(null)
    private val minutes: MutableState<Int?> = mutableStateOf(null)
    private val teams = mutableStateListOf<SimpleTeam>()
    private val statusSelect = mutableStateOf(DisplayableStatus())
    private val team1Select = mutableStateOf(SimpleTeam())
    private val team2Select = mutableStateOf(SimpleTeam())
    private val showDateSelect = mutableStateOf(false)
    private val showTimeSelect = mutableStateOf(false)
    private var address = mutableStateOf("")
    private val loading = mutableStateOf(true)
    private val errorSaving = mutableStateOf("")
    private val errorLoading = mutableStateOf("")
    private val errorTitle = mutableStateOf(defaultErrorTitle)
    private val deleteConfirming = mutableStateOf(false)

    private var team1CoachsNotes = ""
    private var team2CoachsNotes = ""
    private var geopoint = GeoPoint(0.0, 0.0)

    fun getTeam1Score() = team1Score
    fun getTeam2Score() = team2Score
    fun getDate() = date
    fun getHours() = hours
    fun getMinutes() = minutes
    fun getTeams() = teams
    fun getStatusSelect() = statusSelect
    fun getTeam1Select() = team1Select
    fun getTeam2Select() = team2Select
    fun getShowDateSelect() = showDateSelect
    fun getShowTimeSelect() = showTimeSelect
    fun getAddress() = address
    fun getLoading() = loading
    fun getErrorSaving() = errorSaving
    fun getErrorLoading() = errorLoading
    fun getErrorTitle() = errorTitle
    fun getDeleteConfirming() = deleteConfirming
    fun resetError() {
        errorSaving.value = ""
        errorTitle.value = defaultErrorTitle
    }

    //Initializes the view model and loads game data if editing an existing game.


    init {
        viewModelScope.launch {
            val league = LeagueAccessor.getLeagueById(GS.user!!.leagueID)
            if (league == null) {
                errorLoading.value = "Failed to connect to the network."
                return@launch
            }

            for (i in league.teamIds.indices) {
                teams.add(SimpleTeam(league.teamIds[i], league.teamNames[i]))
            }

            if (!newGame) {
                val gameRet = LeagueAccessor.getGame(gameId)
                when (gameRet.first) {
                    GameError.NONE -> {}
                    GameError.NETWORK -> { errorLoading.value = "Failed to connect to the network."; return@launch }
                    GameError.UNKNOWN -> { errorLoading.value = "Unknown error occurred."; return@launch }
                }
                val game = gameRet.second
                team1Score.value = game.team1Score.toString()
                team2Score.value = game.team2Score.toString()
                statusSelect.value = DisplayableStatus(game.status)
                team1Select.value = teams.first { it.id == game.team1ID }
                team2Select.value = teams.first { it.id == game.team2ID }

                val cal = Calendar.getInstance()
                cal.setTime(game.timestamp.toDate())
                date.value = cal.time
                hours.value = cal.get(Calendar.HOUR_OF_DAY)
                minutes.value = cal.get(Calendar.MINUTE)
                address.value = game.address

                team1CoachsNotes = game.team1CoachsNotes
                team2CoachsNotes = game.team2CoachsNotes
                geopoint = game.geopoint
            }

//            team1Select.value =

            loading.value = false
        }
    }

    //write to firestore
    fun writeGame(context: Context, success: () -> Unit = {}) {
        val cal = Calendar.getInstance()
        val cal2 = Calendar.getInstance(TimeZone.getTimeZone("GMT"))
        val team1ScoreInt: Int
        val team2ScoreInt: Int
        if (statusSelect.value.status == null) {
            errorSaving.value = "Please select a status for the game."
            return
        }
        if (team1Select.value.id.isEmpty() || team2Select.value.id.isEmpty() || team1Select.value == team2Select.value) {
            errorSaving.value = "Please choose 2 unique teams."
            return
        }
        try {
            team1ScoreInt = team1Score.value.toInt()
            assert(team1ScoreInt >= 0)
        } catch (e: Throwable) {
            errorSaving.value = "Please specify a valid score for team 1."
            return
        }
        try {
            team2ScoreInt = team2Score.value.toInt()
            assert(team2ScoreInt >= 0)
        } catch (e: Throwable) {
            errorSaving.value = "Please specify a valid score for team 2."
            return
        }
        try {
            cal2.setTime(date.value!!)
            cal.set(Calendar.YEAR, cal2.get(Calendar.YEAR))
            cal.set(Calendar.MONTH, cal2.get(Calendar.MONTH))
            cal.set(Calendar.DAY_OF_MONTH, cal2.get(Calendar.DAY_OF_MONTH))
        } catch (e: Exception) {
            errorSaving.value = "Please specify a valid date."
            return
        }
        try {
            cal.set(Calendar.HOUR_OF_DAY, hours.value!!)
            cal.set(Calendar.MINUTE, minutes.value!!)
        } catch (e: Exception) {
            errorSaving.value = "Please specify a valid time."
            return
        }

        viewModelScope.launch {
            try {
                val coords = getCoordinatesFromAddress(context, address.value)
                geopoint = GeoPoint(coords.first, coords.second)
            } catch (e: Exception) {
                errorSaving.value = "Please enter a valid address for the game."
                return@launch
            }

            val game = Game.from(
                id = gameId,
                address = address.value,
                geopoint = geopoint,
                team1ID = team1Select.value.id,
                team2ID = team2Select.value.id,
                team1Name = team1Select.value.name,
                team2Name = team2Select.value.name,
                timestamp = Timestamp(cal.time),
                team1Score = team1ScoreInt,
                team2Score = team2ScoreInt,
                status = statusSelect.value.status!!,
                team1CoachsNotes = team1CoachsNotes,
                team2CoachsNotes = team2CoachsNotes,
            )

            when (LeagueAccessor.writeGame(game, newGame)) {
                GameError.NONE -> {
                    newGame = false
                    gameId = game.id
                    success()
                }
                GameError.NETWORK -> errorSaving.value = "Failed to connect to your network."
                GameError.UNKNOWN -> errorSaving.value = "Unknown error occurred."
            }
        }
    }

    fun deleteGame(success: () -> Unit) {
        viewModelScope.launch {
            deleteConfirming.value = false
            when (LeagueAccessor.deleteGame(gameId)) {
                GameError.NONE -> success()
                GameError.NETWORK -> {
                    errorTitle.value = "Error Deleting Game"
                    errorSaving.value = "Failed to connect to your network."
                }
                GameError.UNKNOWN -> {
                    errorTitle.value = "Error Deleting Game"
                    errorSaving.value = "Unknown error occurred."
                }
            }
        }
    }


    //Gets the coordinates of an address using the Geocoder.
    private suspend fun getCoordinatesFromAddress(context: Context, address: String): Pair<Double, Double> {
        val geocoder = Geocoder(context, Locale.getDefault())
        return withContext(Dispatchers.IO) {
            val addresses = geocoder.getFromLocationName(address, 1)
            val location = addresses!!.get(0)
            Pair(location.latitude, location.longitude)
        }
    }
}