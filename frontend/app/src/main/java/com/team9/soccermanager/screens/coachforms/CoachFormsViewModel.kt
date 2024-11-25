package com.team9.soccermanager.screens.coachforms

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.team9.soccermanager.model.Form
import com.team9.soccermanager.model.GS
import com.team9.soccermanager.model.Team
import com.team9.soccermanager.model.accessor.TeamAccessor
import com.team9.soccermanager.screens.playerhome.PlayerHomeViewModel
import kotlinx.coroutines.launch

class CoachFormsViewModel: PlayerHomeViewModel() {
    private val forms = mutableStateListOf<Form>()
    private val loading = mutableStateOf(true)
    private val error = mutableStateOf("")
    private val errorAdding = mutableStateOf("")
    private val addingNew = mutableStateOf(false)
    private val newName = mutableStateOf("")

    fun getForms() = forms
    fun getLoading() = loading
    fun getError() = error
    fun getErrorAdding() = errorAdding
    fun getAddingNew() = addingNew
    fun getNewName() = newName

    init {
        viewModelScope.launch {
            val team: Team?
            try {
                team = TeamAccessor.getTeamById(GS.user!!.teamID, true)
            } catch (e: Exception) {
                error.value = "Failed to connect to the network"
                return@launch
            }
            if (team == null) {
                error.value = "Unknown error occurred"
                return@launch
            }
            forms.addAll(team.forms)
            loading.value = false
        }
    }

    fun addDropBox() {
        viewModelScope.launch {
            val team: Team?
            try {
                team = TeamAccessor.getTeamById(GS.user!!.teamID, true)
            } catch (e: Exception) {
                errorAdding.value = "Failed to connect to the network"
                return@launch
            }
            if (team == null) {
                errorAdding.value = "Unknown error occurred"
                return@launch
            }
            val form = Form(team.forms.maxOf { it.id } + 1, newName.value, mutableListOf())
            team.forms.add(form)
            if (TeamAccessor.updateTeam(team)) {
                forms.add(form)
                addingNew.value = false
                newName.value = ""
            } else {
                errorAdding.value = "Failed to connect to the network"
            }
        }
    }
}