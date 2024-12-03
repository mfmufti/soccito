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
    }

}