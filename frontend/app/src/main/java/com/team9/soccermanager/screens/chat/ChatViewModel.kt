package com.team9.soccermanager.screens.chat

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.team9.soccermanager.model.GS
import com.team9.soccermanager.model.accessor.ChatAccessor
import com.team9.soccermanager.model.accessor.Message
import com.team9.soccermanager.screens.playerhome.PlayerHomeViewModel

class ChatViewModel(val chatID: String): PlayerHomeViewModel() {
    private val messages = mutableStateListOf<Message>()
    private var loading = mutableStateOf(true)

    fun getMessages() = messages
    fun isLoading() = loading

    init {
        ChatAccessor.loadAndListenChat(chatID, {
            messages.clear()
            messages.addAll(it)
            loading.value = false
        })
    }

    fun sendMessage(text: String) {
        ChatAccessor.sendMessage(chatID, text)
    }
}