package com.team9.soccermanager.screens.coachforms

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ListenerRegistration
import com.team9.soccermanager.model.Form
import com.team9.soccermanager.model.GS
import com.team9.soccermanager.model.Team
import com.team9.soccermanager.model.accessor.TeamAccessor
import com.team9.soccermanager.screens.playerhome.PlayerHomeViewModel
import kotlinx.coroutines.launch

/*
 View model for the coach forms screen.
 It handles displaying and managing forms for the team.
 */

class CoachFormsViewModel: PlayerHomeViewModel() {
    private val forms = mutableStateListOf<Form>()
    private val loading = mutableStateOf(true)
    private val error = mutableStateOf("")
    private val errorModifyingTitle = mutableStateOf("")
    private val errorModifying = mutableStateOf("")
    private val addingNew = mutableStateOf(false)
    private val newName = mutableStateOf("")
    private val deleteConfirming = mutableStateOf(false)
    private val deleteId = mutableIntStateOf(0)
    private var listenerRegistration: ListenerRegistration? = null

    fun getForms() = forms
    fun getLoading() = loading
    fun getError() = error
    fun getErrorModifyingTitle() = errorModifyingTitle
    fun getErrorModifying() = errorModifying
    fun getAddingNew() = addingNew
    fun getNewName() = newName
    fun getDeleteConfirming() = deleteConfirming
    fun getDeleteId() = deleteId

    init {
        listenerRegistration = TeamAccessor.addSnapshotListener(GS.user!!.teamID, {
            if (it == null) return@addSnapshotListener
            val team: Team? = it.toObject(Team::class.java)
            if (team != null) {
                forms.clear()
                forms.addAll(team.forms)
            }
        })

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
            forms.clear()
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
                errorModifyingTitle.value = "Error Creating Dropbox"
                errorModifying.value = "Failed to connect to the network"
                return@launch
            }
            if (team == null) {
                errorModifyingTitle.value = "Error Creating Dropbox"
                errorModifying.value = "Unknown error occurred"
                return@launch
            }
            val form = Form(if (team.forms.isEmpty()) 0 else team.forms.maxOf { it.id } + 1, newName.value, mutableListOf())
            team.forms.add(form)
            if (TeamAccessor.updateTeam(team)) {
                addingNew.value = false
                newName.value = ""
            } else {
                errorModifyingTitle.value = "Error Creating Dropbox"
                errorModifying.value = "Failed to connect to the network"
            }
        }
    }

    fun deleteDropBox() {
        viewModelScope.launch {
            val team: Team?
            try {
                team = TeamAccessor.getTeamById(GS.user!!.teamID, true)
            } catch (e: Exception) {
                errorModifyingTitle.value = "Error Deleting Dropbox"
                errorModifying.value = "Failed to connect to the network"
                deleteConfirming.value = false
                return@launch
            }
            if (team == null) {
                errorModifyingTitle.value = "Error Deleting Dropbox"
                errorModifying.value = "Unknown error occurred"
                deleteConfirming.value = false
                return@launch
            }
            team.forms.retainAll { it.id != deleteId.intValue }
            if (TeamAccessor.updateTeam(team)) {
                addingNew.value = false
                newName.value = ""
            } else {
                errorModifyingTitle.value = "Error Deleting Dropbox"
                errorModifying.value = "Failed to connect to the network"
            }
            deleteConfirming.value = false
        }
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}