package com.team9.soccermanager.model.accessor

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.team9.soccermanager.model.GS
import com.team9.soccermanager.model.GameError
import com.team9.soccermanager.model.League
import com.team9.soccermanager.model.LeagueError
import kotlinx.coroutines.tasks.await

object LeagueAccessor : LeagueDao {
    private const val LEAGUE_COL = "leagues"
    private var lastLoadedGames: List<Game>? = null

    override suspend fun getLeagueById(id: String): League? {
        try {
            val query = Firebase.firestore.collection(LEAGUE_COL).whereEqualTo("id", id).get().await()
            return query.documents[0].toObject<League>()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override suspend fun getLeagueByInviteCode(code: String): League? {
        try {
            val query = Firebase.firestore.collection(LEAGUE_COL).whereEqualTo("code", code).get().await()
            return query.documents[0].toObject<League>()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override suspend fun createLeague(leagueName: String): League? {
        val leagueDoc = Firebase.firestore.collection(LEAGUE_COL).document()

        // TODO: generate code instead of using doc id
        //  (not secure, but using for now since we are guaranteed uniqueness)
        val league =
            League(leagueDoc.id, leagueName, leagueDoc.id, mutableListOf(), mutableListOf(Firebase.auth.uid ?: ""))

        try {
            leagueDoc.set(league).await()
            return league
        } catch (e: Exception) {
            // TODO: add proper logging of error here
            e.printStackTrace()
            return null
        }
    }

    override suspend fun leagueExists(leagueName: String) : LeagueError {
        return try {
            val res = Firebase.firestore.collection(LEAGUE_COL).whereEqualTo("name", leagueName).get().await()
            if (res.metadata.isFromCache) {
                LeagueError.NETWORK
            } else if (res.isEmpty) {
                LeagueError.NONE
            } else {
                LeagueError.EXISTS
            }
        } catch (e: Exception) {
            if (e.message != null && e.message!!.contains("offline")) {
                LeagueError.NETWORK
            } else {
                println(e)
                LeagueError.UNKNOWN
            }
        }
    }

    override suspend fun updateLeague(league: League): Boolean {
        try {
            Firebase.firestore.collection(LEAGUE_COL).document(league.id).set(league).await()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    override suspend fun getGames(): Pair<GameError, List<Game>> {
        try {
            val doc = Firebase.firestore.collection(LEAGUE_COL).document(GS.user!!.leagueID).get().await()
            if (doc.metadata.isFromCache) {
                return Pair(GameError.NETWORK, listOf())
            } else if (doc["games"] == null) {
                return Pair(GameError.NONE, listOf())
            }
            val games = mutableListOf<Game>()
            val gamesRaw = doc["games"] as? List<*>
            if (gamesRaw != null) {
                for ((index, gameRaw) in gamesRaw.withIndex()) {
                    val gameMap = gameRaw as Map<*, *>
                    games.add(Game(index, gameMap))
                }
            }
            lastLoadedGames = games
            return Pair(GameError.NONE, games)
        } catch (e: Exception) {
            return if (e.message != null && e.message!!.contains("offline")) {
                Pair(GameError.NETWORK, listOf())
            } else {
                Pair(GameError.UNKNOWN, listOf())
            }
        }
    }

    override suspend fun getGame(index: Int): Pair<GameError, Game> {
        val games = getGames()
        return if (games.first != GameError.NONE) {
            Pair(games.first, Game())
        } else if (index < 0 || index >= games.second.size) {
            Pair(GameError.UNKNOWN, Game())
        } else {
            Pair(GameError.NONE, games.second[index])
        }
    }

    override fun getGameFromLoaded(index: Int): Game {
        return lastLoadedGames!![index]
    }

    override suspend fun writeGame(game: Game): GameError {
        try {
            FieldPath.of("games", game.index.toString())
            Firebase.firestore.collection(LEAGUE_COL).document(GS.user!!.leagueID).update(game.toMap())
            return GameError.NONE
        } catch (e: Exception) {
            if (e.message != null &&  e.message!!.contains("offline")) {
                return GameError.NETWORK
            } else {
                println(e)
                return GameError.UNKNOWN
            }
        }
    }
}