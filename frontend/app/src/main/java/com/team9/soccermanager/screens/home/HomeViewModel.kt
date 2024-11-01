package com.team9.soccermanager.screens.home

import com.team9.soccermanager.model.Account

class HomeViewModel {

    fun getUserName(then : (String) -> Unit = {}) {
        Account.getUserName {
            then(it)
        }
    }

    fun getTeamStandings() : MutableList<String> {
        return mutableListOf("Manchester", "Gifted", "Tryhards")
    }

    fun signOut() {
        Account.signOut()
    }
}