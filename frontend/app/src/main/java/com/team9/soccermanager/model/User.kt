package com.team9.soccermanager.model

import com.google.firebase.firestore.DocumentReference

/*
  This file defines data classes representing a player's availability (PlrAvail) and a user (User) in the application.
 */


data class PlrAvail(val avail: Availability, val reason: String) {
    constructor() : this(Availability.AVAILABLE, "")
    constructor(plrAvailMap: Map<*, *>) : this(
        reason = plrAvailMap["reason"] as String,
        avail = Availability.valueOf(plrAvailMap["avail"] as String)
    )
}

data class User(
    var id: String,
    var email: String,
    var fullname: String,
    var leagueID: String,
    var leagueName: String,
    var teamID: String,
    var teamName: String,
    var type: String,
    var chats: List<DocumentReference>,
    var notificationToken: String?,
    var lastAnnouncementViewTime: Long
) {
    // need to provide no-arg constructor to support deserialization with Firebase
    constructor() : this("", "", "", "", "", "", "", "", listOf(), "", 0)
}