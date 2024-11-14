package com.team9.soccermanager.screens.newplayer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.team9.soccermanager.model.Account
import com.team9.soccermanager.model.accessor.TeamAccessor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewPlayerViewModel : ViewModel() {

    fun joinTeam(teamCode: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (teamCode.isEmpty()) {
            onError("Please leave no fields blank.")
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // TODO: restructure error handling, should not deal with them both here and in accessors
                val team = TeamAccessor.getTeamByInviteCode(teamCode)
                    ?: throw Exception("Team not found")
                team.playerIds.add(Firebase.auth.uid ?: throw Exception("Player not logged in"))
                TeamAccessor.updateTeam(team)
                Account.joinTeam(team.id)
                Account.joinLeague(team.leagueId)
                withContext(Dispatchers.Main) {
                    onSuccess()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onError("There was an error joining that team.")
            }
        }
    }
}