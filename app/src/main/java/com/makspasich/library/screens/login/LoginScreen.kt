package com.makspasich.library.screens.login

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseUser
import com.makspasich.library.common.button.GoogleLoginButton
import com.makspasich.library.common.button.SignOutButton
import com.makspasich.library.model.service.auth.GoogleAuthUiClient
import com.makspasich.library.model.service.auth.SignInResult
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    googleAuthUiClient: GoogleAuthUiClient,
    openAndPopUp: (String, String) -> Unit,
    signOutClick: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val loginState by viewModel.loginState.collectAsStateWithLifecycle()
    val firebaseUser by viewModel.firebaseUser.collectAsStateWithLifecycle(null)
    val loading by viewModel.loading.collectAsStateWithLifecycle()
    val lifecycleScope = rememberCoroutineScope()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val authCredential = googleAuthUiClient.getAuthCredential(
                intent = result.data ?: return@rememberLauncherForActivityResult
            )
            viewModel.onSignInResult(authCredential, openAndPopUp)
        }
    }

    LoginScreenContent(
        isLoading = loading,
        loginState = loginState,
        firebaseUser = firebaseUser,
        onAppStart = { viewModel.onAppStart(openAndPopUp) },
        onSignInClick = {
            lifecycleScope.launch {
                val signInIntentSender = googleAuthUiClient.signIn()
                launcher.launch(
                    IntentSenderRequest.Builder(
                        signInIntentSender ?: return@launch
                    ).build()
                )
            }
        },
        signOutClick = signOutClick
    )
}

@Composable
fun LoginScreenContent(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    loginState: SignInResult,
    firebaseUser: FirebaseUser?,
    onAppStart: () -> Unit,
    onSignInClick: () -> Unit,
    signOutClick: () -> Unit
) {
    val commonModifier = modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .background(color = MaterialTheme.colorScheme.background)
    if (isLoading) {
        LoadingContent(modifier = commonModifier)
    } else {
        if (loginState.hasUser) {
            if (loginState.isAccessGranted) {
                LoadingContent(modifier = commonModifier)
            } else {
                UserRequiredAccessGrantContent(
                    modifier = commonModifier,
                    firebaseUser = firebaseUser,
                    signOutClick = signOutClick
                )
            }
        } else {
            LoginContent(modifier = commonModifier, onSignInClick = onSignInClick)
        }
    }

    LaunchedEffect(true) {
        onAppStart()
    }
}

@Composable
fun LoadingContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.onBackground)
    }
}

@Composable
fun UserRequiredAccessGrantContent(
    modifier: Modifier = Modifier,
    firebaseUser: FirebaseUser?,
    signOutClick: () -> Unit
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    modifier = Modifier
                        .padding(PaddingValues(bottom = 16.dp)),
                    imageVector = Icons.Filled.Warning,
                    contentDescription = null
                )

                Text(
                    modifier = Modifier
                        .padding(PaddingValues(bottom = 16.dp)),
                    text = "Access grant required",
                    style = MaterialTheme.typography.headlineSmall
                )
                Column(
                    modifier = Modifier
                        .weight(weight = 1f, fill = false)
                        .padding(PaddingValues(bottom = 24.dp))
                        .align(Alignment.CenterHorizontally),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    firebaseUser?.let {
                        it.photoUrl?.let {
                            AsyncImage(
                                model = it,
                                contentDescription = "Proto profile",
                                modifier = Modifier
                                    .padding(PaddingValues(bottom = 16.dp))
                                    .size(90.dp)
                                    .aspectRatio(1f)
                                    .clip(CircleShape)

                            )
                        }
                        it.displayName?.let {
                            Text(text = it)
                        }
                        it.email?.let {
                            Text(text = it)
                        }
                    } ?: run {
                        Text(text = "Fail")
                    }

                }
            }
        }
        ButtonContainer {
            SignOutButton(signOutClick)
        }
    }
}

@Composable
fun LoginContent(
    modifier: Modifier = Modifier,
    onSignInClick: () -> Unit
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomCenter
    ) {
        ButtonContainer {
            GoogleLoginButton(onSignInClick)
        }
    }
}

@Composable
fun ButtonContainer(
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(2f),
        contentAlignment = Alignment.Center,
        content = content
    )
}
