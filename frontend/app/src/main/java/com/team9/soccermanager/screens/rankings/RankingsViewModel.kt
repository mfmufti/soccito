package com.team9.soccermanager.screens.rankings

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.team9.soccermanager.model.GS
import com.team9.soccermanager.model.accessor.TeamAccessor
import com.team9.soccermanager.model.accessor.LeagueAccessor
import com.team9.soccermanager.model.Team
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

data class TeamView(val id: String, val teamName: String, val gp: Long, val wins: Long, val losses: Long, val draws: Long, val pts: Long)

class RankingsViewModel {

    //var teamsList = mutableStateListOf<TeamView>()
    var leagueId = GS.user?.leagueID
    private val loading = mutableStateOf(true)
    private val teamsList = mutableStateListOf<TeamView>()

    fun getList(): SnapshotStateList<TeamView> {
        return teamsList
    }

    fun getLoading(): MutableState<Boolean> {
        return loading
    }

    private suspend fun getScreenData() {
        val db = Firebase.firestore
        try {
            val leagueDocument = db.collection("leagues").document(leagueId!!).get().await()
            val tList = leagueDocument.data?.get("teamIds") as? List<*>
            if (tList != null) {
                for (t in tList) {
                    val teamDocument = db.collection("teams").document(t as String).get().await()
                    if (teamDocument.data != null) {
                        val tid = teamDocument.data!!["id"] as String
                        val currName = teamDocument.data!!["name"] as String
                        val gp = teamDocument.data!!["gamesPlayed"] as Long
                        val wins = teamDocument.data!!["wins"] as Long
                        val losses = teamDocument.data!!["losses"] as Long
                        val draws = teamDocument.data!!["draws"] as Long
                        val pts = teamDocument.data!!["points"] as Long
                        teamsList.add(TeamView(tid, currName, gp, wins, losses, draws, pts))
                    }
                }
            }
        } catch (e: Exception) {
            println("Error: ${e.message}")
        }
        teamsList.sortByDescending { it.pts }
        loading.value = false
    }

    init {
        CoroutineScope(Dispatchers.IO).launch {
            getScreenData()
        }
        // Don't know if we really need the listeners for real time updates...
        /*
        var start1 = true
        if (leagueId != null) {

            Firebase.firestore.collection("leagues").document(leagueId!!).addSnapshotListener {
                snapshot, e ->
                if (e != null) {
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    println("SNAPSHOT")
                    if(start1) {
                        start1 = false
                        return@addSnapshotListener
                    }
                    getScreenData()
                }
            }

            Firebase.firestore.collection("leagues").document(leagueId!!).get().addOnSuccessListener {
                if (it.data != null) {
                    val teams = it.data!!["teamIds"] as List<String>
                    val flags = BooleanArray(teams.size) { true }
                    for(i in 0 .. teams.size - 1) {
                        Firebase.firestore.collection("teams").document(teams[i]).addSnapshotListener {
                            s, e ->
                            if (e != null) {
                                return@addSnapshotListener
                            }
                            if (s != null && s.exists()) {
                                println("TEAM LISTEN")
                                if (flags[i]) {
                                    flags[i] = false
                                    return@addSnapshotListener
                                }
                                getScreenData()
                            }
                        }
                    }
                }
            }
        }*/
    }
}