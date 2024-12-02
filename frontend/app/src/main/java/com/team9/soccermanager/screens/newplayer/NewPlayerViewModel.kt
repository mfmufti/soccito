package com.team9.soccermanager.screens.newplayer

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team9.soccermanager.model.TeamCodeError
import com.team9.soccermanager.model.accessor.TeamAccessor
import kotlinx.coroutines.launch

/*
 View model for the new player screen.
 It handles validating and processing the team code entered by a new player.
 */

class NewPlayerViewModel : ViewModel() {
    private var teamCode = mutableStateOf("")
    private var error = mutableStateOf("")

    fun getTeamCode() = teamCode
    fun getError() = error

    /*
    Checks if the team code is valid and exists.
    @param success A callback function to be invoked if the team code is valid.
     */

    fun checkTeamCode(success: (String) -> Unit) {
        if (teamCode.value.isEmpty()) {
            error.value = "Please enter a team code"
            return
        }
        viewModelScope.launch {
            when (TeamAccessor.teamCodeExists(teamCode.value)) {
                TeamCodeError.NONE -> success(teamCode.value)
                TeamCodeError.NETWORK -> error.value = "Failed to connect to the network"
                TeamCodeError.NOT_EXIST -> error.value = "Invalid team join code"
                TeamCodeError.UNKNOWN -> error.value = "Unknown error occurred"
            }
        }
    }
}