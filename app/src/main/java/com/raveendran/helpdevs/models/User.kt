package com.raveendran.helpdevs.models

data class User(
    val name: String = "",
    val email: String = "",
    val uid: String = "",
    val photoUrl: String = "",
    val status: Boolean = false
)
