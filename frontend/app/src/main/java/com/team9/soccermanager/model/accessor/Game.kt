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
    val winner: Winner
) {
    constructor(): this(
        -1, "", GeoPoint(0.0, 0.0), "",
        "", "", "", Timestamp.now(), Winner.UNKNOWN
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
        winner = Winner.valueOf(gameMap["winner"] as String),
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
            "winner" to winner.toString()
        )
    }
}