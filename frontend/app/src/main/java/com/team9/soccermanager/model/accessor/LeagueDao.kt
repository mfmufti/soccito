package com.team9.soccermanager.model.accessor

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import com.team9.soccermanager.model.GameError
import com.team9.soccermanager.model.League
import com.team9.soccermanager.model.LeagueError
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

data class Game(
    val index: Int,
    val address: String,
    val geopoint: GeoPoint,
    val team1ID: String,
    val team2ID: String,
    val team1Name: String,
    val team2Name: String,
    val timestamp: Timestamp,
    val winnerID: String?,
    val winnerName: String?
) {
    constructor(): this(-1, "", GeoPoint(0.0, 0.0), "", "", "", "", Timestamp.now(), null, null)
}

interface LeagueDao {
    suspend fun getLeagueById(id: String) : League?
    suspend fun getLeagueByInviteCode(code: String) : League?
    suspend fun createLeague(leagueName: String) : League?
    suspend fun leagueExists(leagueName: String) : LeagueError
    suspend fun updateLeague(league: League) : Boolean
    suspend fun getGames(id: String): Pair<GameError, List<Game>>
    suspend fun getGame(id: String, index: Int): Pair<GameError, Game>
    fun getGameFromLoaded(index: Int): Game
}