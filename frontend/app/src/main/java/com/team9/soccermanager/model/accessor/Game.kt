package com.team9.soccermanager.model.accessor

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import com.team9.soccermanager.model.Winner

data class Game(
    val index: Int,
    val address: String,
    val geopoint: GeoPoint,
    val team1ID: String,
    val team2ID: String,
    val team1Name: String,
    val team2Name: String,
    val timestamp: Timestamp,
    val score: String,
    val winner: Winner,
    val team1CoachsNotes: String,
    val team2CoachsNotes: String
) {

    constructor(): this(
        -1, "", GeoPoint(0.0, 0.0), "",
        "", "", "", Timestamp.now(), "", Winner.UNKNOWN, "", ""
    )

    constructor(index: Int, gameMap: Map<*, *>): this(
        index = index,
        address = gameMap["address"] as String,
        geopoint = gameMap["geopoint"] as GeoPoint,
        team1ID = gameMap["team1ID"] as String,
        team2ID = gameMap["team2ID"] as String,
        team1Name = gameMap["team1Name"] as String,
        team2Name = gameMap["team2Name"] as String,
        timestamp = gameMap["timestamp"] as Timestamp,
        score = gameMap["score"] as String,
        winner = Winner.valueOf(gameMap["winner"] as String),
        team1CoachsNotes = gameMap["team1CoachsNotes"] as String,
        team2CoachsNotes = gameMap["team2CoachsNotes"] as String
    )

    fun toMap(): Map<String, Any> {
        return mapOf(
            "address" to address,
            "geopoint" to geopoint,
            "team1ID" to team1ID,
            "team2ID" to team2ID,
            "team1Name" to team1Name,
            "team2Name" to team2Name,
            "timestamp" to timestamp,
            "score" to score,
            "winner" to winner.toString(),
            "team1CoachsNotes" to team1CoachsNotes,
            "team2CoachsNotes" to team2CoachsNotes

        )
    }
}