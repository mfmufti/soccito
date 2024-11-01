package com.team9.soccermanager

import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import com.team9.soccermanager.model.accessor.TeamAccessor
import com.team9.soccermanager.model.Team

class TestTeam {
    @Test
    fun `test getTeamById`() = runBlocking {
        val team: Team? = TeamAccessor.getTeamByInviteCode("kzOcrvvilvTOU1VzxF9x")
        println(team?.name)
        if (team != null) {
            assertEquals("Barcelona", team.name)
        } else {
            println("HIIIII")
        }

    }

}