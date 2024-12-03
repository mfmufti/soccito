package com.team9.soccermanager.screens.chatselect

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.team9.soccermanager.model.GS
import com.team9.soccermanager.model.accessor.Chat
import com.team9.soccermanager.model.accessor.ChatAccessor
import com.team9.soccermanager.screens.playerhome.PlayerHomeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/*
   View model for the chat selection screen.
   It handles loading and displaying a list of chats for the user to select from.
 */

class ChatSelectViewModel: PlayerHomeViewModel() {
    private val chats = mutableStateListOf<Chat>()
    private var loading = mutableStateOf(true)
    private var error = mutableStateOf(false)
    private var errorLoadingChat = mutableStateOf(false)

    fun isErrorLoadingChat() = errorLoadingChat
    fun isError() = error
    fun isLoading() = loading
    fun getChats() = chats

    fun resetErrorLoadingChat() {
        errorLoadingChat.value = false
    }
    //Initializes the view model and loads the list of chats.
    init {
        CoroutineScope(Dispatchers.Main).launch {
            delay(1000)
            ChatAccessor.loadChats({ chats.addAll(it); loading.value = false }, { error.value = true })
        }
    }

    fun switchToChat(index: Int, switchToChat: (String, String) -> Unit) {
        val myRef = Firebase.firestore.collection("users").document(GS.user!!.id)
        val userRef = chats[index].userRef
        ChatAccessor.getChatID(
            myRef,
            userRef,
            { switchToChat(it, chats[index].name) },
            { errorLoadingChat.value = true }
        )
    }
}