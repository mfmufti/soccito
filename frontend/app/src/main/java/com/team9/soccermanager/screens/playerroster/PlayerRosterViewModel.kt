package com.team9.soccermanager.screens.playerroster

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.team9.soccermanager.model.GS
import com.team9.soccermanager.screens.chatselect.Chat
import com.team9.soccermanager.screens.playerhome.PlayerHomeScreenViewModel

class PlayerAvailablility(val name: String, val availability: String)

class PlayerRosterViewModel: PlayerHomeScreenViewModel() {
    private val playerAvailabilityList = mutableStateListOf<PlayerAvailablility>()
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
                            for (player in it.data!!["playerIds"] as List<*>) {
                                db.collection("users").document(player as String).get().addOnSuccessListener {
                                    if (it.data != null && it.id != myId) {
                                        playerAvailabilityList.add(PlayerAvailablility(it.data!!["fullname"] as String, "Available"))
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

    fun getPlayerAvailabilityList(): MutableList<PlayerAvailablility> {
        return playerAvailabilityList
    }

}

