package com.team9.soccermanager.model.accessor

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ListenerRegistration

data class Chat(val name: String, val type: String, val userRef: DocumentReference, var read: Boolean)
data class Message(val right: Boolean, var text: String, var time: Long)
data class ChatMessage(val from: DocumentReference, var text: String, var time: Long)

interface ChatDao {
    fun loadChats(onLoad: (List<Chat>) -> Unit, onError: () -> Unit)
    fun getChatID(userRef1: DocumentReference, userRef2: DocumentReference, onSuccess: (String) -> Unit, onError: () -> Unit)
    fun loadAndListenChat(chatID: String, onLoad: (List<Message>) -> Unit): ListenerRegistration
    fun sendMessage(chatID: String, text: String)
    fun updateUserReadTime(chatID: String, onSuccess: () -> Unit)
    fun checkChatStatus(onResult: (Boolean) -> Unit)
    fun getLastSeen(chatID: String, onSuccess: (Long) -> Unit)
}