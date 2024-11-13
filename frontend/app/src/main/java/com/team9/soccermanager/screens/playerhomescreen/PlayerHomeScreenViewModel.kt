package com.team9.soccermanager.screens.playerHomeScreen

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.team9.soccermanager.model.Account
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.toObject
import com.team9.soccermanager.model.GS
import com.team9.soccermanager.model.Team

open class PlayerHomeScreenViewModel {

    var signedOut = false;

    fun signOut() {
        if (!signedOut) {
            Account.signOut()
            signedOut = true
        }
    }

    fun getTeamName(then: (String) -> Unit): Unit {
        CoroutineScope(Dispatchers.Default).launch {
            if (Firebase.auth.currentUser == null) {
                then("")
            }
            try {
                val email = Firebase.auth.currentUser!!.email
                val query =
                    Firebase.firestore.collection("users").whereEqualTo("email", email).get()
                        .await()
                val id = query.documents[0].id
                println("The id is $id")
                val query2 =
                    Firebase.firestore.collection("teams").whereArrayContains("coachIds", id).get()
                        .await()
                if (!query2.isEmpty) {
                    val team = query2.documents[0].toObject<Team>()
                    then(team!!.name)
                } else {
                    val query3 =
                        Firebase.firestore.collection("teams").whereArrayContains("playerIds", id)
                            .get().await()
                    val team2 = query3.documents[0].toObject<Team>()
                    then(team2!!.name)
                }
            } catch (e: Exception) {
                // TODO: add proper logging of error here
                e.printStackTrace()
                then("")
            }
        }
    }

    fun getFullName(then: (String) -> Unit): Unit {
        then(GS.user!!.fullname)
    }
}