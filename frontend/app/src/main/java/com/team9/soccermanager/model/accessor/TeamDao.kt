package com.team9.soccermanager.model.accessor

import android.content.ContentResolver
import android.net.Uri
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import com.team9.soccermanager.model.AvailView
import com.team9.soccermanager.model.Availability
import com.team9.soccermanager.model.PlrAvail
import com.team9.soccermanager.model.RankingView
import com.team9.soccermanager.model.Team
import com.team9.soccermanager.model.TeamCodeError
import com.team9.soccermanager.model.TeamError

interface TeamDao {
    suspend fun getTeamById(id: String, requireConnection: Boolean = false) : Team?
    fun addSnapshotListener(id: String, snapshotListener: (DocumentSnapshot?) -> Unit): ListenerRegistration
    suspend fun getTeamByInviteCode(code: String) : Team?
    suspend fun createTeam(teamName: String, leagueId: String) : Team?
    suspend fun teamExists(teamName: String, leagueID: String): TeamError
    suspend fun teamCodeExists(teamCode: String): TeamCodeError
    suspend fun updateTeam(team: Team) : Boolean
    suspend fun uploadForm(uri: Uri, contentResolver: ContentResolver, id: Int, progressListener: (Double) -> Unit = {}, canceler: (() -> Unit) -> Unit = {})
    suspend fun listenForUpdates(onResult: (Team) -> Unit): ListenerRegistration?
    suspend fun getPlayerAvail(onResult: (List<AvailView>) -> Unit, onError: (TeamError) -> Unit)
    suspend fun getRankingsData(onResult: (List<RankingView>) -> Unit)
    fun getNotificationTokens(onTokens: (tokens: List<String>) -> Unit)
    fun updateUserAvail(id: String, avail: Availability, reason: String)

}