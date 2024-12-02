package com.team9.soccermanager.model.accessor

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import com.team9.soccermanager.model.GameStatus

/* This file defines the Game data class, which represents a soccer game in
 the application. It includes constructors, a function to convert the object
 to a map, and a companion object with a factory method for creatingGame
 instances. */

data class Game(
    var id: Int,
    val address: String,
    val geopoint: GeoPoint,
    val team1ID: String,
    val team2ID: String,
    val team1Name: String,
    val team2Name: String,
    val timestamp: Timestamp,
    val team1Score: Int,
    val team2Score: Int,
    val status: GameStatus,
    var team1CoachsNotes: String,
    var team2CoachsNotes: String
) {

    constructor(): this(
        -1, "", GeoPoint(0.0, 0.0), "",
        "", "", "", Timestamp.now(), 0, 0, GameStatus.SCHEDULED, "", ""
    )

    constructor(gameMap: Map<*, *>): this(
        id = (gameMap["id"] as Long).toInt(),
        address = gameMap["address"] as String,
        geopoint = gameMap["geopoint"] as GeoPoint,
        team1ID = gameMap["team1ID"] as String,
        team2ID = gameMap["team2ID"] as String,
        team1Name = gameMap["team1Name"] as String,
        team2Name = gameMap["team2Name"] as String,
        timestamp = gameMap["timestamp"] as Timestamp,
        team1Score = (gameMap["team1Score"] as Long).toInt(),
        team2Score = (gameMap["team2Score"] as Long).toInt(),
        status = GameStatus.valueOf(gameMap["status"] as String),
        team1CoachsNotes = gameMap["team1CoachsNotes"] as String,
        team2CoachsNotes = gameMap["team2CoachsNotes"] as String
    )

    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id.toLong(),
            "address" to address,
            "geopoint" to geopoint,
            "team1ID" to team1ID,
            "team2ID" to team2ID,
            "team1Name" to team1Name,
            "team2Name" to team2Name,
            "timestamp" to timestamp,
            "team1Score" to team1Score.toLong(),
            "team2Score" to team2Score.toLong(),
            "status" to status.toString(),
            "team1CoachsNotes" to team1CoachsNotes,
            "team2CoachsNotes" to team2CoachsNotes
        )
    }

    companion object {
        fun from(id: Int,
            address: String,
            geopoint: GeoPoint,
            team1ID: String,
            team2ID: String,
            team1Name: String,
            team2Name: String,
            timestamp: Timestamp,
            team1Score: Int,
            team2Score: Int,
            status: GameStatus,
            team1CoachsNotes: String,
            team2CoachsNotes: String): Game {
            return Game(id, address, geopoint, team1ID, team2ID, team1Name, team2Name, timestamp, team1Score,
                team2Score, status, team1CoachsNotes, team2CoachsNotes)
        }
    }
}