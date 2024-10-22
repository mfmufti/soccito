package com.team9.soccermanager.screens.welcome

import com.team9.soccermanager.model.Account

class WelcomeViewModel {
    var account = Account()

    fun getUserName(then : (String) -> Unit = {}) {
        account.getUserName {
            then(it)
        }
    }
}