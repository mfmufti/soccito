package com.team9.soccermanager.screens.playerchatscreen

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.team9.soccermanager.model.GS
import com.team9.soccermanager.screens.playerHomeScreen.PlayerHomeScreenViewModel

data class Message(val right: Boolean, var text: String)

class PlayerChatViewModel(val chatID: String): PlayerHomeScreenViewModel() {
    private val messages = mutableStateListOf<Message>()
    private var loading = mutableStateOf(true)

    private fun processData(data: Map<String, Any>?) {
        val tmp = data?.get("messages")
        if (tmp != null) {
            messages.clear()
            val newMessages: List<*> = (tmp as List<*>)
            for (message in newMessages) {
                val m = message as Map<*, *>
                val isFromCurrent = (m["from"] as DocumentReference).id == GS.user?.id
                messages.add(Message(isFromCurrent, m["text"] as String))
            }
        }
    }

    init {
        val document = Firebase.firestore.collection("chats").document(chatID)
        document.get().addOnSuccessListener({
            processData(it.data)
            loading.value = false
        })
        document.addSnapshotListener({ snapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                processData(snapshot.data)
            }
        })
    }

    fun isLoading(): MutableState<Boolean> {
        return loading
    }

    fun getMessages() : MutableList<Message> {
        return messages
    }

    fun sendMessage(text: String) {
        val db = Firebase.firestore
        val toAdd = hashMapOf(
            "from" to db.collection("users").document(GS.user!!.id),
            "text" to text
        )
        db.collection("chats").document(chatID).get().addOnSuccessListener {
            val messages = it.data!!["messages"] as List<*>
            val messagesUpdated : ArrayList<Any?> = ArrayList(messages)
            messagesUpdated.add(toAdd)
            db.collection("chats").document(chatID).update("messages", messagesUpdated)
        }
    }
}