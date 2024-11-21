package com.team9.soccermanager.model.accessor

import com.team9.soccermanager.model.League
import com.team9.soccermanager.model.LeagueError

interface LeagueDao {
    suspend fun getLeagueById(id: String) : League?
    suspend fun getLeagueByInviteCode(code: String) : League?
    suspend fun createLeague(leagueName: String) : League?
    suspend fun leagueExists(leagueName: String) : LeagueError
    suspend fun updateLeague(league: League) : Boolean
}