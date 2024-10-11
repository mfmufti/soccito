package com.team9.soccermanager

import android.content.Intent
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.*
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.*
import com.team9.soccermanager.ui.theme.SoccerManagerTheme
import androidx.compose.runtime.*
import com.google.firebase.auth.auth
import com.google.firebase.Firebase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SoccerManagerTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainScreen(baseContext)
                }
            }
        }
    }
}


private fun getUserProfile() {
    // [START get_user_profile]
    val user = Firebase.auth.currentUser
    user?.let {
        // Name, email address, and profile photo Url
        val name = it.displayName
        val email = it.email
        val photoUrl = it.photoUrl

        // Check if user's email is verified
        val emailVerified = it.isEmailVerified

        // The user's ID, unique to the Firebase project. Do NOT use this value to
        // authenticate with your backend server, if you have one. Use
        // FirebaseUser.getIdToken() instead.
        val uid = it.uid
    }
    // [END get_user_profile]
}


fun loginHandler(baseContext: Context, pwd: String, username: String) {
    EmailPasswordActivity.get().signIn(baseContext, username, pwd) { success ->
        if (success) {
            // User is logged in, proceed to the main screen
            println("LOGGED IN, WELCOME")
            val name = "John Doe"
            val intent = Intent(baseContext, WelcomeScreen::class.java).apply {
                putExtra("USER_NAME", name)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            baseContext.startActivity(intent)
        } else {
            println("FAILED")
        }
    }
}

fun registerHandler(baseContext: Context, pwd: String, username: String) {
    EmailPasswordActivity.get().createAccount(baseContext, username, pwd) { success ->
        if (success) {
            // User is logged in, proceed to the main screen
            println("LOGGED IN, WELCOME")
            val name = "John Doe"
            val intent = Intent(baseContext, WelcomeScreen::class.java).apply {
                putExtra("USER_NAME", name)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            baseContext.startActivity(intent)
        } else {
            println("FAILED")
        }
    }
}

@Composable
fun MainScreen(baseContext: Context) {
    var isLoginScreen by remember { mutableStateOf(true) }

    if (isLoginScreen) {
        LoginScreen(baseContext = baseContext, switchToRegister = { isLoginScreen = false })
    } else {
        RegistrationScreen(baseContext = baseContext, switchToLogin = { isLoginScreen = true })
    }
}

@Composable
fun LoginScreen(switchToRegister: () -> Unit, baseContext: Context) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("")}

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Login", fontSize = 30.sp)

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = password,
            onValueChange = { password = it},
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { loginHandler(baseContext, password, email) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Create an account",
            modifier = Modifier
                .clickable { switchToRegister() }
                .padding(8.dp),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun RegistrationScreen(switchToLogin: () -> Unit, baseContext: Context) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Register", fontSize = 30.sp)

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { registerHandler(baseContext, username = email, pwd = password) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Already have an account? Login",
            modifier = Modifier
                .clickable { switchToLogin() }
                .padding(8.dp),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}