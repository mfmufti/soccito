package com.team9.soccermanager.screens.coachhome.forms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import com.team9.soccermanager.model.GS
import com.team9.soccermanager.model.Team
import com.team9.soccermanager.model.accessor.TeamAccessor

open class CoachHomeFormsViewModel : ViewModel() {
    fun getTeam(then: (Team) -> Unit): Unit {
        val teamId = GS.user?.teamID
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val team = TeamAccessor.getTeamById(teamId!!)
                    ?: throw Exception("Team not found")
                withContext(Dispatchers.Main) {
                    then(team)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}