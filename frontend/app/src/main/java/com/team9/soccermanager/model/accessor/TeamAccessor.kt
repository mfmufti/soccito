package com.team9.soccermanager.model.accessor

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.team9.soccermanager.model.Team
import kotlinx.coroutines.tasks.await

object TeamAccessor : TeamDao {
    private const val TEAM_COL = "teams"

    override suspend fun getTeamById(id: String): Team  {
        TODO("Not yet implemented")
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

    override suspend fun createTeam(teamName: String): Team? {
        val teamDoc = Firebase.firestore.collection(TEAM_COL).document()

        val team = Team(teamDoc.id, teamName, teamDoc.id, mutableListOf(), mutableListOf(Firebase.auth.uid ?: ""))

        try {
            teamDoc.set(team).await()
            return team
        } catch (e: Exception) {
            // TODO: add proper logging of error here
            e.printStackTrace()
            return null
        }
    }

    // TODO make DAO base class with generic update fn
    // TODO update function replaces entire document, instead add update Fns to change specific fields
    override suspend fun updateTeam(team: Team): Boolean {
        try {
            Firebase.firestore.collection(TEAM_COL).document(team.id).set(team).await()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
}