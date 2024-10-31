package com.team9.soccermanager.model.accessor

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.team9.soccermanager.model.League
import kotlinx.coroutines.tasks.await

object LeagueAccessor : LeagueDao {
    private const val LEAGUE_COL = "leagues"

    override suspend fun getLeagueById(id: String): League? {
        TODO("Not yet implemented")
    }

    override suspend fun getLeagueByInviteCode(code: String): League? {
        try {
            val query = Firebase.firestore.collection(LEAGUE_COL).whereEqualTo("code", code).get().await()
            return query.documents[0].toObject<League>()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override suspend fun createLeague(leagueName: String): League? {
        val leagueDoc = Firebase.firestore.collection(LEAGUE_COL).document()

        // TODO: generate code instead of using doc id
        //  (not secure, but using for now since we are guaranteed uniqueness)
        val league =
            League(leagueDoc.id, leagueName, leagueDoc.id, mutableListOf(), mutableListOf(Firebase.auth.uid ?: ""))

        try {
            leagueDoc.set(league).await()
            return league
        } catch (e: Exception) {
            // TODO: add proper logging of error here
            e.printStackTrace()
            return null
        }
    }

    override suspend fun updateLeague(league: League): Boolean {
        try {
            Firebase.firestore.collection(LEAGUE_COL).document(league.id).set(league).await()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
}