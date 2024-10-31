package com.team9.soccermanager.model.accessor

import com.team9.soccermanager.model.League

interface LeagueDao {
    suspend fun getLeagueById(id: String) : League?
    suspend fun getLeagueByInviteCode(code: String) : League?
    suspend fun createLeague(leagueName: String) : League?
    suspend fun updateLeague(league: League) : Boolean

}