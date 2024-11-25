package com.team9.soccermanager.screens.playerforms

import android.content.ContentResolver
import android.net.Uri
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ListenerRegistration
import com.team9.soccermanager.model.Form
import com.team9.soccermanager.model.GS
import com.team9.soccermanager.model.Team
import com.team9.soccermanager.model.accessor.TeamAccessor
import com.team9.soccermanager.screens.playerhome.PlayerHomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.floor

data class SimpleFormUpload(val formId: Int, val name: String, val uploaded: Boolean, val link: String, val timestamp: Timestamp)

class PlayerFormsViewModel: PlayerHomeViewModel() {
    private val formUploads = mutableStateListOf<SimpleFormUpload>()
    private val loading = mutableStateOf(true)
    private val error = mutableStateOf("")
    private val uploading = mutableStateOf(false)
    private val progress = mutableDoubleStateOf(0.0)
    private val uploadError = mutableStateOf("")
    private var listenerRegistration: ListenerRegistration? = null

    fun getFormUploads() = formUploads
    fun getLoading() = loading
    fun getError() = error
    fun getUploading() = uploading
    fun getProgress() = progress

    init {
        val update = { team: Team ->
            formUploads.clear()
            for (form in team.forms) {
                var found = false
                for (upload in form.uploads) {
                    if (upload.playerID == GS.user!!.id) {
                        formUploads.add(SimpleFormUpload(form.id, form.name, true, upload.link, upload.timestamp))
                        found = true
                        break
                    }
                }
                if (!found) {
                    formUploads.add(SimpleFormUpload(form.id, form.name, false, "", Timestamp.now()))
                }
            }
        }

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
            update(team)
            loading.value = false
        }

        listenerRegistration = TeamAccessor.addSnapshotListener(GS.user!!.teamID, {
            if (it == null) return@addSnapshotListener
            val team: Team? = it.toObject(Team::class.java)
            if (team != null) {
                update(team)
            }
        })
    }

    fun uploadForm(uri: Uri, contentResolver: ContentResolver, id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                uploading.value = true
                progress.doubleValue = 0.0
                TeamAccessor.uploadForm(uri, contentResolver, id, { progress.doubleValue = floor(it * 100) / 100 })
//                formUploads.replaceAll {
//                    if (it.formId == id) {
//                        SimpleFormUpload(id, it.name, true, )
//                    } else it
//                }
                uploading.value = false
            } catch (e: Exception) {
                uploadError.value = "Failed to upload. Please check your connection."
                uploading.value = false
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}