package com.team9.soccermanager.screens.newcoach

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team9.soccermanager.model.TeamError
import com.team9.soccermanager.model.accessor.TeamAccessor
import kotlinx.coroutines.launch

/*
 View model for the new coach screen.
 It handles validating and processing the league code and team name entered by a new coach.
 */

class NewCoachViewModel : ViewModel() {
    private var leagueCode = mutableStateOf("")
    private var team = mutableStateOf("")
    private var error = mutableStateOf("")

    fun getLeagueCode() = leagueCode
    fun getTeam() = team
    fun getError() = error

    /*
    Checks if the league code and team name are valid.
    @param success A callback function to be invoked if the league code and team name are valid.
     */

    fun checkTeam(success: (String, String) -> Unit) {
        if (leagueCode.value.isBlank() || team.value.isBlank()) {
            error.value = "Please fill all fields"
            return
        }
        if (team.value.trim().length > 25) {
            error.value = "Team name must be less than 26 characters"
            return
        }
        team.value = team.value.trim()
        viewModelScope.launch {
            when (TeamAccessor.teamExists(team.value, leagueCode.value)) {
                TeamError.NONE -> success(leagueCode.value, team.value)
                TeamError.EXISTS -> error.value = "A team with this name already exists"
                TeamError.NETWORK -> error.value = "Failed to connect to the network"
                TeamError.BAD_JOIN_CODE -> error.value = "Invalid league join code"
                TeamError.UNKNOWN -> error.value = "Unknown error occurred"
            }
        }
    }
}