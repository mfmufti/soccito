package com.team9.soccermanager.model.accessor

import com.team9.soccermanager.model.NameError
import com.team9.soccermanager.model.PwdError
import com.team9.soccermanager.model.User

interface UserDao {
    fun updateUserPwd(currPwd: String, newPwd: String, onError: (PwdError) -> Unit)
    fun updateUserName(newName: String, onError: (NameError) -> Unit)
}