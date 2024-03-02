package com.makspasich.library.model.service.auth

data class SignInResult(
    val hasUser: Boolean = false,
    val isAccessGranted: Boolean = false
)