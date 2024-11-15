package com.team9.soccermanager.screens.chatselect

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.team9.soccermanager.model.GS
import com.team9.soccermanager.screens.playerhome.PlayerHomeScreenViewModel

data class Chat(val name: String, val type: String, val userRef: DocumentReference)

class ChatSelectViewModel: PlayerHomeScreenViewModel() {
    private val chats = mutableStateListOf<Chat>()
    private var loading = mutableStateOf(true)

    init {
        val db = Firebase.firestore
        val myType = GS.user!!.type
        val myId = GS.user!!.id

        if (myType != "admin") {
            db.collection("teams").whereArrayContains(myType + "Ids", myId).get().addOnSuccessListener {
                if (it.documents.isNotEmpty()) {
                    val teamID = it.documents[0].id
                    db.collection("teams").document(teamID).get().addOnSuccessListener {
                        if (it.data != null) {
                            for (coach in it.data!!["coachIds"] as List<*>) {
                                db.collection("users").document(coach as String).get().addOnSuccessListener {
                                    if (it.data != null && it.id != myId) {
                                        chats.add(Chat(it.data!!["fullname"] as String, "Coach", it.reference))
                                    }
                                }
                            }
                            for (player in it.data!!["playerIds"] as List<*>) {
                                db.collection("users").document(player as String).get().addOnSuccessListener {
                                    if (it.data != null && it.id != myId) {
                                        chats.add(Chat(it.data!!["fullname"] as String, "Player", it.reference))
                                    }
                                }
                            }
                        }
                        loading.value = false
                    }
                }
            }
        }
    }

    fun isLoading(): MutableState<Boolean> {
        return loading
    }

    fun getChats(): MutableList<Chat> {
        return chats
    }

    fun switchToChat(index: Int, switchToChat: (String, String) -> Unit) {
        val db = Firebase.firestore
        val myRef = db.collection("users").document(GS.user!!.id)
        val userRef = chats[index].userRef
        db.collection("chats").whereArrayContains("users", userRef).get().addOnSuccessListener {
            var found = ""

            for (d in it.documents) {
                val users = d.data!!["users"] as List<*>
                if (users.contains(myRef)) {
                    found = d.id
                }
            }

            if (found.isEmpty()) {
                val data = mapOf(
                    "messages" to listOf<Any>(),
                    "users" to listOf(myRef, userRef)
                )
                db.collection("chats").add(data).addOnSuccessListener {
                    switchToChat(it.id, chats[index].name)
                }
            } else {
                switchToChat(found, chats[index].name)
            }
        }
    }
}