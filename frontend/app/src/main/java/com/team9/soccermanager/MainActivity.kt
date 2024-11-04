package com.team9.soccermanager

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.team9.soccermanager.model.Account
import com.team9.soccermanager.ui.theme.SoccerManagerTheme
import com.team9.soccermanager.screens.login.LoginView
import com.team9.soccermanager.screens.register.RegisterView
import com.team9.soccermanager.screens.home.HomeView
import com.team9.soccermanager.screens.newadmin.NewAdminView
import com.team9.soccermanager.screens.newcoach.NewCoachView
import com.team9.soccermanager.screens.newplayer.NewPlayerView
import com.team9.soccermanager.screens.playerChatScreen.PlayerChatView
import com.team9.soccermanager.screens.playerGameScheduleScreen.PlayerGameScheduleView
import com.team9.soccermanager.screens.typeselect.TypeSelectView
import com.team9.soccermanager.screens.welcome.WelcomeView
import com.team9.soccermanager.screens.playerHomeScreen.PlayerHomeScreenView
import com.team9.soccermanager.screens.playerRosterScreen.PlayerRosterView
import com.team9.soccermanager.screens.rankingsScreen.RankingView
import kotlinx.serialization.Serializable

@Serializable object WelcomeScreen
@Serializable object LoginScreen
@Serializable object RegisterScreen
@Serializable object TypeSelectScreen
@Serializable object NewAdminScreen
@Serializable object NewCoachScreen
@Serializable object NewPlayerScreen
@Serializable object HomeScreen
@Serializable object PlayerHomeScreen
@Serializable object LeagueStandingsScreen
@Serializable object PlayerGameScheduleScreen
@Serializable object PlayerRosterScreen
@Serializable object PlayerChatScreen

class MainActivity : ComponentActivity() {
    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        enableEdgeToEdge()
        setContent {
            SoccerManagerTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    App()
                }
            }
        }
    }
}

class Navigator(val navController: NavHostController) {
    fun switch(dest: Any) {
        navController.navigate(route = dest)
    }
    fun popSwitch(dest: Any, top: Any) {
        navController.navigate(dest) {
            popUpTo(top) { inclusive = false }
        }
    }
    fun pop() {
        navController.popBackStack()
    }
    fun clearSwitch(dest: Any) {
        navController.navigate(dest) {
            popUpTo(0) { inclusive = true }
        }
    }
}

@Composable
fun App(navController: NavHostController = rememberNavController()) {
    val nav = remember(navController) { Navigator(navController) }
    var start: Any = if (Account.isLoggedIn()) HomeScreen else WelcomeScreen
    //start = LeagueStandingsScreen // For debug purposes
    // First check if authenticated user is player, coach, admin, and guide them
    // to the appropriate screen. If its player, go to playerHomeScreen upon login and register

    NavHost(navController = navController, startDestination = start) {
        composable<WelcomeScreen> {
            WelcomeView(
                switchToLogin = { nav.switch(LoginScreen) },
                switchToRegister = { nav.switch(RegisterScreen) }
            )
        }
        composable<LoginScreen> {
            LoginView(
                switchBack = { nav.pop() },
                switchToRegister = { nav.popSwitch(RegisterScreen, WelcomeScreen) },
                switchToHome = { nav.clearSwitch(HomeScreen) }
                // For testing player screen, uncomment below line (and comment above line) and login as player:
                // email: pt1@test.com, pwd: abc123
                // switchToHome = { nav.clearSwitch(PlayerHomeScreen) }
            )
        }
        composable<RegisterScreen> {
            RegisterView(
                switchBack = { nav.pop() },
                switchToLogin = { nav.popSwitch(LoginScreen, WelcomeScreen) },
                switchToTypeSelect = { nav.clearSwitch(TypeSelectScreen) }
            )
        }
        composable<TypeSelectScreen> {
            TypeSelectView(
                switchBack = { nav.pop() },
                switchToPlayer = { nav.switch(NewPlayerScreen) },
                switchToCoach = { nav.switch(NewCoachScreen) },
                switchToAdmin = { nav.switch(NewAdminScreen) }
            )
        }
        composable<NewAdminScreen> {
            NewAdminView(
                switchToHome = { nav.clearSwitch(HomeScreen) },
                switchBack = { nav.pop() }
            )
        }
        composable<NewCoachScreen> {
            NewCoachView(
                switchToHome = { nav.clearSwitch(HomeScreen) },
                switchBack = { nav.pop() }
            )
        }
        composable<NewPlayerScreen> {
            NewPlayerView(
                switchToHome = { nav.clearSwitch(HomeScreen) },
                switchBack = { nav.pop() }
            )
        }
        composable<HomeScreen> {
            HomeView(
                switchToWelcome = { nav.clearSwitch(WelcomeScreen) }
            )
        }
        composable<PlayerHomeScreen> {
            PlayerHomeScreenView(
                switchToWelcome = { nav.clearSwitch(WelcomeScreen) },
                goToLeagueStandings = { nav.switch(LeagueStandingsScreen) },
                goToSchedule = { nav.clearSwitch(PlayerGameScheduleScreen) },
                goToRoster = { nav.clearSwitch(PlayerRosterScreen) },
                goToChat = { nav.clearSwitch(PlayerChatScreen) }
            )
        }
        composable<PlayerGameScheduleScreen> {
            PlayerGameScheduleView(
                switchToWelcome = { nav.clearSwitch(WelcomeScreen) },
                goToHome = { nav.clearSwitch(PlayerHomeScreen) },
                goToRoster = { nav.clearSwitch(PlayerRosterScreen) },
                goToChat = { nav.clearSwitch(PlayerChatScreen) }
            )
        }
        composable<PlayerRosterScreen> {
            PlayerRosterView(
                switchToWelcome = { nav.clearSwitch(WelcomeScreen) },
                goToHome = { nav.clearSwitch(PlayerHomeScreen) },
                goToSchedule = { nav.clearSwitch(PlayerGameScheduleScreen) },
                goToChat = { nav.clearSwitch(PlayerChatScreen) }
            )
        }
        composable<PlayerChatScreen> {
            PlayerChatView(
                switchToWelcome = { nav.clearSwitch(WelcomeScreen) },
                goToHome = { nav.clearSwitch(PlayerHomeScreen) },
                goToSchedule = { nav.clearSwitch(PlayerGameScheduleScreen) },
                goToRoster = { nav.clearSwitch(PlayerRosterScreen) },
            )
        }
        composable<LeagueStandingsScreen> {
            RankingView(
                switchBack = { nav.pop() }
            )
        }

    }
}