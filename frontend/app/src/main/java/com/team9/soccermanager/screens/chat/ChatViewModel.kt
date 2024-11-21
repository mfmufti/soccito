package com.team9.soccermanager.screens.chat

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.google.firebase.firestore.ListenerRegistration
import com.team9.soccermanager.model.accessor.ChatAccessor
import com.team9.soccermanager.model.accessor.Message
import com.team9.soccermanager.screens.playerhome.PlayerHomeViewModel

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
        ChatAccessor.sendMessage(chatID, text)
    }

    override fun onCleared() {
        super.onCleared()
        listener.remove()
    }
}