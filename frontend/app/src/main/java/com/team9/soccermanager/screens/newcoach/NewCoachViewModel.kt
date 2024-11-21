package com.team9.soccermanager.screens.newcoach

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team9.soccermanager.model.TeamError
import com.team9.soccermanager.model.accessor.TeamAccessor
import kotlinx.coroutines.launch

class NewCoachViewModel : ViewModel() {
    private var leagueCode = mutableStateOf("")
    private var team = mutableStateOf("")
    private var error = mutableStateOf("")

    fun getLeagueCode() = leagueCode
    fun getTeam() = team
    fun getError() = error

    fun checkTeam(success: (String, String) -> Unit) {
        viewModelScope.launch {
            when (TeamAccessor.teamExists(team.value, leagueCode.value)) {
                TeamError.NONE -> success(team.value, leagueCode.value)
                TeamError.EXISTS -> error.value = "A team with this name already exists"
                TeamError.NETWORK -> error.value = "Network error occurred"
                TeamError.BAD_JOIN_CODE -> error.value = "Invalid league join code"
                TeamError.UNKNOWN -> error.value = "Unknown error occurred"
            }
        }
    }
}