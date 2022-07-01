package com.makspasich.library.models

data class User(
    var uid: String = "",
    var username: String = "",
    var email: String = "",
    var granted: Boolean = false
) {
}