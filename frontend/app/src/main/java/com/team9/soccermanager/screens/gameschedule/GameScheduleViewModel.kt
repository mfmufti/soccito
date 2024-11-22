package com.team9.soccermanager.screens.gameschedule

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.team9.soccermanager.model.GS
import com.team9.soccermanager.model.GameError
import com.team9.soccermanager.model.accessor.Game
import com.team9.soccermanager.model.accessor.LeagueAccessor
import com.team9.soccermanager.screens.playerhome.PlayerHomeViewModel
import kotlinx.coroutines.launch

class GameScheduleViewModel: PlayerHomeViewModel() {
    private val upcomingGames = mutableStateListOf<Game>()
    private val completedGames = mutableStateListOf<Game>()
    private val loading = mutableStateOf(true)
    private val error = mutableStateOf("")

    fun getUpcomingGames() = upcomingGames
    fun getCompletedGames() = completedGames
    fun getLoading() = loading
    fun getError() = error

    init {
        viewModelScope.launch {
            val gameData = LeagueAccessor.getGames(GS.user!!.leagueID)
            val gameError = gameData.first

            if (gameError == GameError.NETWORK) {
                error.value = "There was an error connecting to the internet"
                return@launch
            } else if (gameError == GameError.UNKNOWN) {
                error.value = "Unknown error occurred"
                return@launch
            }

            val games = gameData.second

            for (game in games) {
                if (GS.user!!.type != "admin" && !listOf(game.team1ID, game.team2ID).contains(GS.user!!.teamID)) {
                    // Skip games that are between two different teams, if not admin
                    continue
                }
                if (game.timestamp >= Timestamp.now()) {
                    upcomingGames.add(game)
                } else {
                    completedGames.add(game)
                }
            }
            loading.value = false
        }
    }
}