package com.team9.soccermanager.screens.playerhome

import android.content.ContentResolver
import android.content.ContentValues.TAG
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore
import com.team9.soccermanager.model.Account
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.toObject
import com.google.firebase.messaging.FirebaseMessaging
import com.team9.soccermanager.model.Announcement
import com.team9.soccermanager.model.GS
import com.team9.soccermanager.model.Team
import com.team9.soccermanager.model.accessor.TeamAccessor

open class PlayerHomeViewModel : ViewModel() {
    var signedOut = false
    var announcements = mutableStateOf<List<Announcement>?>(null)
    private var listener: ListenerRegistration? = null

    init {
        viewModelScope.launch(Dispatchers.IO) {
            listener = TeamAccessor.listenForUpdates {
                viewModelScope.launch(Dispatchers.Main) {
                    announcements.value = it.announcements.toList()
                    if(GS.user != null) {
                        if (it.announcements.size > 0) {
                            GS.updateNotificationStatus(true, it.announcements.reversed().first().datePosted)
                        }
                    }
                }
            }
            Account.setupNotifications()
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

    fun getTeamName(then: (String) -> Unit): Unit {
        then(GS.user!!.teamName)
//        CoroutineScope(Dispatchers.Default).launch {
//            if (Firebase.auth.currentUser == null) {
//                then("")
//            }
//            try {
//                val email = Firebase.auth.currentUser!!.email
//                val query =
//                    Firebase.firestore.collection("users").whereEqualTo("email", email).get()
//                        .await()
//                val id = query.documents[0].id
//                val query2 =
//                    Firebase.firestore.collection("teams").whereArrayContains("coachIds", id).get()
//                        .await()
//                if (!query2.isEmpty) {
//                    val team = query2.documents[0].toObject<Team>()
//                    then(team!!.name)
//                } else {
//                    val query3 =
//                        Firebase.firestore.collection("teams").whereArrayContains("playerIds", id)
//                            .get().await()
//                    val team2 = query3.documents[0].toObject<Team>()
//                    then(team2!!.name)
//                }
//            } catch (e: Exception) {
//                // TODO: add proper logging of error here
//                e.printStackTrace()
//                then("")
//            }
//        }
    }

    fun getFullName(then: (String) -> Unit): Unit {
        then(GS.user!!.fullname)
    }

    override fun onCleared() {
        super.onCleared()
        listener?.remove()
    }
}