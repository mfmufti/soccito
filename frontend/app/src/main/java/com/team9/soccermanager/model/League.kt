package com.team9.soccermanager.model

data class League(
    val id: String,
    val name: String,
    val code: String,
    val teamIds: MutableList<String>,
    val adminIds: MutableList<String>
) {
    // need to provide no-arg constructor to support deserialization with Firebase
    constructor(): this("", "", "", mutableListOf(), mutableListOf())
}