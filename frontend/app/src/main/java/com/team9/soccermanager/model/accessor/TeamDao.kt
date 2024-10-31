package com.team9.soccermanager.model.accessor

import com.team9.soccermanager.model.Team

interface TeamDao {
    suspend fun getTeamById(id: String) : Team?
    suspend fun getTeamByInviteCode(code: String) : Team?
    suspend fun createTeam(teamName: String) : Team?
    suspend fun updateTeam(team: Team) : Boolean
}