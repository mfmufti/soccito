package com.team9.soccermanager.screens.changepassword

import androidx.compose.runtime.mutableStateOf
import com.team9.soccermanager.model.PwdError
import com.team9.soccermanager.model.accessor.UserAccessor

class ChangePasswordViewModel {

    private val error = mutableStateOf("")
    fun getError() = error

    fun handlePasswordChange(currPwd: String, newPwd: String, confirmPwd: String, onSuccess: () -> Unit) {
        if (newPwd != confirmPwd) {
            error.value = "Failed! Your new passwords don't match"
            return
        }
        UserAccessor.updateUserPwd(currPwd, newPwd) { status ->
            if (status == PwdError.NONE) {
                onSuccess()
            } else {
                error.value = when(status) {
                    PwdError.UNKNOWN -> "Failed! Unknown error occurred"
                    PwdError.INCORRECT -> "Failed! Incorrect current password"
                    PwdError.NO_EMAIL -> "Failed! No email attached to account"
                    else -> "Failed! New password is weak"
                }
            }
        }
    }
}