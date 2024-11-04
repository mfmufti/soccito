package com.team9.soccermanager.screens.rankingsScreen

import androidx.compose.runtime.mutableStateListOf
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
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
    var teamId: String = ""

    suspend fun initializer(): Unit {
        if (Firebase.auth.currentUser == null) {
            return
        }
        try {
            val email = Firebase.auth.currentUser!!.email
            val query =
                Firebase.firestore.collection("users").whereEqualTo("email", email).get()
                    .await()
            val id = query.documents[0].id
            var query2 = Firebase.firestore.collection("teams").whereArrayContains("playerIds", id).get()
                .await()
            if (query2.isEmpty) {
                query2 =
                    Firebase.firestore.collection("teams").whereArrayContains("coachIds", id).get()
                        .await()
            }
            val team = query2.documents[0].toObject<Team>()
            teamId = team!!.id
            val league = LeagueAccessor.getLeagueById(team!!.leagueId)
            leagueName = league!!.name
            for(teamId in league.teamIds) {
                teamsList.add(TeamAccessor.getTeamById(teamId))
            }
            teamsList.sortByDescending { it!!.points }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    init {
        CoroutineScope(Dispatchers.Default).launch {
            initializer()
        }
    }
}