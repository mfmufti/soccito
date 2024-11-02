package com.team9.soccermanager.screens.home

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.team9.soccermanager.model.Account
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class HomeViewModel {
    var signedOut = false

    fun getUserName(then : (String) -> Unit = {}) {
        Account.getUserName {
            then(it)
        }
    }

    fun getTeamStandings(): MutableList<String> {
        return mutableListOf("Manchester", "Gifted", "Tryhards")
    }

    fun signOut() {
        if (!signedOut) {
            Account.signOut()
            signedOut = true
        }
    }

    fun getJoinCode(then: (String) -> Unit): Unit {
        CoroutineScope(Dispatchers.Default).launch {
            if (Firebase.auth.currentUser == null) {
                return@launch
            }
            val email = Firebase.auth.currentUser!!.email
            val query = Firebase.firestore.collection("users").whereEqualTo("email", email).get().await()
            val id = query.documents[0].id
            println("The id is $id")
            val query2 = Firebase.firestore.collection("teams").whereArrayContains("coachIds", id).get().await()
            if (!query2.isEmpty) {
                then("Team invite code:\n ${query2.documents[0].id}")
            } else {
                val query3 = Firebase.firestore.collection("leagues").whereArrayContains("adminIds", id).get().await()
                if (!query3.isEmpty) {
                    then("League invite code:\n ${query3.documents[0].id}")
                } else {
                    then("No invite code for player")
                }
            }
        }
    }
}