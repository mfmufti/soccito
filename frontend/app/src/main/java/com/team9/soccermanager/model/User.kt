package com.team9.soccermanager.model

import com.google.firebase.firestore.DocumentReference

data class User(
    var id: String,
    val email: String,
    val fullname: String,
    val leagueID: String,
    val leagueName: String,
    var teamID: String,
    val teamName: String,
    val type: String,
    val chats: List<DocumentReference>
) {
    // need to provide no-arg constructor to support deserialization with Firebase
    constructor() : this("", "", "", "", "", "", "", "", listOf())
}