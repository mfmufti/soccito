package com.team9.soccermanager.model

enum class RegisterError {
    NONE, WEAK_PASSWORD, BAD_EMAIL, USER_EXISTS, UNKNOWN
}

enum class LoginError {
    NONE, NOT_EXIST, BAD_CREDENTIALS, UNKNOWN, BROKEN_ACCOUNT
}

enum class LeagueError {
    NONE, EXISTS, NETWORK, UNKNOWN
}

enum class TeamError {
    NONE, EXISTS, NETWORK, BAD_JOIN_CODE, UNKNOWN
}

enum class TeamCodeError {
    NONE, NOT_EXIST, NETWORK, UNKNOWN
}

enum class MainScreens {
    HOME, SCHEDULE, ROSTER, CHAT, BACK, NONE
}