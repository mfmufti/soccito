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

class NewAdminViewModel : ViewModel() {
    private var league = mutableStateOf("")
    private var error = mutableStateOf("")

    fun getLeague() = league
    fun getError() = error

    fun checkLeague(success: (String) -> Unit) {
        if (league.value.isEmpty()) {
            error.value = "Please enter a league name"
            return
        }
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