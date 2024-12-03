package com.team9.soccermanager.screens.announcements

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.team9.soccermanager.model.GS
import com.team9.soccermanager.model.accessor.TeamAccessor
import com.team9.soccermanager.screens.coachhome.CoachHomeViewModel

/*
 View model for the coach announcements screen.
 It handles sending notifications to players about new announcements.
 */

class CoachAnnouncementsViewModel : CoachHomeViewModel() {
    //Sends notifications to all players in the team.
    fun sendNotifications() {
        TeamAccessor.getNotificationTokens { tokens ->
            for (token in tokens) {
                sendNotification(token).addOnCompleteListener {
                    if(it.isSuccessful) {
                        Log.d("Notifications", it.result)
                    } else {
                        it.exception?.printStackTrace()
                    }
                }
            }
        }
    }
//Sends a notification to a specific player using their notification token.
    private fun sendNotification(token: String): Task<String> {
        // Create the arguments to the callable function.
        val data = mapOf(
            "title" to "New Announcement",
            "body" to "${GS.user?.fullname} posted a new announcement",
            "token" to token
        )

        return Firebase.functions
            .getHttpsCallable("sendNotification")
            .call(data)
            .continueWith { task ->
                // This continuation runs on either success or failure, but if the task
                // has failed then result will throw an Exception which will be
                // propagated down.
                val result = task.result.getData()
                result.toString()
                // result
            }
    }
}