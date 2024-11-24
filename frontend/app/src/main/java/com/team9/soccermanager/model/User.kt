package com.team9.soccermanager.model

import com.google.firebase.firestore.DocumentReference

data class PlrAvail(val avail: Availability, val reason: String) {
    constructor() : this(Availability.AVAILABLE, "")
    constructor(plrAvailMap: Map<*, *>) : this(
        reason = plrAvailMap["reason"] as String,
        avail = Availability.valueOf(plrAvailMap["avail"] as String)
    )
}

data class User(
    var id: String,
    var playerAvail: PlrAvail,
    var email: String,
    var fullname: String,
    var leagueID: String,
    var leagueName: String,
    var teamID: String,
    var teamName: String,
    var type: String,
    var chats: List<DocumentReference>
) {
    // need to provide no-arg constructor to support deserialization with Firebase
    constructor() : this("", PlrAvail(),"", "", "", "", "", "", "", listOf())
}