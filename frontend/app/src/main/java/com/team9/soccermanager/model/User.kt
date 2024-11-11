package com.team9.soccermanager.model

data class User(
    val email: String,
    val fullname: String,
    val leagueID: String,
    val leagueName: String,
    val teamID: String,
    val teamName: String,
    val type: String,
) {
    // need to provide no-arg constructor to support deserialization with Firebase
    constructor() : this("", "", "", "", "", "", "")
}