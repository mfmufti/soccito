package com.team9.soccermanager.screens.coachroster

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.team9.soccermanager.model.AvailView
import com.team9.soccermanager.model.Availability
import com.team9.soccermanager.model.PlrAvail
import com.team9.soccermanager.model.TeamError
import com.team9.soccermanager.model.accessor.TeamAccessor
import com.team9.soccermanager.screens.playerhome.PlayerHomeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CoachRosterViewModel : PlayerHomeViewModel() {

    private var plrAvailability = mutableStateListOf<AvailView>()
    private var error = mutableStateOf("")
    private var loading = mutableStateOf(true)

    fun getAvailList() = plrAvailability
    fun getError() = error
    fun getLoading() = loading

    fun handleAvailUpdate(id: String, newAvail: Boolean, reason: String, onError: (String) -> Unit, onSuccess: () -> Unit) {
        if (!newAvail && (reason.isEmpty() || reason.isBlank())) {
            onError("Please provide a reason")
            return
        }
        if (!newAvail && reason.length > 100) {
            onError("Reason exceeds 100 char limit")
            return
        }
        var availability = Availability.AVAILABLE
        var dbReason = ""
        if (!newAvail) {
            dbReason = reason
            availability = Availability.UNAVAILABLE
        }
        TeamAccessor.updateUserAvail(id, availability, dbReason)

        onSuccess()

        loading.value = true

        for(i in 0..<plrAvailability.size) {
            if (plrAvailability[i].playerId == id) {
                plrAvailability[i].playerAvail = PlrAvail(availability, dbReason)
            }
        }

        loading.value = false

    }

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