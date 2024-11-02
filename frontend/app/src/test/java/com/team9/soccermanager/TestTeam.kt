package com.team9.soccermanager

import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.runBlocking
import kotlin.test.*
import kotlin.test.assertEquals
import com.team9.soccermanager.model.accessor.TeamAccessor
import com.team9.soccermanager.model.Team
import io.mockk.*

class TestTeam {
    val testInvite = "kzOcrvvilvTOU1VzxF9x"

    @BeforeTest
    fun Setup() {
        mockkObject(TeamAccessor)
    }

    @Test
    @Ignore("Non-mock test")
    fun `test getTeamById`() = runBlocking {
        val team: Team? = TeamAccessor.getTeamByInviteCode(testInvite)
        println(team?.name)
        if (team != null) {
            assertEquals("Barcelona", team.name)
        } else {
            println("HIIIII")
        }
    }

    @Test
    fun newTest() = runTest {
        val mockTeam = mockk<Team>()
        every { mockTeam.name } returns "Barcelona"
        coEvery { TeamAccessor.getTeamByInviteCode(testInvite) } returns mockTeam

        val team = TeamAccessor.getTeamByInviteCode(testInvite)
        assertEquals("Barcelona", team?.name)
    }

    @AfterTest
    fun completion() {
        unmockkAll()
    }
}