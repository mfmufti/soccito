package com.team9.soccermanager.model

data class FormFile(val fileName: String, val downloadUrl: String, val author: String, val datePosted: Long) {
    constructor() : this("", "","",0)
}

data class Announcement (val content: String, val authorName: String, val datePosted: Long) {
    constructor() : this("", "",0)
}

data class AvailView (val playerId: String, val playerName: String, var playerAvail: PlrAvail) {
    constructor() : this("", "", PlrAvail())
}

data class RankingView(val id: String, val teamName: String, val gp: Long, val wins: Long, val losses: Long, val draws: Long, val pts: Long)

data class Team (
    var id: String,
    var name: String,
    var code: String,
    val playerIds: MutableList<String>,
    val coachIds: MutableList<String>,
    val leagueId: String,
    var wins: Int,
    var losses: Int,
    var draws: Int,
    var gamesPlayed: Int,
    var points: Int,
    val announcements: MutableList<Announcement>,
    val forms: MutableList<FormFile>
) {

    // need to provide no-arg constructor to support deserialization with Firebase
    constructor() : this("", "", "", mutableListOf(), mutableListOf(),
        "", 0, 0, 0, 0, 0, mutableListOf(), mutableListOf())
}