package com.makspasich.library.models

data class User(
    val uid: String = "",
    val username: String = "",
    val email: String = "",
    val granted: Boolean = false
)