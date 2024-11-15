package com.team9.soccermanager.screens.coachhome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.team9.soccermanager.model.Account
import kotlinx.coroutines.*
import com.team9.soccermanager.model.Announcement
import com.team9.soccermanager.model.GS
import com.team9.soccermanager.model.Team
import com.team9.soccermanager.model.accessor.TeamAccessor
import kotlinx.coroutines.tasks.await

open class CoachHomeScreenViewModel : ViewModel() {

    var signedOut = false;

    init {
        viewModelScope.launch(Dispatchers.IO) {
            TeamAccessor.listenForUpdates {
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

    fun getJoinCode(then: (String) -> Unit): Unit {
        CoroutineScope(Dispatchers.Default).launch {
            if (Firebase.auth.currentUser == null) {
                return@launch
            }
            val email = GS.user!!.email
            val query =
                Firebase.firestore.collection("users").whereEqualTo("email", email).get().await()
            val id = query.documents[0].id
            println("The id is $id")
            val query2 =
                Firebase.firestore.collection("teams").whereArrayContains("coachIds", id).get()
                    .await()
            if (!query2.isEmpty) {
                then("Team invite code:\n ${query2.documents[0].id}")
            } else {
                val query3 =
                    Firebase.firestore.collection("leagues").whereArrayContains("adminIds", id)
                        .get().await()
                if (!query3.isEmpty) {
                    then("League invite code:\n ${query3.documents[0].id}")
                } else {
                    then("No invite code for player")
                }
            }
        }
    }
}