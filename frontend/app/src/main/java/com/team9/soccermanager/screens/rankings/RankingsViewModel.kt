package com.team9.soccermanager.screens.rankings

import androidx.compose.runtime.mutableStateListOf
import com.team9.soccermanager.model.GS
import com.team9.soccermanager.model.accessor.TeamAccessor
import com.team9.soccermanager.model.accessor.LeagueAccessor
import com.team9.soccermanager.model.Team

class RankingsViewModel {
    var leagueName: String = ""
    var teamsList = mutableStateListOf<Team?>()
    var teamId = GS.user?.teamID

    suspend fun getScreenData() {
        try {
            var leagueId = GS.user?.leagueID
            val league = LeagueAccessor.getLeagueById(leagueId!!)
            leagueName = league!!.name
            for(teamId in league.teamIds) {
                teamsList.add(TeamAccessor.getTeamById(teamId))
            }
            teamsList.sortByDescending { it!!.points }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}