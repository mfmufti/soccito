package com.team9.soccermanager.screens.chatselect

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.team9.soccermanager.model.GS
import com.team9.soccermanager.screens.playerhome.PlayerHomeViewModel

data class Chat(val name: String, val type: String, val userRef: DocumentReference)

class ChatSelectViewModel: PlayerHomeViewModel() {
    private val chats = mutableStateListOf<Chat>()
    private var loading = mutableStateOf(true)
    private var error = mutableStateOf(false)
    private var errorLoadingChat = mutableStateOf(false)
    private var waiting = 0

    private val chatSorter = { a: Chat, b: Chat ->
        if (a.type == b.type) {
            a.userRef.id.compareTo(b.userRef.id)
        } else {
            val order = listOf("Admin", "Coach", "Player")
            order.indexOf(a.type).compareTo(order.indexOf(b.type))
        }
    }

    private val decrementAndCheck = {
        waiting--
        if (waiting == 0) {
            chats.sortWith(chatSorter)
            loading.value = false
        }
    }

    init {
        val db = Firebase.firestore
        val myType = GS.user!!.type
        val myId = GS.user!!.id
        val iterateAndAdd = { doc: DocumentSnapshot, field: String, type: String ->
            for (user in doc.data!![field] as List<*>) {
                waiting++
                db.collection("users").document(user as String).get().addOnSuccessListener {
                    if (it.data != null && it.id != myId) {
                        chats.add(Chat(it.data!!["fullname"] as String, type, it.reference))
                    }
                    decrementAndCheck()
                }.addOnFailureListener({ error.value = true })
            }
        }

        waiting++

        if (myType != "admin") {
            waiting++
            db.collection("teams").document(GS.user!!.teamID).get().addOnSuccessListener {
                if (it.data == null) return@addOnSuccessListener
                iterateAndAdd(it, "coachIds", "Coach")
                iterateAndAdd(it, "playerIds", "Player")
                decrementAndCheck()
            }.addOnFailureListener({ error.value = true })
        }

        if (myType != "player") {
            waiting++
            db.collection("leagues").document(GS.user!!.leagueID).get().addOnSuccessListener {
                if (it.data == null) return@addOnSuccessListener
                iterateAndAdd(it, "adminIds", "Admin")
                for (team in it.data!!["teamIds"] as List<*>) {
                    waiting++
                    db.collection("teams").document(team as String).get().addOnSuccessListener {
                        if (it.data != null) {
                            iterateAndAdd(it, "coachIds", "Coach")
                            decrementAndCheck()
                        }
                    }.addOnFailureListener({ error.value = true })
                }
                decrementAndCheck()
            }.addOnFailureListener({ error.value = true })
        }
        decrementAndCheck()
    }

    fun isErrorLoadingChat(): MutableState<Boolean> {
        return errorLoadingChat
    }

    fun resetErrorLoadingChat() {
        errorLoadingChat.value = false
    }

    fun isError(): MutableState<Boolean> {
        return error
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
            if (it.metadata.isFromCache) {
                errorLoadingChat.value = true
                return@addOnSuccessListener
            }
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
                }.addOnFailureListener({ errorLoadingChat.value = true })
            } else {
                switchToChat(found, chats[index].name)
            }
        }.addOnFailureListener({ errorLoadingChat.value = true })
    }
}