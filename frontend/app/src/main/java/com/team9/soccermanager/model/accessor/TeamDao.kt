package com.team9.soccermanager.model.accessor

import android.content.ContentResolver
import android.net.Uri
import com.team9.soccermanager.model.Team
import com.team9.soccermanager.model.TeamCodeError
import com.team9.soccermanager.model.TeamError

interface TeamDao {
    suspend fun getTeamById(id: String) : Team?
    suspend fun getTeamByInviteCode(code: String) : Team?
    suspend fun createTeam(teamName: String, leagueId: String) : Team?
    suspend fun teamExists(teamName: String, leagueID: String): TeamError
    suspend fun teamCodeExists(teamCode: String): TeamCodeError
    suspend fun updateTeam(team: Team) : Boolean
    suspend fun uploadForm(uri: Uri, contentResolver: ContentResolver) : Unit
    suspend fun listenForUpdates(onResult: (Team) -> Unit)
}