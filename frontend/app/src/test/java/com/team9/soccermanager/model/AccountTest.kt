package com.team9.soccermanager.model

import com.google.firebase.auth.FirebaseAuth
import io.mockk.*
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

class AccountTest {
    @BeforeTest
    fun setup() {
        val mockAuth = mockk<FirebaseAuth>()
        mockkStatic(FirebaseAuth::class)
        every { FirebaseAuth.getInstance() } returns mockAuth
    }

    @Test
    fun `test isLoggedIn`() {
        // Mock FirebaseAuth
        mockkObject(Account)
        every { Account.isLoggedIn() } returns true

        val result = Account.isLoggedIn()

        assertTrue(result)
        verify { Account.isLoggedIn() }
        unmockkObject(Account)
    }

    @Test
    fun `test getUserName`() {
        mockkObject(Account)
        val callback = mockk<(String) -> Unit>(relaxed = true)
        every { Account.getUserName(callback) } answers {
            callback.invoke("mockUsername")
        }

        Account.getUserName(callback)

        verify { callback("mockUsername") }
        unmockkObject(Account)
    }

    @Test
    fun `test createAccount`() {
        mockkObject(Account)
        val callback = mockk<(RegisterError) -> Unit>(relaxed = true)
        every { Account.createAccount(any(), any(), any(), any(), callback) } answers {
            callback.invoke(RegisterError.NONE)
        }

        Account.createAccount("player", "Mock Name", "mock@example.com", "password123", callback)

        verify { callback(RegisterError.NONE) }
        unmockkObject(Account)
    }

    @Test
    fun `test joinTeam`() {
        mockkObject(Account)
        justRun { Account.joinTeam("mockTeamId") }

        Account.joinTeam("mockTeamId")

        verify { Account.joinTeam("mockTeamId") }
        unmockkObject(Account)
    }

    @Test
    fun `test joinLeague`() {
        mockkObject(Account)
        justRun { Account.joinLeague("mockLeagueId") }

        Account.joinLeague("mockLeagueId")

        verify { Account.joinLeague("mockLeagueId") }
        unmockkObject(Account)
    }

    @Test
    fun `test setupGS`() {
        mockkObject(Account)
        val callback = mockk<() -> Unit>(relaxed = true)
        justRun { Account.setupGS(callback) }

        Account.setupGS(callback)

        unmockkObject(Account)
    }

    @Test
    fun `test signIn`() {
        mockkObject(Account)
        val callback = mockk<(LoginError) -> Unit>(relaxed = true)
        every { Account.signIn(any(), any(), callback) } answers {
            callback.invoke(LoginError.NONE)
        }

        Account.signIn("mock@example.com", "password123", callback)

        verify { callback(LoginError.NONE) }
        unmockkObject(Account)
    }

    @Test
    fun `test signOut`() {
        mockkObject(Account)
        justRun { Account.signOut() }

        Account.signOut()

        verify { Account.signOut() }
        unmockkObject(Account)
    }

    @Test
    fun `test setupNotifications`() {
        mockkObject(Account)
        justRun { Account.setupNotifications() }

        Account.setupNotifications()

        verify { Account.setupNotifications() }
        unmockkObject(Account)
    }

    @AfterTest
    fun completion() {
        unmockkAll()
    }
}
