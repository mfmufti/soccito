package com.team9.soccermanager.screens.announcements

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.team9.soccermanager.model.Account
import com.team9.soccermanager.model.GS
import com.team9.soccermanager.screens.playerhome.PlayerHomeViewModel

class PlayerAnnouncementsViewModel : PlayerHomeViewModel() {
    init {
        super.getTeam {
            super.announcements.value = it.announcements.toList()
        }
        Account.updateViewAnnouncement()
        GS.updateNotificationStatus(false, null)
        //notificationsViewModel.setHasNotifications(true)
    }
}