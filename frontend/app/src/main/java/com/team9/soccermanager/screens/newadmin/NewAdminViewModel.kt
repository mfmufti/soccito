package com.team9.soccermanager.screens.newadmin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team9.soccermanager.model.accessor.LeagueAccessor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewAdminViewModel : ViewModel() {

    fun createLeague(leagueName: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val res = LeagueAccessor.createLeague(leagueName)
            withContext(Dispatchers.Main) {
                if (res != null) {
                    onSuccess()
                } else {
                    onError("There was an error creating the league.")
                }
            }
        }
    }
}