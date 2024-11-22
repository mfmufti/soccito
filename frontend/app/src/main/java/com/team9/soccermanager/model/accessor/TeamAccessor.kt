package com.team9.soccermanager.model.accessor

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.FirebaseStorage
import com.team9.soccermanager.model.Account
import com.team9.soccermanager.model.FormFile
import com.team9.soccermanager.model.GS
import com.team9.soccermanager.model.Team
import com.team9.soccermanager.model.TeamCodeError
import com.team9.soccermanager.model.TeamError
import kotlinx.coroutines.tasks.await
import java.io.IOException
import java.util.UUID

object TeamAccessor : TeamDao {
    private const val TEAM_COL = "teams"
    private const val LEAGUE_COL = "leagues"
    private var _lastAccessedTeam : Team? = null

    override suspend fun getTeamById(id: String): Team?  {
        if(_lastAccessedTeam?.id != id) {
//            println("wrong id")
            try {
                val query = Firebase.firestore.collection(TEAM_COL).whereEqualTo("id", id).get().await()
                _lastAccessedTeam = query.documents[0].toObject<Team>()
            } catch (e: Exception) {
                e.printStackTrace()
                _lastAccessedTeam = null
            }
        }
//        println("returning ...")
        return _lastAccessedTeam
    }

    override suspend fun getTeamByInviteCode(code: String): Team? {
        try {
            val query = Firebase.firestore.collection(TEAM_COL).whereEqualTo("code", code).get().await()
            return query.documents[0].toObject<Team>()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override suspend fun createTeam(teamName: String, leagueId: String): Team? {
        val teamDoc = Firebase.firestore.collection(TEAM_COL).document()

        val team = Team(teamDoc.id, teamName, teamDoc.id, mutableListOf(), mutableListOf(Firebase.auth.uid ?: ""), leagueId, 0, 0, 0, 0, 0, mutableListOf(), mutableListOf())

        try {
            teamDoc.set(team).await()
            return team
        } catch (e: Exception) {
            // TODO: add proper logging of error here
            e.printStackTrace()
            return null
        }
    }

    override suspend fun teamExists(teamName: String, leagueID: String): TeamError {
        return try {
            val doc = Firebase.firestore.collection(LEAGUE_COL).document(leagueID).get().await()
            if (doc.metadata.isFromCache) {
                TeamError.NETWORK
            } else if (!doc.exists()) {
                TeamError.BAD_JOIN_CODE
            } else {
                var ret = TeamError.NONE
                for (teamID in doc["teamIds"] as List<*>) {
                    val doc2 = Firebase.firestore.collection(TEAM_COL).document(teamID as String).get().await()
                    if (doc.metadata.isFromCache) {
                        ret = TeamError.NETWORK
                        break
                    } else if (doc2.exists() && doc2["name"] == teamName) {
                        ret = TeamError.EXISTS
                        break
                    }
                }
                ret
            }

        } catch (e: Exception) {
            if (e.message != null && e.message!!.contains("offline")) {
                TeamError.NETWORK
            } else {
                println(e)
                TeamError.UNKNOWN
            }
        }
    }

    override suspend fun teamCodeExists(teamCode: String): TeamCodeError {
        return try {
            val doc = Firebase.firestore.collection(TEAM_COL).document(teamCode).get().await()
            if (doc.metadata.isFromCache) {
                TeamCodeError.NETWORK
            } else if (doc.exists()) {
                TeamCodeError.NONE
            } else {
                TeamCodeError.NOT_EXIST
            }
        } catch (e: Exception) {
            if (e.message != null && e.message!!.contains("offline")) {
                TeamCodeError.NETWORK
            } else {
                println(e)
                TeamCodeError.UNKNOWN
            }
        }
    }

    // TODO make DAO base class with generic update fn
    // TODO update function replaces entire document, instead add update Fns to change specific fields
    override suspend fun updateTeam(team: Team): Boolean {
        try {
            Firebase.firestore.collection(TEAM_COL).document(team.id).set(team).await()
            _lastAccessedTeam = team
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    override suspend fun uploadForm(uri: Uri, contentResolver: ContentResolver) {
        // original java code: https://stackoverflow.com/questions/5568874/how-to-extract-the-file-name-from-uri-returned-from-intent-action-get-content/25005243#25005243
        var result: String? = null
        if (uri.scheme.equals("content")) {
            val cursor = contentResolver.query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor?.close();
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        val folderRef = FirebaseStorage.getInstance().getReference("teams")
        val ref = folderRef.child("${_lastAccessedTeam?.id}/${UUID.randomUUID()}_${result}")

        val inputStream = contentResolver.openInputStream(uri)
        inputStream.use {
            val downloadUrl = ref.putBytes(inputStream!!.readBytes()).await().storage.downloadUrl.await().toString()
            //downloadUrl.metadata?.path
            val newFormFile = FormFile(result, downloadUrl, GS.user?.fullname!!, System.currentTimeMillis())
            _lastAccessedTeam?.forms?.add(newFormFile)
            updateTeam(_lastAccessedTeam!!)
        }
    }

    override suspend fun listenForUpdates(onResult: (Team) -> Unit): ListenerRegistration? {
        val id = getTeamById(GS.user?.teamID!!)?.id ?: return null
        val docRef = Firebase.firestore.collection(TEAM_COL).document(id)
        return docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                _lastAccessedTeam = snapshot.toObject<Team>()
                onResult(_lastAccessedTeam!!)
            }
        }
    }
}