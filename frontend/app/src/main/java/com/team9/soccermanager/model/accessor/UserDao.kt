package com.team9.soccermanager.model.accessor

import com.team9.soccermanager.model.NameError
import com.team9.soccermanager.model.PwdError
import com.team9.soccermanager.model.User

/*
 `UserDao`, defines the data access methods for users in the application.
  Implementations of this interface will provide the logic for interacting with the data source
  (firestore) to manage user-related data.
 */

interface UserDao {
    fun updateUserPwd(currPwd: String, newPwd: String, onError: (PwdError) -> Unit)
    fun updateUserName(newName: String, onError: (NameError) -> Unit)
}