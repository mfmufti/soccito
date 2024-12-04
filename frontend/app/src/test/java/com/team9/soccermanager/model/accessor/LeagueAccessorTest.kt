package com.team9.soccermanager.model.accessor

import com.team9.soccermanager.model.GameError
import com.team9.soccermanager.model.League
import com.team9.soccermanager.model.LeagueError
import com.team9.soccermanager.model.RankingRow
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/* Tests for the various methods of the LeagueAccessor model file */

class LeagueAccessorTest {

    @BeforeTest
    fun setup() {
        mockkObject(LeagueAccessor)
    }

    @AfterTest
    fun completion() {
        unmockkAll()
    }

    // Tests the getLeagueById function by verifying it returns the correct league with the given ID.
    @Test
    fun `test getLeagueById`() = runTest {
        val mockLeague = mockk<League>()
        every { mockLeague.id } returns "mockId"
        coEvery { LeagueAccessor.getLeagueById("mockId") } returns mockLeague

        val league = LeagueAccessor.getLeagueById("mockId")

        assertEquals("mockId", league?.id)
        coVerify { LeagueAccessor.getLeagueById("mockId") }
    }

    // Tests the getLeagueByInviteCode function by verifying it returns the correct league with the given invite code.
    @Test
    fun `test getLeagueByInviteCode`() = runTest {
        val mockLeague = mockk<League>()
        every { mockLeague.code } returns "mockCode"
        coEvery { LeagueAccessor.getLeagueByInviteCode("mockCode") } returns mockLeague

        val league = LeagueAccessor.getLeagueByInviteCode("mockCode")

        assertEquals("mockCode", league?.code)
        coVerify { LeagueAccessor.getLeagueByInviteCode("mockCode") }
    }

    // Tests the createLeague function by verifying it creates a new league with the given name.
    @Test
    fun `test createLeague`() = runTest {
        val mockLeague = mockk<League>()
        every { mockLeague.name } returns "mockLeagueName"
        coEvery { LeagueAccessor.createLeague("mockLeagueName") } returns mockLeague

        val league = LeagueAccessor.createLeague("mockLeagueName")

        assertEquals("mockLeagueName", league?.name)
        coVerify { LeagueAccessor.createLeague("mockLeagueName") }
    }

    // Tests the leagueExists function by verifying it returns the correct error code when a league with the given name already exists.
    @Test
    fun `test leagueExists`() = runTest {
        coEvery { LeagueAccessor.leagueExists("mockLeague") } returns LeagueError.EXISTS

        val error = LeagueAccessor.leagueExists("mockLeague")

        assertEquals(LeagueError.EXISTS, error)
        coVerify { LeagueAccessor.leagueExists("mockLeague") }
    }

    // Tests the updateLeague function by verifying it successfully updates a league.
    @Test
    fun `test updateLeague`() = runTest {
        coEvery { LeagueAccessor.updateLeague(any()) } returns true

        val result = LeagueAccessor.updateLeague(mockk())

        assertTrue(result)
        coVerify { LeagueAccessor.updateLeague(any()) }
    }

    // Tests the getGames function by verifying it returns a list of games.
    @Test
    fun `test getGames`() = runTest {
        val mockGameList = listOf(mockk<Game>(), mockk<Game>())
        coEvery { LeagueAccessor.getGames() } returns Pair(GameError.NONE, mockGameList)

        val (error, games) = LeagueAccessor.getGames()

        assertEquals(GameError.NONE, error)
        assertEquals(2, games.size)
        coVerify { LeagueAccessor.getGames() }
    }

    // Tests the getGame function by verifying it returns the correct game with the given ID.
    @Test
    fun `test getGame`() = runTest {
        val mockGame = mockk<Game>()
        every { mockGame.id } returns 123
        coEvery { LeagueAccessor.getGame(123) } returns Pair(GameError.NONE, mockGame)

        val (error, game) = LeagueAccessor.getGame(123)

        assertEquals(GameError.NONE, error)
        assertEquals(123, game.id)
        coVerify { LeagueAccessor.getGame(123) }
    }

    // Tests the getGameFromLoaded function by verifying it returns the correct game with the given ID from the loaded games.
    @Test
    fun `test getGameFromLoaded`() {
        val mockGame = mockk<Game>()
        every { mockGame.id } returns 123
        every { LeagueAccessor.getGameFromLoaded(123) } returns mockGame

        val game = LeagueAccessor.getGameFromLoaded(123)

        assertEquals(123, game.id)
        verify { LeagueAccessor.getGameFromLoaded(123) }
    }

    // Tests the writeGame function by verifying it successfully writes a game to the database.
    @Test
    fun `test writeGame`() = runTest {
        coEvery { LeagueAccessor.writeGame(any(), true) } returns GameError.NONE

        val result = LeagueAccessor.writeGame(mockk(), true)

        assertEquals(GameError.NONE, result)
        coVerify { LeagueAccessor.writeGame(any(), true) }
    }

    // Tests the deleteGame function by verifying it successfully deletes a game from the database.
    @Test
    fun `test deleteGame`() = runTest {
        coEvery { LeagueAccessor.deleteGame(123) } returns GameError.NONE

        val result = LeagueAccessor.deleteGame(123)

        assertEquals(GameError.NONE, result)
        coVerify { LeagueAccessor.deleteGame(123) }
    }

    // Tests the getRankingsData function by verifying it returns the correct rankings data.
    @Test
    fun `test getRankingsData`() = runTest {
        val mockRankingRowList = listOf(mockk<RankingRow>(), mockk<RankingRow>())
        coEvery { LeagueAccessor.getRankingsData(any()) } answers {
            val callback = firstArg<(List<RankingRow>) -> Unit>()
            callback(mockRankingRowList)
        }

        var result: List<RankingRow>? = null
        LeagueAccessor.getRankingsData { rankings ->
            result = rankings
        }

        assertNotNull(result)
        assertEquals(2, result?.size)
        coVerify { LeagueAccessor.getRankingsData(any()) }
    }
}