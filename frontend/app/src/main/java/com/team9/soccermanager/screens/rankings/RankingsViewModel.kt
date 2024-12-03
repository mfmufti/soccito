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
import com.team9.soccermanager.model.RankingView
import com.team9.soccermanager.model.accessor.TeamAccessor
import com.team9.soccermanager.model.accessor.LeagueAccessor
import com.team9.soccermanager.model.Team
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class RankingsViewModel {

    //var teamsList = mutableStateListOf<TeamView>()
    private val loading = mutableStateOf(true)
    private val teamsList = mutableStateListOf<RankingView>()

    fun getList() = teamsList
    fun getLoading() = loading

    init {
        CoroutineScope(Dispatchers.IO).launch {
            TeamAccessor.getRankingsData {
                teamsList.addAll(it)
                loading.value = false
            }
        }
    }

}