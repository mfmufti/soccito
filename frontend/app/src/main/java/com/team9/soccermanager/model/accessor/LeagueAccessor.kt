package com.team9.soccermanager.model.accessor

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.SetOptions
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
            League(leagueDoc.id, leagueName, leagueDoc.id, mutableListOf(), mutableListOf(), mutableListOf(Firebase.auth.uid ?: ""))

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
            Firebase.firestore.collection(LEAGUE_COL).document(league.id).set(league, SetOptions.merge()).await()
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
                for (gameRaw in gamesRaw) {
                    val gameMap = gameRaw as Map<*, *>
                    games.add(Game(gameMap))
                }
            }
            lastLoadedGames = games
            return Pair(GameError.NONE, games)
        } catch (e: Exception) {
            return if (e.message != null && e.message!!.contains("offline")) {
                Pair(GameError.NETWORK, listOf())
            } else {
                e.printStackTrace()
                Pair(GameError.UNKNOWN, listOf())
            }
        }
    }

    override suspend fun getGame(id: Int): Pair<GameError, Game> {
        val games = getGames()
        return if (games.first != GameError.NONE) {
            Pair(games.first, Game())
        } else {
            val game = games.second.find { it.id == id }
            if (game == null) {
                Pair(GameError.UNKNOWN, Game())
            } else {
                Pair(GameError.NONE, game)
            }
        }
    }

    override fun getGameFromLoaded(id: Int): Game {
        return lastLoadedGames!!.first { it.id == id }
    }

    override suspend fun writeGame(game: Game, newGame: Boolean): GameError {
        try {
            val docRef = Firebase.firestore.collection(LEAGUE_COL).document(GS.user!!.leagueID)
            val doc = docRef.get().await()
            var gamesRaw = doc["games"]
            if (gamesRaw == null) {
                gamesRaw = listOf<Any>()
            }
            var games = gamesRaw as List<*>
            if (newGame) {
                val id = if (games.isEmpty()) 0 else games.maxOf { (it as Map<*, *>)["id"] as Long }
                game.id = id.toInt()
                games += game.toMap()
            } else {
                var found = false
                for ((i, curGame) in games.withIndex()) {
                    val gameMap = curGame as Map<*, *>
                    if ((gameMap["id"] as Long).toInt() == game.id) {
                        found = true
                        games = games.mapIndexed({ index, elem -> if (index == i) game.toMap() else elem })
                        break
                    }
                }
                if (!found) {
                    games += game.toMap()
                }
            }
            docRef.update("games", games).await()
            lastLoadedGames = games.map { Game(it as Map<*, *>) }
            return GameError.NONE
        } catch (e: Exception) {
            e.printStackTrace()
            if (e.message != null &&  e.message!!.contains("offline")) {
                return GameError.NETWORK
            } else {
                return GameError.UNKNOWN
            }
        }
    }
}