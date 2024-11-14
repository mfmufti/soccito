package com.team9.soccermanager.screens.rankingsScreen

import androidx.compose.runtime.mutableStateListOf
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.team9.soccermanager.model.GS
import com.team9.soccermanager.model.League
import kotlinx.coroutines.tasks.await
import com.team9.soccermanager.model.accessor.TeamAccessor
import com.team9.soccermanager.model.accessor.LeagueAccessor
import com.team9.soccermanager.model.Team
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RankingViewModel {
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