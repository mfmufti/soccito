package com.team9.soccermanager.model

import com.google.firebase.firestore.DocumentReference

data class User(
    var id: String,
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
    constructor() : this("", "", "", "", "", "", "", "", listOf())
}