package com.makspasich.library.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthCredential
import com.makspasich.library.NavigationRoute
import com.makspasich.library.model.service.AccountService
import com.makspasich.library.model.service.StorageService
import com.makspasich.library.model.service.auth.SignInResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val accountService = AccountService()
    private val storageService = StorageService()
    private val _loginState = MutableStateFlow(SignInResult())
    val loginState: StateFlow<SignInResult>
        get() = _loginState
    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean>
        get() = _loading

    val firebaseUser = accountService.currentUser
    fun onAppStart(openAndPopUp: (String, String) -> Unit) {
        _loading.update { true }
        if (!accountService.hasUser) {
            _loading.update { false }
            _loginState.value = _loginState.value.copy(hasUser = false, isAccessGranted = false)
        } else {
            viewModelScope.launch {
                storageService.getUser(accountService.currentUserId)
                    ?.let { user ->
                        _loginState.value =
                            _loginState.value.copy(hasUser = true, isAccessGranted = user.granted)
                        _loading.update { false }
                        if (user.granted) {
                            openAndPopUp(
                                NavigationRoute.Products.route,
                                NavigationRoute.LoginScreen.route
                            )
                        }
                    } ?: run {
                    _loading.update { false }
                    _loginState.update { it.copy(hasUser = true, isAccessGranted = false) }
                }
            }
        }
    }

    fun onSignInResult(authCredential: AuthCredential, openAndPopUp: (String, String) -> Unit) {
        viewModelScope.launch {
            val authenticate = accountService.authenticate(authCredential)
            _loginState.update { authenticate }
            if (_loginState.value.isAccessGranted) {
                openAndPopUp(
                    NavigationRoute.Products.route,
                    NavigationRoute.LoginScreen.route
                )
            }
        }
    }
}
