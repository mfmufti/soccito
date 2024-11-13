package com.team9.soccermanager.screens.coachScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.team9.soccermanager.model.Account
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.toObject
import com.team9.soccermanager.model.Announcement
import com.team9.soccermanager.model.Team
import com.team9.soccermanager.model.accessor.TeamAccessor

open class CoachHomeScreenViewModel : ViewModel() {

    var signedOut = false;

    fun signOut() {
        if (!signedOut) {
            Account.signOut()
            signedOut = true
        }
    }

    fun getTeam(then: (Team) -> Unit): Unit {
        val teamId = Account.getCurUser()?.teamID
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
        val teamId = Account.getCurUser()?.teamID
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val team = TeamAccessor.getTeamById(teamId!!)
                    ?: throw Exception("Team not found")
                team.announcements.add(Announcement(content, Account.getCurUser()?.fullname?.split(" ")?.first()!!, System.currentTimeMillis()))
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
        then(Account.user!!.fullname)
    }



}