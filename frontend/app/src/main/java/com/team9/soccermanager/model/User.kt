package com.team9.soccermanager.model

data class User(
    val id: String,
    val name: String,
    val email: String
) {
    // need to provide no-arg constructor to support deserialization with Firebase
    constructor() : this("", "", "")
}