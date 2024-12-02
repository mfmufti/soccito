package com.team9.soccermanager.screens.formspecific

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.team9.soccermanager.model.Form
import com.team9.soccermanager.model.FormUpload
import kotlinx.coroutines.*
import com.team9.soccermanager.model.GS
import com.team9.soccermanager.model.Team
import com.team9.soccermanager.model.accessor.TeamAccessor
import com.team9.soccermanager.screens.playerhome.PlayerHomeViewModel

/*
  View model for the form-specific screen.
  It handles displaying and managing form uploads.
 */

data class Upload(val name: String, val uploaded: Boolean, val time: Timestamp, val link: String) {
    constructor(name: String, uploaded: Boolean): this(name, uploaded, Timestamp.now(), "")
}

class FormSpecificViewModel(id: Int): PlayerHomeViewModel() {
//    private var formUploads = mutableStateListOf<FormUpload>()
    private val loading = mutableStateOf(true)
    private val error = mutableStateOf("")

    private var uploads = mutableStateListOf<Upload>()

    fun getUploads() = uploads
    fun getLoading() = loading
    fun getError() = error

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
            val idToInd = mutableMapOf<String, Int>()
            for (i in team.playerNames.indices) {
                val name = team.playerNames[i]
                uploads.add(Upload(name, false, Timestamp.now(), ""))
                idToInd[team.playerIds[i]] = i
            }
            var found = false
            for (curForm in team.forms) {
                if (curForm.id == id) {
                    for (upload in curForm.uploads) {
                        if (idToInd.containsKey(upload.playerID)) {
                            val ind = idToInd[upload.playerID]!!
                            uploads[ind] = Upload(upload.playerName, true, upload.timestamp, upload.link)
                        }
                    }
                    found = true
                    break
                }
            }
            if (!found) {
                error.value = "Unknown error occurred"
            } else {
                loading.value = false
            }
        }
    }
}