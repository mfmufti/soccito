package com.team9.soccermanager.model

data class User(
    var email: String,
    var fullname: String,
    var leagueID: String,
    val leagueName: String,
    var teamID: String,
    val teamName: String,
    var type: String,
) {
    // need to provide no-arg constructor to support deserialization with Firebase
    constructor() : this("", "", "", "", "", "", "")
}