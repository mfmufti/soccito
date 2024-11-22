package com.team9.soccermanager.screens.register

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.team9.soccermanager.model.Account
import com.team9.soccermanager.model.RegisterError
import com.team9.soccermanager.model.accessor.LeagueAccessor
import com.team9.soccermanager.model.accessor.TeamAccessor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterViewModel(val type: String, val other: Map<String, String>): ViewModel() {
    private var fullnameState = mutableStateOf(""); private var fullname by fullnameState
    private var emailState = mutableStateOf(""); private var email by emailState
    private var passwordState = mutableStateOf(""); private var password by passwordState
    private var errorState = mutableStateOf(""); private var error by errorState

//    fun getFullname() = fullname
    fun getFullname() = fullnameState
    fun getEmail() = emailState
    fun getPassword() = passwordState
    fun getError() = errorState

    fun handleRegister(success: () -> Unit) {
        if (fullname.isEmpty() || email.isEmpty() || password.isEmpty()) {
            error = "Please fill all fields"
            return
        }
        Account.createAccount(type, fullname, email, password) { status ->
            if (status == RegisterError.NONE) {
                when (type) {
                    "admin" -> createLeague(other["leagueName"]!!, success)
                    "coach" -> createTeam(other["leagueCode"]!!, other["teamName"]!!, success)
                    else -> joinTeam(other["teamCode"]!!, success)
                }
            } else {
                error = when (status) {
                    RegisterError.BAD_EMAIL -> "Bad email provided"
                    RegisterError.USER_EXISTS -> "A user with this email already exists"
                    RegisterError.WEAK_PASSWORD -> "The password provided is weak"
                    else -> "Unknown error occurred"
                }
            }
        }
    }

    fun joinTeam(teamCode: String, onSuccess: () -> Unit) {
//        if (teamCode.isEmpty()) {
//            error = "Please leave no fields blank"
//            return
//        }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // TODO: restructure error handling, should not deal with them both here and in accessors
                val team = TeamAccessor.getTeamByInviteCode(teamCode)
                    ?: throw Exception("Team not found")
                team.playerIds.add(Firebase.auth.uid ?: throw Exception("Player not logged in"))
                TeamAccessor.updateTeam(team)
                Account.joinTeam(team.id)
                Account.joinLeague(team.leagueId)
                withContext(Dispatchers.Main) {
                    onSuccess()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                error = "There was an error joining that team"
            }
        }
    }

    fun createLeague(leagueName: String, onSuccess: () -> Unit) {
//        if (leagueName.isEmpty()) {
//            onError("Please leave no fields blank.")
//            return
//        }
        viewModelScope.launch(Dispatchers.IO) {
            val res = LeagueAccessor.createLeague(leagueName)
            withContext(Dispatchers.Main) {
                if (res != null) {
                    Account.joinLeague(res.id)
                    onSuccess()
                } else {
                    error = "There was an error creating the league"
                }
            }
        }
    }

    fun createTeam(leagueCode: String, teamName: String, onSuccess: () -> Unit) {
//        if (leagueCode.isEmpty() || teamName.isEmpty()) {
//            onError("Please leave no fields blank")
//            return
//        }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // TODO: restructure error handling, should not deal with them both here and in accessors
                val league = LeagueAccessor.getLeagueByInviteCode(leagueCode)
                    ?: throw Exception("League not found")
                val team = TeamAccessor.createTeam(teamName, league.id) ?: throw Exception("Failed to create team")
                league.teamIds.add(team.id)
                LeagueAccessor.updateLeague(league)
                Account.joinTeam(team.id)
                Account.joinLeague(leagueCode)
                withContext(Dispatchers.Main) {
                    onSuccess()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                error = "There was an error creating the team"
            }
        }
    }
}