package com.team9.soccermanager.model

import android.util.Log
import com.team9.soccermanager.Navigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/*
 * This object (GS) serves as a global state holder for the application,
 * providing access to shared data like the current user and notification status.
 */

object GS {
    var user : User? = null

    private val _notificationState = MutableStateFlow(false)
    val notificationState: StateFlow<Boolean> = _notificationState
/* Updates the notification status based on announcement details and user type. */
    fun updateNotificationStatus(hasAnnouncement: Boolean, newAnnouncementTime: Long?) {
        Log.d("global state", "this is being called!")
        if (user != null && user!!.type == "player") {
            if(hasAnnouncement) {
                if(newAnnouncementTime == null || newAnnouncementTime < user!!.lastAnnouncementViewTime) {
                    Log.d("global state", "blocked")
                    return
                }
            }
            _notificationState.value = hasAnnouncement
        }
    }

    var nav: Navigator? = null
}