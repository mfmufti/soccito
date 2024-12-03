package com.team9.soccermanager.screens.chat

import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore
import com.google.firebase.functions.ktx.functions
import com.team9.soccermanager.model.GS
import com.team9.soccermanager.model.accessor.ChatAccessor
import com.team9.soccermanager.model.accessor.Message
import com.team9.soccermanager.screens.playerhome.PlayerHomeViewModel
import kotlinx.coroutines.runBlocking

/*
 View model for the chat screen.
 It handles loading and displaying messages for a specific chat, + sending new messages.
*/

class ChatViewModel(private val chatID: String): PlayerHomeViewModel() {
    private val messages = mutableStateListOf<Message>()
    private var loading = mutableStateOf(true)
    private val listener: ListenerRegistration = ChatAccessor.loadAndListenChat(chatID, {
        messages.clear()
        messages.addAll(it)
        loading.value = false
    })

    fun getMessages() = messages
    fun isLoading() = loading

    fun sendMessage(text: String) {
        ChatAccessor.sendMessage(chatID, text.trim())
    }

    override fun onCleared() {
        ChatAccessor.updateUserReadTime(chatID) {
            super.onCleared()
            listener.remove()
        }
    }
}