package com.team9.soccermanager.screens.home

import com.team9.soccermanager.model.Account

class HomeViewModel {
    var account = Account()

    fun getUserName(then : (String) -> Unit = {}) {
        account.getUserName {
            then(it)
        }
    }
}