package com.team9.soccermanager.screens.newadmin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team9.soccermanager.model.Account
import com.team9.soccermanager.model.LeagueError
import com.team9.soccermanager.model.accessor.LeagueAccessor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/*
 View model for the new admin screen..
 It handles validating and processing the league name entered by a new admin.
 */


class NewAdminViewModel : ViewModel() {
    private var league = mutableStateOf("")
    private var error = mutableStateOf("")

    fun getLeague() = league
    fun getError() = error

    /*
     Checks if the league name is valid and if the league already exists.
     @param success A callback function to be invoked if the league name is valid and the league does not already exist.
     */

    fun checkLeague(success: (String) -> Unit) {
        if (league.value.isBlank()) {
            error.value = "Please enter a league name"
            return
        }
        if (league.value.trim().length > 25) {
            error.value = "League name must be less than 26 characters"
            return
        }
        league.value = league.value.trim()
        viewModelScope.launch {
            when (LeagueAccessor.leagueExists(league.value)) {
                LeagueError.NONE -> success(league.value)
                LeagueError.EXISTS -> error.value = "A league with this name already exists"
                LeagueError.NETWORK -> error.value = "Failed to connect to the network"
                LeagueError.UNKNOWN -> error.value = "Unknown error occurred"
            }
        }
    }
}