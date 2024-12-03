package com.team9.soccermanager.model.accessor

import com.google.firebase.firestore.ListenerRegistration
import com.team9.soccermanager.model.AvailView
import kotlinx.coroutines.test.runTest
import kotlin.test.*
import kotlin.test.assertEquals
import com.team9.soccermanager.model.Team
import com.team9.soccermanager.model.TeamCodeError
import com.team9.soccermanager.model.TeamError
import io.mockk.*

class TeamTest {
    val testInvite = "kzOcrvvilvTOU1VzxF9x"

    @BeforeTest
    fun setup() {
        mockkObject(TeamAccessor)
    }

    @Test
    fun `test getTeamById`() = runTest {
        val mockTeam = mockk<Team>()
        every { mockTeam.name } returns "Barcelona"
        coEvery { TeamAccessor.getTeamByInviteCode(testInvite) } returns mockTeam

        val team = TeamAccessor.getTeamByInviteCode(testInvite)
        assertEquals("Barcelona", team?.name)
    }

    @Test
    fun `test addSnapshotListener`() {
        val mockListener = mockk<ListenerRegistration>()

        every { TeamAccessor.addSnapshotListener("mockId", any()) } returns mockListener

        val result = TeamAccessor.addSnapshotListener("mockId") { println("Snapshot triggered") }

        assertEquals(mockListener, result)
    }

    @Test
    fun `test getTeamByInviteCode`() = runTest {
        val mockTeam = mockk<Team>()
        every { mockTeam.name } returns "MockTeam"

        coEvery { TeamAccessor.getTeamByInviteCode("mockCode") } returns mockTeam

        val result = TeamAccessor.getTeamByInviteCode("mockCode")

        assertEquals("MockTeam", result?.name)
    }

    @Test
    fun `test createTeam`() = runTest {
        val mockTeam = mockk<Team>()
        every { mockTeam.name } returns "New Team"

        coEvery { TeamAccessor.createTeam("New Team", "leagueId") } returns mockTeam

        val result = TeamAccessor.createTeam("New Team", "leagueId")

        assertEquals("New Team", result?.name)
    }

    @Test
    fun `test teamExists`() = runTest {
        coEvery { TeamAccessor.teamExists("teamName", "leagueId") } returns TeamError.EXISTS

        val result = TeamAccessor.teamExists("teamName", "leagueId")

        assertEquals(TeamError.EXISTS, result)
    }

    @Test
    fun `test teamCodeExists`() = runTest {
        coEvery { TeamAccessor.teamCodeExists("mockCode") } returns TeamCodeError.NONE

        val result = TeamAccessor.teamCodeExists("mockCode")

        assertEquals(TeamCodeError.NONE, result)
    }

    @Test
    fun `test updateTeam`() = runTest {
        coEvery { TeamAccessor.updateTeam(any()) } returns true

        val result = TeamAccessor.updateTeam(mockk())

        assertTrue(result)
    }

    @Test
    fun `test uploadForm`() = runTest {
        coEvery {
            TeamAccessor.uploadForm(
                any(), any(), any(), any(), any()
            )
        } returns Unit

        TeamAccessor.uploadForm(mockk(), mockk(), 1, {}, {})

        // No assertion needed, as the function is mocked to do nothing.
    }

    @Test
    fun `test listenForUpdates`() = runTest {
        val mockListener = mockk<ListenerRegistration>()

        coEvery { TeamAccessor.listenForUpdates(any()) } returns mockListener

        val result = TeamAccessor.listenForUpdates { println("Update received") }

        assertEquals(mockListener, result)
    }

    @Test
    fun `test updateUserAvail`() = runTest {
        justRun { TeamAccessor.updateUserAvail("mockId", any(), "mockReason") }

        TeamAccessor.updateUserAvail("mockId", mockk(), "mockReason")

        // Verifying that the function was called
        verify { TeamAccessor.updateUserAvail("mockId", any(), "mockReason") }
    }

    @Test
    fun `test getPlayerAvail`() = runTest {
        coEvery {
            TeamAccessor.getPlayerAvail(any(), any())
        } answers {
            firstArg<(List<AvailView>) -> Unit>().invoke(emptyList())
        }

        TeamAccessor.getPlayerAvail(
            { result -> assertTrue(result.isEmpty()) },
            { error -> fail("Unexpected error: $error") }
        )
    }

    @Test
    fun `test getNotificationTokens`() = runTest  {
        justRun { TeamAccessor.getNotificationTokens(any()) }

        TeamAccessor.getNotificationTokens { tokens -> assertTrue(tokens.isEmpty()) }

        // Verifying that the function was called
        verify { TeamAccessor.getNotificationTokens(any()) }
    }

    @AfterTest
    fun completion() {
        unmockkAll()
    }
}