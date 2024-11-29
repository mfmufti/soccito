package com.team9.soccermanager.model

enum class RegisterError {
    NONE, WEAK_PASSWORD, BAD_EMAIL, USER_EXISTS, UNKNOWN, NETWORK
}

enum class LoginError {
    NONE, NOT_EXIST, BAD_CREDENTIALS, UNKNOWN, BROKEN_ACCOUNT, NETWORK
}

enum class LeagueError {
    NONE, EXISTS, NETWORK, UNKNOWN
}

enum class PwdError {
    WEAK, INCORRECT, NETWORK, UNKNOWN, NONE, NO_EMAIL
}

enum class NameError {
    UNKNOWN, NONE
}

enum class TeamError {
    NONE, EXISTS, NETWORK, BAD_JOIN_CODE, UNKNOWN
}

enum class TeamCodeError {
    NONE, NOT_EXIST, NETWORK, UNKNOWN
}

enum class GameError {
    NONE, NETWORK, UNKNOWN
}

enum class MainScreens {
    HOME, SCHEDULE, ROSTER, CHAT, BACK, NONE
}

enum class MenuScreens {
    PROFILE, ACCOUNT
}

enum class GameStatus {
    ONGOING, COMPLETED, SCHEDULED
}

enum class Availability {
    AVAILABLE, UNAVAILABLE
}