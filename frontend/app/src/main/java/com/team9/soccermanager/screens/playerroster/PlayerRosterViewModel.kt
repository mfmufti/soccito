package com.team9.soccermanager.screens.playerroster

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.team9.soccermanager.model.AvailView
import com.team9.soccermanager.model.GS
import com.team9.soccermanager.model.PlrAvail
import com.team9.soccermanager.model.TeamError
import com.team9.soccermanager.model.accessor.TeamAccessor
import com.team9.soccermanager.screens.playerhome.PlayerHomeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/*
 View model for the player roster screen.
 It handles fetching and managing the player availability data.
 */


class PlayerRosterViewModel: PlayerHomeViewModel() {
    private var plrAvailability = mutableStateListOf<AvailView>()
    private var error = mutableStateOf("")
    private var loading = mutableStateOf(true)

    fun getAvailList() = plrAvailability
    fun getError() = error
    fun getLoading() = loading


    //Initializes the view model and fetches the player availability data.
    init {
        CoroutineScope(Dispatchers.IO).launch {
            TeamAccessor.getPlayerAvail(
                {
                    plrAvailability.clear()
                    plrAvailability.addAll(it)
                    loading.value = false
                }, {
                    error.value = when(it) {
                        TeamError.NETWORK -> "Network connection error, check internet connectivity."
                        else -> "Unknown error occurred while getting player availability"
                    }
                    loading.value = false
                }
            )
        }
    }

}

