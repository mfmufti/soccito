package com.team9.soccermanager.screens.newcoach

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team9.soccermanager.model.accessor.LeagueAccessor
import com.team9.soccermanager.model.accessor.TeamAccessor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewCoachViewModel : ViewModel() {

    fun createTeam(leagueCode: String, teamName: String,  onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // TODO: restructure error handling, should not deal with them both here and in accessors
                val league = LeagueAccessor.getLeagueByInviteCode(leagueCode)
                    ?: throw Exception("League not found")
                val team = TeamAccessor.createTeam(teamName) ?: throw Exception("Failed to create team")
                league.teamIds.add(team.id)
                LeagueAccessor.updateLeague(league)
                withContext(Dispatchers.Main) {
                    onSuccess()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // TODO: show error in app
            }
        }
    }
}