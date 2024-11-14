package com.team9.soccermanager.model.accessor

import android.content.ContentResolver
import android.net.Uri
import com.team9.soccermanager.model.Team

interface TeamDao {
    suspend fun getTeamById(id: String) : Team?
    suspend fun getTeamByInviteCode(code: String) : Team?
    suspend fun createTeam(teamName: String, leagueId: String) : Team?
    suspend fun updateTeam(team: Team) : Boolean
    suspend fun uploadForm(uri: Uri, contentResolver: ContentResolver) : Unit
}