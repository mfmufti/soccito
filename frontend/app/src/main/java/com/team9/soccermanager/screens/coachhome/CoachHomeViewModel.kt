package com.team9.soccermanager.screens.coachhome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore
import com.team9.soccermanager.model.Account
import kotlinx.coroutines.*
import com.team9.soccermanager.model.Announcement
import com.team9.soccermanager.model.GS
import com.team9.soccermanager.model.Team
import com.team9.soccermanager.model.accessor.TeamAccessor
import kotlinx.coroutines.tasks.await

open class CoachHomeViewModel : ViewModel() {
    var signedOut = false
    private var listener: ListenerRegistration? = null

    init {
        viewModelScope.launch(Dispatchers.IO) {
            listener = TeamAccessor.listenForUpdates {
                // If there's any live components that need to update, do it here
            }
        }
    }

    fun signOut() {
        if (!signedOut) {
            Account.signOut()
            signedOut = true
        }
    }

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

    fun addAnnouncement(content: String, onAdd: () -> Unit) {
        val teamId = GS.user?.teamID
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val team = TeamAccessor.getTeamById(teamId!!)
                    ?: throw Exception("Team not found")
                team.announcements.add(Announcement(content, GS.user?.fullname?.split(" ")?.first()!!, System.currentTimeMillis()))
                TeamAccessor.updateTeam(team)
                withContext(Dispatchers.Main) {
                    onAdd()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getFullName(then: (String) -> Unit): Unit {
        then(GS.user!!.fullname)
    }

    override fun onCleared() {
        super.onCleared()
        listener?.remove()
    }
}