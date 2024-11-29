package com.team9.soccermanager.screens.changename

import androidx.compose.runtime.mutableStateOf
import com.team9.soccermanager.model.GS
import com.team9.soccermanager.model.NameError
import com.team9.soccermanager.model.accessor.UserAccessor

class ChangeNameViewModel {

    private val error = mutableStateOf("")
    fun getError() = error

    fun handleNameChange(newName: String, onSuccess: () -> Unit) {
        GS.user!!.fullname = newName
        UserAccessor.updateUserName(newName) {
            status ->
            if (status == NameError.NONE) {
                onSuccess()
            } else {
                error.value = "Failed! Unknown error occurred"
            }
        }

    }

}