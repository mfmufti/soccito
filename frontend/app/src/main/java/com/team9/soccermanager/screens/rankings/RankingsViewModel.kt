package com.team9.soccermanager.screens.rankings

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.team9.soccermanager.model.RankingRow
import com.team9.soccermanager.model.accessor.LeagueAccessor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RankingsViewModel {

    private val loading = mutableStateOf(true)
    private val teamsList = mutableStateListOf<RankingRow>()

    fun getList() = teamsList
    fun getLoading() = loading

    init {
        CoroutineScope(Dispatchers.IO).launch {
            LeagueAccessor.getRankingsData {
                teamsList.addAll(it)
                loading.value = false
            }
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