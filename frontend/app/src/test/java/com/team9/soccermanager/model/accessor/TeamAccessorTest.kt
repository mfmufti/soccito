package com.team9.soccermanager.model.accessor

import io.mockk.mockk
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.events.Publisher
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.FirebaseStorage
import com.team9.soccermanager.model.Account
//import com.team9.soccermanager.model.FormFile
import com.team9.soccermanager.model.GS
import com.team9.soccermanager.model.Team
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.runTest
import java.util.UUID
import kotlin.test.Ignore
import kotlin.test.assertEquals

class TeamAccessorTest {
    private val team: Team = Team()
    private val TEAM_COL = "teams"
    private var _lastAccessedTeam : Team? = null

    suspend fun getTeamByIdd(fire: FirebaseFirestore = Firebase.firestore, id: String): Team?  {
        if(_lastAccessedTeam?.id != id) {
//            println("wrong id")
            try {
                val query = fire.collection(TEAM_COL).whereEqualTo("id", id).get().await()
                _lastAccessedTeam = query.documents[0].toObject<Team>()
            } catch (e: Exception) {
                e.printStackTrace()
                _lastAccessedTeam = null
            }
        }
//        println("returning ...")
        return _lastAccessedTeam
    }

    @BeforeTest
    fun setup() {
//        mockkObject(fire)
//        mockkObject(Firebase.firestore)
        team.name = "The team"
        team.id = "teamxyz"
        team.gamesPlayed = 5
        team.code = "code"
    }

    @Test
    fun getTeamById() = runTest {
        val mockRepo = mockk<FirebaseFirestore>()
        val mockRef = mockk<CollectionReference>()
        val mockQuery = mockk<Query>()
        val mockTask = mockk<Task<QuerySnapshot>>()
        val mockDocumentSnapshot = mockk<DocumentSnapshot>()
        val mockQuerySnapshot = mockk<QuerySnapshot>()

        every { mockRepo.collection("teams") }  returns mockRef
        every { mockRef.whereEqualTo("id", ofType<String>()) } returns mockQuery
        every { mockQuery.get() } returns mockTask
        coEvery { mockTask.await() } returns mockQuerySnapshot
        every { mockQuerySnapshot.documents } returns listOf(mockDocumentSnapshot)
        every { mockDocumentSnapshot.toObject<Team>() } returns team
        mockkStatic("retrofit2.KotlinExtensions")

        assertEquals(getTeamByIdd(mockRepo, "teamxyz"), team)

        verify { Firebase.firestore.collection("teams") }
        verify { mockRef.whereEqualTo("id", ofType<String>()) }
        verify { mockQuery.get() }
        coVerify { mockTask.await() }
        verify { mockQuerySnapshot.documents }
        verify { mockQuerySnapshot.documents[0].toObject<Team>() }
    }

    @Ignore
    @Test
    fun getTeamByInviteCode() {

    }

    @Ignore
    @Test
    fun createTeam() {
    }

    @Ignore
    @Test
    fun updateTeam() {
    }

    @Ignore
    @Test
    fun uploadForm() {
    }

    @Ignore
    @Test
    fun listenForUpdates() {
    }

    @AfterTest
    fun completed() {
        unmockkAll()
    }
}