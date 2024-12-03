package com.team9.soccermanager

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.team9.soccermanager.model.Account
import com.team9.soccermanager.model.GS
import com.team9.soccermanager.model.MainScreens
import com.team9.soccermanager.model.MenuScreens
import com.team9.soccermanager.screens.account.AccountView
import com.team9.soccermanager.screens.coachforms.CoachFormsView
import com.team9.soccermanager.screens.adminhome.AdminHomeView
import com.team9.soccermanager.screens.announcements.CoachAnnouncementsView
import com.team9.soccermanager.screens.announcements.PlayerAnnouncementsView
import com.team9.soccermanager.screens.changename.ChangeNameView
import com.team9.soccermanager.screens.changepassword.ChangePasswordView
import com.team9.soccermanager.screens.chatselect.ChatSelectView
import com.team9.soccermanager.screens.coachhome.CoachHomeView
import com.team9.soccermanager.screens.formspecific.FormSpecificView
import com.team9.soccermanager.ui.theme.SoccerManagerTheme
import com.team9.soccermanager.screens.login.LoginView
import com.team9.soccermanager.screens.register.RegisterView
import com.team9.soccermanager.screens.newadmin.NewAdminView
import com.team9.soccermanager.screens.newcoach.NewCoachView
import com.team9.soccermanager.screens.newplayer.NewPlayerView
import com.team9.soccermanager.screens.chat.ChatView
import com.team9.soccermanager.screens.chat.ChatViewModel
import com.team9.soccermanager.screens.formspecific.FormSpecificViewModel
import com.team9.soccermanager.screens.coachroster.CoachRosterView
import com.team9.soccermanager.screens.gameedit.GameEditView
import com.team9.soccermanager.screens.gameedit.GameEditViewModel
import com.team9.soccermanager.screens.gameschedule.GameScheduleView
import com.team9.soccermanager.screens.typeselect.TypeSelectView
import com.team9.soccermanager.screens.welcome.WelcomeView
import com.team9.soccermanager.screens.playerhome.PlayerHomeView
import com.team9.soccermanager.screens.playerroster.PlayerRosterView
import com.team9.soccermanager.screens.rankings.RankingsView
import com.team9.soccermanager.screens.loadscreen.LoadView
import com.team9.soccermanager.screens.playerforms.PlayerFormsView
import com.team9.soccermanager.screens.gamespecific.GameSpecificView
import com.team9.soccermanager.screens.gamespecific.GameSpecificViewModel
import com.team9.soccermanager.screens.profile.ProfileView
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer

@Serializable object LoadScreen
@Serializable object WelcomeScreen
@Serializable object LoginScreen
@Serializable data class RegisterScreen(
    var type: String,
    var leagueName: String = "",
    var leagueCode: String = "",
    var teamName: String = "",
    var teamCode: String = ""
)
@Serializable object TypeSelectScreen
@Serializable object NewAdminScreen
@Serializable object NewCoachScreen
@Serializable object NewPlayerScreen
@Serializable object PlayerHomeScreen
@Serializable object PlayerFormsScreen
@Serializable object PlayerAnnouncementsScreen
@Serializable object CoachHomeScreen
@Serializable object CoachFormsScreen
@Serializable object CoachAnnouncementsScreen
@Serializable data class FormSpecificView(val id: Int, val title: String)
@Serializable object AdminHomeScreen
@Serializable object LeagueStandingsScreen
@Serializable object GameScheduleScreen
@Serializable data class GameSpecificScreen(val id: Int)
@Serializable data class GameEditScreen(val newGame: Boolean, val id: Int)
@Serializable object PlayerRosterScreen
@Serializable data class ChatScreen(var chatID: String, var fullname: String)
@Serializable object ProfileScreen
@Serializable object ChatSelectScreen
@Serializable object CoachRosterScreen
@Serializable object AccountScreen
@Serializable object PasswordChangeScreen
@Serializable object NameChangeScreen

class MainActivity : ComponentActivity() {
    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        enableEdgeToEdge()
        setContent {
            SoccerManagerTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    App(askForNotifications = { askNotificationPermission() })
                }
            }
        }
    }

    // Declare the launcher at the top of your Activity/Fragment:
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
        } else {
            // TODO: Inform user that that your app will not show notifications.
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
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
    fun pop(screen: Any? = null) {
        if (navController.previousBackStackEntry != null) {
            if (screen == null) {
                navController.popBackStack()
            } else {
                navController.popBackStack(screen, false)
            }
        }
    }
    fun clearSwitch(dest: Any) {
        navController.navigate(dest) {
            popUpTo(0) { inclusive = true }
        }
    }
}

@Composable
fun App(navController: NavHostController = rememberNavController(), askForNotifications: () -> Unit) {

    //start = LeagueStandingsScreen // For debug purposes
    // First check if authenticated user is player, coach, admin, and guide them
    // to the appropriate screen. If its player, go to playerHomeScreen upon login and register

    val nav = remember(navController) { Navigator(navController) }
    GS.nav = nav
    var start by remember { mutableStateOf<Any>(LoadScreen) }

    if (Account.isLoggedIn()) {
        Account.setupGS {
            start = when(GS.user?.type) {
                "admin" -> AdminHomeScreen
                "player" -> PlayerHomeScreen
                else -> CoachHomeScreen
            }
        }
    } else {
        start = WelcomeScreen
    }

    val RosterScreen: () -> Any = {
        if (GS.user!!.type == "player") {
            PlayerRosterScreen
        } else {
            CoachRosterScreen
        }
    }

    val HomeScreen: () -> Any = {
        if (GS.user!!.type == "coach") {
            CoachHomeScreen
        } else if (GS.user!!.type == "player") {
            PlayerHomeScreen
        } else {
            AdminHomeScreen
        }
    }

    val switchMainScreen = { newScreen: MainScreens ->
        if (newScreen == MainScreens.BACK) {
            nav.pop()
        } else {
            nav.clearSwitch(when (newScreen) {
                MainScreens.ROSTER -> RosterScreen()
                MainScreens.CHAT -> ChatSelectScreen
                MainScreens.SCHEDULE -> GameScheduleScreen
                else -> HomeScreen()
            })
        }
    }

    val switchMenuScreen = { menuScreen: MenuScreens ->
        nav.switch(when (menuScreen) {
            MenuScreens.PROFILE -> ProfileScreen
            else -> AccountScreen
        })
    }

    NavHost(navController = navController, startDestination = start) {
        composable<LoadScreen> {
            LoadView()
        }
        composable<WelcomeScreen> {
            WelcomeView(
                switchToLogin = { nav.switch(LoginScreen) },
                switchToRegister = { nav.switch(TypeSelectScreen) }
            )
        }
        composable<LoginScreen> {
            LoginView(
                switchBack = { nav.pop() },
                switchToRegister = { nav.popSwitch(TypeSelectScreen, WelcomeScreen) },
                switchToSpecific = { nav.clearSwitch(HomeScreen()) },
                askForNotifications = askForNotifications
            )
        }
        composable<RegisterScreen> { backStackEntry ->
            val data: RegisterScreen = backStackEntry.toRoute()
            RegisterView(
                type = data.type,
                other = mapOf(
                    "leagueName" to data.leagueName,
                    "leagueCode" to data.leagueCode,
                    "teamName" to data.teamName,
                    "teamCode" to data.teamCode,
                ),
                switchBack = { nav.pop() },
                switchToLogin = { nav.popSwitch(LoginScreen, WelcomeScreen) },
                switchToHome = { nav.clearSwitch(HomeScreen()) },
                askForNotifications = askForNotifications
            )
        }
        composable<TypeSelectScreen> {
            TypeSelectView(
                switchBack = { nav.pop() },
                switchToPlayer = { nav.switch(NewPlayerScreen) },
                switchToAdmin = { nav.switch(NewAdminScreen) },
                switchToCoach = { nav.switch(NewCoachScreen) }
            )
        }
        composable<NewAdminScreen> {
            NewAdminView(
                switchToRegister = { leagueName ->
                    nav.switch(RegisterScreen("admin", leagueName = leagueName))
                }, switchBack = { nav.pop() }
            )
        }
        composable<NewCoachScreen> {
            NewCoachView(
                switchToRegister = { leagueCode, teamName ->
                    nav.switch(RegisterScreen("coach", leagueCode = leagueCode, teamName = teamName))
                }, switchBack = { nav.pop() }
            )
        }
        composable<NewPlayerScreen> {
            NewPlayerView(
                switchToRegister = { teamCode ->
                    nav.switch(RegisterScreen("player", teamCode = teamCode))
                }, switchBack = { nav.pop() }
            )
        }
        composable<PlayerHomeScreen> {
            PlayerHomeView(
                viewModel = viewModel(),
                switchToWelcome = { nav.clearSwitch(WelcomeScreen) },
                switchMainScreen = switchMainScreen,
                switchMenuScreen = switchMenuScreen,
                goToLeagueStandings = { nav.switch(LeagueStandingsScreen) },
                goToForms = { nav.switch(PlayerFormsScreen) },
                goToAnnouncements = { nav.switch(PlayerAnnouncementsScreen) },
                goToSpecificGame = { id -> nav.switch(GameSpecificScreen(id)) },
            )
        }
        composable<PlayerFormsScreen> {
            PlayerFormsView(
                viewModel = viewModel(),
                switchToWelcome = { nav.clearSwitch(WelcomeScreen) },
                switchMainScreen = switchMainScreen,
                switchMenuScreen = switchMenuScreen
            )
        }
        composable<PlayerAnnouncementsScreen> {
            PlayerAnnouncementsView(
                title = "Announcements",
                viewModel = viewModel(),
                switchToWelcome = { nav.clearSwitch(WelcomeScreen) },
                switchMainScreen = switchMainScreen,
                switchMenuScreen = switchMenuScreen
            )
        }
        composable<CoachAnnouncementsScreen> {
            CoachAnnouncementsView(
                title = "Announcements",
                viewModel = viewModel(),
                switchToWelcome = { nav.clearSwitch(WelcomeScreen) },
                switchMainScreen = switchMainScreen,
                switchMenuScreen = switchMenuScreen
            )
        }
        composable<CoachHomeScreen> {
            CoachHomeView(
                viewModel = viewModel(),
                switchToWelcome = { nav.clearSwitch(WelcomeScreen) },
                switchMainScreen = switchMainScreen,
                switchMenuScreen = switchMenuScreen,
                goToLeagueStandings = { nav.switch(LeagueStandingsScreen) },
                goToForms = { nav.switch(CoachFormsScreen) },
                goToAnnouncements = { nav.switch(CoachAnnouncementsScreen) },
                goToSpecificGame = { id -> nav.switch(GameSpecificScreen(id)) },
            )
        }
        composable<CoachFormsScreen> {
            CoachFormsView(
                viewModel = viewModel(),
                switchToWelcome = { nav.clearSwitch(WelcomeScreen) },
                switchMainScreen = switchMainScreen,
                switchMenuScreen = switchMenuScreen,
                goToSpecificForm = { id, title -> nav.switch(FormSpecificView(id, title)) }
            )
        }
        composable<FormSpecificView> { backStackEntry ->
            val data: FormSpecificView = backStackEntry.toRoute()
            FormSpecificView(
                id = data.id,
                title = data.title,
                viewModel = viewModel(factory = viewModelFactory { initializer { FormSpecificViewModel(data.id) } }),
                switchToWelcome = { nav.clearSwitch(WelcomeScreen) },
                switchMainScreen = switchMainScreen,
                switchMenuScreen = switchMenuScreen
            )
        }
        composable<AdminHomeScreen> {
            AdminHomeView(
                switchToWelcome = { nav.clearSwitch(WelcomeScreen) },
                switchMainScreen = switchMainScreen,
                switchMenuScreen = switchMenuScreen,
                goToLeagueStandings = { nav.switch(LeagueStandingsScreen) },
                goToSpecificGame = { id -> nav.switch(GameSpecificScreen(id)) },
            )
        }
        composable<GameScheduleScreen> {
            GameScheduleView(
                switchToWelcome = { nav.clearSwitch(WelcomeScreen) },
                switchMainScreen = switchMainScreen,
                switchMenuScreen = switchMenuScreen,
                goToSpecificGame = { id -> nav.switch(GameSpecificScreen(id)) },
                goToGameAdd = { nav.switch(GameEditScreen(true, -1)) },
            )
        }
        composable<GameSpecificScreen> { backStackEntry ->
            val data: GameSpecificScreen = backStackEntry.toRoute()
            GameSpecificView(
                gameId = data.id,
//                viewModel = viewModel(factory = viewModelFactory { initializer { GameSpecificViewModel(data.id) } }),
                switchToWelcome = { nav.clearSwitch(WelcomeScreen) },
                switchMainScreen = switchMainScreen,
                switchMenuScreen = switchMenuScreen,
                goToGameEdit = { nav.switch(GameEditScreen(false, data.id)) },
            )
        }
        composable<GameEditScreen> { backStackEntry ->
            val data: GameEditScreen = backStackEntry.toRoute()
            GameEditView(
                gameId = data.id,
                newGame = data.newGame,
                viewModel = viewModel(factory = viewModelFactory { initializer { GameEditViewModel(data.id, data.newGame) } }),
                switchToWelcome = { nav.clearSwitch(WelcomeScreen) },
                switchMainScreen = switchMainScreen,
                goToGameSchedule = { nav.pop(GameScheduleScreen) },
                switchMenuScreen = switchMenuScreen
            )
        }
        composable<PlayerRosterScreen> {
            PlayerRosterView(
                switchToWelcome = { nav.clearSwitch(WelcomeScreen) },
                switchMainScreen = switchMainScreen,
                switchMenuScreen = switchMenuScreen
            )
        }
        composable<CoachRosterScreen> {
            CoachRosterView(
                switchToWelcome = { nav.clearSwitch(WelcomeScreen) },
                switchMainScreen = switchMainScreen,
                switchMenuScreen = switchMenuScreen
            )
        }
        composable<ChatScreen> { backStackEntry ->
            val data: ChatScreen = backStackEntry.toRoute()
            ChatView(
                viewModel = viewModel(factory = viewModelFactory { initializer { ChatViewModel(data.chatID) } }),
                chatID = data.chatID,
                fullname = data.fullname,
                switchToWelcome = { nav.clearSwitch(WelcomeScreen) },
                switchMainScreen = switchMainScreen,
                switchMenuScreen = switchMenuScreen
            )
        }
        composable<LeagueStandingsScreen> {
            RankingsView(
                switchBack = { nav.pop() }
            )
        }
        composable<ChatSelectScreen> {
            ChatSelectView(
                viewModel = viewModel(),
                switchToWelcome = { nav.clearSwitch(WelcomeScreen) },
                switchMainScreen = switchMainScreen,
                switchMenuScreen = switchMenuScreen,
                goToChat = { chatID, fullname -> nav.clearSwitch(ChatScreen(chatID, fullname))},
            )
        }
        composable<ProfileScreen> {
            ProfileView(
                switchBack = { nav.pop() }
            )
        }
        composable<AccountScreen> {
            AccountView(
                switchBack = { nav.pop() },
                passwordChange = { nav.switch(PasswordChangeScreen) },
                nameChange = { nav.switch(NameChangeScreen) }
            )
        }
        composable<PasswordChangeScreen> {
            ChangePasswordView(
                switchBack = { nav.pop() }
            )
        }
        composable<NameChangeScreen> {
            ChangeNameView(
                switchBack = { nav.pop() }
            )
        }
    }
}