package com.team9.soccermanager.model.accessor

import com.team9.soccermanager.model.GameError
import com.team9.soccermanager.model.League
import com.team9.soccermanager.model.LeagueError
import com.team9.soccermanager.model.RankingRow

interface LeagueDao {
    suspend fun getLeagueById(id: String) : League?
    suspend fun getLeagueByInviteCode(code: String) : League?
    suspend fun createLeague(leagueName: String) : League?
    suspend fun leagueExists(leagueName: String) : LeagueError
    suspend fun updateLeague(league: League) : Boolean
    suspend fun getGames(): Pair<GameError, List<Game>>
    suspend fun getGame(id: Int): Pair<GameError, Game>
    fun getGameFromLoaded(id: Int): Game
    suspend fun writeGame(game: Game, newGame: Boolean = false): GameError
    suspend fun deleteGame(gameId: Int): GameError
    suspend fun getRankingsData(onResult: (List<RankingRow>) -> Unit)
}