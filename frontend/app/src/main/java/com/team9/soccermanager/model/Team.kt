package com.team9.soccermanager.model

data class Team (
    val id: String,
    val name: String,
    val code: String,
    val playerIds: MutableList<String>,
    val coachIds: MutableList<String>
) {
    // need to provide no-arg constructor to support deserialization with Firebase
    constructor() : this("", "", "", mutableListOf(), mutableListOf())
}