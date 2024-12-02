package com.team9.soccermanager.screens.playerforms

import android.content.ContentResolver
import android.content.res.AssetFileDescriptor
import android.content.res.AssetFileDescriptor.UNKNOWN_LENGTH
import android.net.Uri
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.StorageException
import com.team9.soccermanager.model.Form
import com.team9.soccermanager.model.GS
import com.team9.soccermanager.model.Team
import com.team9.soccermanager.model.accessor.TeamAccessor
import com.team9.soccermanager.screens.playerhome.PlayerHomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.floor

/*
 View model for the player forms screen.
 It handles fetching, managing, and uploading player forms.
 */

data class SimpleFormUpload(val formId: Int, val name: String, val uploaded: Boolean, val link: String, val timestamp: Timestamp)

class PlayerFormsViewModel: PlayerHomeViewModel() {
    private val validMimeTypes = listOf("application/pdf", "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
    private val maxBytes = 5e6
    private val formUploads = mutableStateListOf<SimpleFormUpload>()
    private val loading = mutableStateOf(true)
    private val error = mutableStateOf("")
    private val uploading = mutableStateOf(false)
    private val progress = mutableDoubleStateOf(0.0)
    private val uploadError = mutableStateOf("")
    private var listenerRegistration: ListenerRegistration? = null
    private var cancel: () -> Unit = {}

    fun getFormUploads() = formUploads
    fun getLoading() = loading
    fun getError() = error
    fun getUploading() = uploading
    fun getProgress() = progress
    fun getUploadError() = uploadError

    /*
     Initializes the view model and fetches the form uploads.
     */
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
        val mimeType = contentResolver.getType(uri)
        if (!validMimeTypes.contains(mimeType)) {
            uploadError.value = "Your form file must be a PDF or DOCX."
            return
        } else {
            val a = contentResolver.openAssetFileDescriptor(uri , "r")
            a.use {
                if (a == null || a.length == UNKNOWN_LENGTH) {
                    uploadError.value = "Your form file size could not be determined."
                    return@uploadForm
                } else if (a.length > maxBytes) {
                    uploadError.value = "Your form file cannot exceed 5MB in size."
                    return@uploadForm
                }
            }
        }
        // The file is a PDF or DOCX and of a valid size, proceed with the upload
        viewModelScope.launch(Dispatchers.IO) {
            try {
                uploading.value = true
                progress.doubleValue = 0.0
                TeamAccessor.uploadForm(uri, contentResolver, id, { progress.doubleValue = floor(it * 100) / 100 }, { cancel = it })
                uploading.value = false
                cancel = {}
            } catch (_: StorageException) {
                uploading.value = false
            } catch (e: Exception) {
                uploadError.value = "Failed to upload. Please check your connection."
                uploading.value = false
            }
        }
    }

    fun cancelUpload() {
        cancel()
    }

    /*
     Cleans up resources when the view model is cleared.
     */

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}