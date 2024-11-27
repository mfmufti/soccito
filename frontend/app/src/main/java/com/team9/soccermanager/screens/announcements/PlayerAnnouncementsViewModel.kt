package com.team9.soccermanager.screens.announcements

import com.team9.soccermanager.screens.playerhome.PlayerHomeViewModel

class PlayerAnnouncementsViewModel : PlayerHomeViewModel() {
    init {
        super.getTeam {
            super.announcements.value = it.announcements.toList()
        }
    }
}