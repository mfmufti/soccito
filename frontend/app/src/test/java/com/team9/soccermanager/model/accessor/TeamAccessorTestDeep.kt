package com.team9.soccermanager.model.accessor

import io.mockk.mockk
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.team9.soccermanager.model.Team
import com.team9.soccermanager.model.TeamCodeError
import com.team9.soccermanager.model.TeamError
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/* Deeper tests for various methods of the TeamAccessor model file */

class TeamAccessorTestDeep {
    private val team: Team = Team()
    private val TEAM_COL = "teams"

    private fun getMockTask(exception: Exception? = null): Task<QuerySnapshot> {
        val task: Task<QuerySnapshot> = mockk(relaxed = true)
        val relaxedSnapshot: QuerySnapshot = mockk(relaxed = true)
        val mockDocumentSnapshot = mockk<DocumentSnapshot>()
        every { task.isComplete } returns true
        every { task.exception } returns exception
        every { task.isCanceled } returns false
        every { task.result } returns relaxedSnapshot

        every { relaxedSnapshot.documents } returns listOf(mockDocumentSnapshot)
        every { mockDocumentSnapshot.toObject<Team>() } returns team

        return task
    }

    private fun getMockTask2(exception: Exception? = null): Task<Void> {
        val task: Task<Void> = mockk(relaxed = true)
        every { task.isComplete } returns true
        every { task.exception } returns exception
        every { task.isCanceled } returns false
        val relaxedVoid: Void = mockk(relaxed = true)
        every { task.result } returns relaxedVoid
        return task
    }

    @BeforeTest
    fun setup() {
        mockkStatic(FirebaseFirestore::class)
        team.apply {
            name = "The team"
            id = "teamxyz"
            gamesPlayed = 5
            code = "code"
        }
    }

    // Tests the getTeamById function by verifying it retrieves the correct team from Firestore based on the provided team ID.
    @Test
    fun getTeamById() = runTest {
        val mockRepo = mockk<FirebaseFirestore>()
        val mockRef = mockk<CollectionReference>()
        val mockQuery = mockk<Query>()
        every { Firebase.firestore.collection("teams") } returns mockRef
        every { mockRepo.collection("teams") }  returns mockRef
        every { mockRef.whereEqualTo("id", ofType<String>()) } returns mockQuery
        every { mockQuery.get() } returns getMockTask()
        assertEquals(TeamAccessor.getTeamById("teamxyz"), team)
    }

    // Tests the createTeam function by verifying it creates a new team document in Firestore with the correct data and returns the newly created team object.
    @Test
    fun `createTeam creates and returns new team`() = runTest {
        val teamName = "New Team"
        val leagueId = "league123"
        val teamId = "team123"
        val userId = "user123"

        mockkStatic(Firebase::class)
        val mockAuth = mockk<FirebaseAuth>()
        mockkStatic(FirebaseAuth::class)
        every { FirebaseAuth.getInstance() } returns mockAuth
        every { mockAuth.uid } returns userId

        val teamDoc = mockk<DocumentReference>()
        val collectionRef = mockk<CollectionReference>()

        every { Firebase.firestore.collection(TEAM_COL) } returns collectionRef
        every { collectionRef.document() } returns teamDoc
        every { teamDoc.id } returns teamId
        coEvery { teamDoc.set(any<Team>()) } returns getMockTask2()

        val result = TeamAccessor.createTeam(teamName, leagueId)

        assertEquals(teamName, result?.name)
        assertEquals(leagueId, result?.leagueId)
        assertEquals(userId, result?.coachIds?.first())
        assertEquals(teamId, result?.id)

        verify { teamDoc.set(any<Team>()) }
        verify { mockAuth.uid }
    }

    // Tests the teamExists function by verifying it checks if a team with the given name already exists in the specified league.
    @Test
    fun teamExistsTest() = runTest {
        val mockFirestore = mockk<FirebaseFirestore>()
        val mockLeagueCollection = mockk<CollectionReference>()
        val mockTeamCollection = mockk<CollectionReference>()
        val mockLeagueDoc = mockk<DocumentReference>()
        val mockTeamDoc = mockk<DocumentReference>()

        val mockLeagueSnapshot = mockk<DocumentSnapshot>()
        every { mockLeagueSnapshot.exists() } returns true
        every { mockLeagueSnapshot.metadata.isFromCache } returns false
        every { mockLeagueSnapshot["teamIds"] } returns listOf("team1", "team2")

        val mockTeamSnapshot1 = mockk<DocumentSnapshot>()
        every { mockTeamSnapshot1.exists() } returns true
        every { mockTeamSnapshot1["name"] } returns "Test Team"

        val mockTeamSnapshot2 = mockk<DocumentSnapshot>()
        every { mockTeamSnapshot2.exists() } returns true
        every { mockTeamSnapshot2["name"] } returns "Other Team"

        val leagueTask = mockk<Task<DocumentSnapshot>>(relaxed = true)
        val teamTask1 = mockk<Task<DocumentSnapshot>>(relaxed = true)
        val teamTask2 = mockk<Task<DocumentSnapshot>>(relaxed = true)

        every { leagueTask.isComplete } returns true
        every { leagueTask.isCanceled } returns false
        every { leagueTask.exception } returns null
        every { leagueTask.result } returns mockLeagueSnapshot

        every { teamTask1.isComplete } returns true
        every { teamTask1.isCanceled } returns false
        every { teamTask1.exception } returns null
        every { teamTask1.result } returns mockTeamSnapshot1

        every { teamTask2.isComplete } returns true
        every { teamTask2.isCanceled } returns false
        every { teamTask2.exception } returns null
        every { teamTask2.result } returns mockTeamSnapshot2

        mockkStatic(FirebaseFirestore::class)
        every { Firebase.firestore } returns mockFirestore
        every { mockFirestore.collection("leagues") } returns mockLeagueCollection
        every { mockLeagueCollection.document("leagueId") } returns mockLeagueDoc
        every { mockLeagueDoc.get() } returns leagueTask

        every { mockFirestore.collection("teams") } returns mockTeamCollection
        every { mockTeamCollection.document("team1").get() } returns teamTask1
        every { mockTeamCollection.document("team2").get() } returns teamTask2

        val result = TeamAccessor.teamExists("Test Team", "leagueId")

        assertEquals(TeamError.EXISTS, result)
    }

    // Tests the teamCodeExists function by verifying it handles different scenarios: network error, team code exists, and team code does not exist.
    @Test
    fun `teamCodeExists handles network error, exists and non-existing cases`() = runTest {
        val mockDocumentSnapshot: DocumentSnapshot = mockk(relaxed = true)

        val mockTaskNetwork: Task<DocumentSnapshot> = mockk(relaxed = true)
        every { mockTaskNetwork.isComplete } returns true
        every { mockTaskNetwork.exception } returns null
        every { mockTaskNetwork.isCanceled } returns false
        every { mockTaskNetwork.result } returns mockDocumentSnapshot
        every { mockDocumentSnapshot.metadata.isFromCache } returns true
        every { mockDocumentSnapshot.exists() } returns false

        val mockFirestore = mockk<FirebaseFirestore>(relaxed = true)
        every { mockFirestore.collection("teams").document("mockTeamCode").get() } returns mockTaskNetwork

        mockkStatic(Firebase::class)
        every { Firebase.firestore } returns mockFirestore

        val result = TeamAccessor.teamCodeExists("mockTeamCode")

        assertEquals(TeamCodeError.NETWORK, result)

        val mockTaskExists: Task<DocumentSnapshot> = mockk(relaxed = true)
        every { mockTaskExists.isComplete } returns true
        every { mockTaskExists.exception } returns null
        every { mockTaskExists.isCanceled } returns false
        every { mockTaskExists.result } returns mockDocumentSnapshot
        every { mockDocumentSnapshot.metadata.isFromCache } returns false
        every { mockDocumentSnapshot.exists() } returns true

        val resultExists = TeamAccessor.teamCodeExists("mockTeamCode")

        assertEquals(TeamCodeError.NONE, resultExists)

        val mockTaskNotExists: Task<DocumentSnapshot> = mockk(relaxed = true)
        every { mockTaskNotExists.isComplete } returns true
        every { mockTaskNotExists.exception } returns null
        every { mockTaskNotExists.isCanceled } returns false
        every { mockTaskNotExists.result } returns mockDocumentSnapshot
        every { mockDocumentSnapshot.metadata.isFromCache } returns false
        every { mockDocumentSnapshot.exists() } returns false

        val resultNotExists = TeamAccessor.teamCodeExists("mockTeamCode")

        assertEquals(TeamCodeError.NOT_EXIST, resultNotExists)
    }

    // Tests the updateTeam function by verifying it updates the team document in Firestore with the provided data and returns true on success.
    @Test
    fun `updateTeam updates team and returns true`() = runTest {
        val mockTask = getMockTask2()
        every { Firebase.firestore.collection(TEAM_COL).document(any()).set(any(), SetOptions.merge()) } returns mockTask

        val team = Team()
        team.id = "team1"

        val result = TeamAccessor.updateTeam(team)

        assertTrue(result)
    }

    @AfterTest
    fun completed() {
        unmockkAll()
    }
}