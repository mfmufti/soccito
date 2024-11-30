package com.team9.soccermanager.model.accessor

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import androidx.compose.runtime.mutableStateListOf
import com.google.firebase.Firebase
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.FirebaseStorage
import com.team9.soccermanager.model.AvailView
import com.team9.soccermanager.model.Availability
import com.team9.soccermanager.model.Form
import com.team9.soccermanager.model.FormUpload
import com.team9.soccermanager.model.GS
import com.team9.soccermanager.model.PlrAvail
import com.team9.soccermanager.model.RankingView
import com.team9.soccermanager.model.Team
import com.team9.soccermanager.model.TeamCodeError
import com.team9.soccermanager.model.TeamError
import kotlinx.coroutines.tasks.await
import java.util.UUID

object TeamAccessor : TeamDao {
    private const val TEAM_COL = "teams"
    private const val LEAGUE_COL = "leagues"
    private const val USER_COL = "users"
    private var _lastAccessedTeam : Team? = null

    override suspend fun getTeamById(id: String, requireConnection: Boolean): Team?  {
        if(_lastAccessedTeam?.id != id) {
            try {
                val query = Firebase.firestore.collection(TEAM_COL).whereEqualTo("id", id).get().await()
                _lastAccessedTeam = query.documents[0].toObject<Team>()
            } catch (e: Exception) {
                if (requireConnection && e.message != null && e.message!!.contains("offline")) {
                    throw FirebaseNetworkException("bad connection")
                }
                e.printStackTrace()
                _lastAccessedTeam = null
            }
        }
//        println("returning ...")
        return _lastAccessedTeam
    }

    override fun addSnapshotListener(id: String, snapshotListener: (DocumentSnapshot?) -> Unit): ListenerRegistration {
        return Firebase.firestore.collection(TEAM_COL).document(id).addSnapshotListener({ a, b -> snapshotListener(a) })
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
        val team = Team(teamDoc.id, teamName, teamDoc.id, mutableListOf(), mutableListOf(), mutableListOf(Firebase.auth.uid ?: ""), leagueId, 0, 0, 0, 0, 0, mutableListOf(), mutableListOf())
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
            Firebase.firestore.collection(TEAM_COL).document(team.id).set(team, SetOptions.merge()).await()
            _lastAccessedTeam = team
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    override suspend fun uploadForm(uri: Uri, contentResolver: ContentResolver, id: Int, progressListener: (Double) -> Unit, canceler: (() -> Unit) -> Unit) {
        // original java code: https://stackoverflow.com/questions/5568874/how-to-extract-the-file-name-from-uri-returned-from-intent-action-get-content/25005243#25005243
        var result: String? = null
        if (uri.scheme.equals("content")) {
            var cursor: Cursor? = null
            try {
                cursor = contentResolver.query(uri, null, null, null, null)
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME));
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                cursor?.close()
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
        _lastAccessedTeam = getTeamById(GS.user!!.teamID)
        val ref = folderRef.child("${_lastAccessedTeam?.id}/${UUID.randomUUID()}_${result}")
        val inputStream = contentResolver.openInputStream(uri)
        inputStream.use {
            val uploadTask = ref.putBytes(inputStream!!.readBytes())
            uploadTask.addOnProgressListener({progressListener(1.0 * it.bytesTransferred / it.totalByteCount)})
            canceler({ uploadTask.cancel() })
            val downloadUrl = uploadTask.await().storage.downloadUrl.await().toString()
            val newFormUpload = FormUpload(downloadUrl, GS.user!!.id, GS.user!!.fullname, Timestamp.now())
            _lastAccessedTeam!!.forms.first { it.id == id }.apply {
                uploads.retainAll {
                    if (it.playerID == GS.user!!.id) {
                        FirebaseStorage.getInstance().getReferenceFromUrl(it.link).delete()
                        false
                    } else true
                }
                uploads.add(newFormUpload)
            }
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

    override fun updateUserAvail(id: String, avail: Availability, reason: String) {
        val db = Firebase.firestore
        val doc = db.collection(USER_COL).document(id)
        val newAvailValue = PlrAvail(avail, reason)
        doc.update("playerAvail", newAvailValue).addOnSuccessListener {
            println("Successfully updated availability")
        }.addOnFailureListener {
            println("Failed update availability")
        }
    }

    override suspend fun getPlayerAvail(onResult: (List<AvailView>) -> Unit, onError: (TeamError) -> Unit) {
        try {
            val tid = GS.user!!.teamID
            val db = Firebase.firestore
            val currTeam = db.collection(TEAM_COL).document(tid).get().await()
            if (currTeam.metadata.isFromCache) {
                onError(TeamError.NETWORK)
                return
            }
            if (currTeam.data == null) {
                onError(TeamError.UNKNOWN)
                return
            }
            val res: MutableList<AvailView> = mutableListOf()
            for(pid in currTeam.data!!["playerIds"] as List<*>) {
                val currPid = pid as String
                val currPlr = db.collection(USER_COL).document(currPid).get().await()
                if (currPlr.data == null) {
                    onError(TeamError.UNKNOWN)
                    return
                }
                val currPlrName = currPlr.data!!["fullname"] as String
                val currPlrAvail = PlrAvail(currPlr.data!!["playerAvail"] as Map<*, *>)
                res.add(AvailView(currPid, currPlrName, currPlrAvail))
            }
            onResult(res)
        } catch(e: Exception) {
            onError(TeamError.UNKNOWN)
        }
    }

    override suspend fun getRankingsData(onResult: (List<RankingView>) -> Unit) {
        val db = Firebase.firestore
        val teamsList = mutableStateListOf<RankingView>()
        try {
            val leagueDocument = db.collection(LEAGUE_COL).document(GS.user!!.leagueID).get().await()
            val tList = leagueDocument.data?.get("teamIds") as? List<*>
            if (tList != null) {
                for (t in tList) {
                    val teamDocument = db.collection("teams").document(t as String).get().await()
                    if (teamDocument.data != null) {
                        val tid = teamDocument.data!!["id"] as String
                        val currName = teamDocument.data!!["name"] as String
                        val gp = teamDocument.data!!["gamesPlayed"] as Long
                        val wins = teamDocument.data!!["wins"] as Long
                        val losses = teamDocument.data!!["losses"] as Long
                        val draws = teamDocument.data!!["draws"] as Long
                        val pts = teamDocument.data!!["points"] as Long
                        teamsList.add(RankingView(tid, currName, gp, wins, losses, draws, pts))
                    }
                }
            }
        } catch (e: Exception) {
            println("Error: ${e.message}")
        }
        teamsList.sortByDescending { it.pts }
        onResult(teamsList)
    }

    override fun getNotificationTokens(onTokens: (tokens: List<String>) -> Unit) {
        if (_lastAccessedTeam != null) {
            val userIds = _lastAccessedTeam!!.playerIds
            Firebase.firestore.collection(USER_COL)
                .whereIn("id", userIds)
                .addSnapshotListener {
                    snapshot, ex -> if(snapshot != null) onTokens(snapshot.documents.mapNotNull { document -> document.getString("notificationToken") })
                }

        }
    }
}