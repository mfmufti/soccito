package com.team9.soccermanager.model

data class Team (
    val id: String,
    val name: String,
    val code: String,
    val playerIds: MutableList<String>,
    val coachIds: MutableList<String>,
    val leagueId: String,
    var wins: Int,
    var losses: Int,
    var draws: Int,
    var gamesPlayed: Int,
    var points: Int
) {
    // need to provide no-arg constructor to support deserialization with Firebase
    constructor() : this("", "", "", mutableListOf(), mutableListOf(), "", 0, 0, 0, 0, 0)
}