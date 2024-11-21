package com.team9.soccermanager.model.accessor

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ListenerRegistration

data class Chat(val name: String, val type: String, val userRef: DocumentReference)
data class Message(val right: Boolean, var text: String)

interface ChatDao {
    fun loadChats(onLoad: (List<Chat>) -> Unit, onError: () -> Unit)
    fun getChatID(userRef1: DocumentReference, userRef2: DocumentReference, onSuccess: (String) -> Unit, onError: () -> Unit)
    fun loadAndListenChat(chatID: String, onLoad: (List<Message>) -> Unit): ListenerRegistration
    fun sendMessage(chatID: String, text: String)
}