package com.makspasich.library.model.service

import com.google.firebase.Firebase
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.makspasich.library.model.service.auth.SignInResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.cancellation.CancellationException

class AccountService(
    private val auth: FirebaseAuth = Firebase.auth
)  {
    private val storageService = StorageService()
    val currentUserId: String
        get() = auth.currentUser?.uid.orEmpty()

    val hasUser: Boolean
        get() = auth.currentUser != null
    val currentUser: Flow<FirebaseUser?>
        get() = callbackFlow {
            val listener =
                FirebaseAuth.AuthStateListener { auth ->
                    this.trySend(auth.currentUser)
                }
            auth.addAuthStateListener(listener)
            awaitClose { auth.removeAuthStateListener(listener) }
        }

    suspend fun authenticate(googleCredentials: AuthCredential): SignInResult {
        return try {
            return auth.signInWithCredential(googleCredentials)
                .await()
                .user?.let { firebaseUser ->
                    storageService.getUser(firebaseUser.uid)
                        ?.let { SignInResult(hasUser = true, isAccessGranted = it.granted) }
                        ?: SignInResult(hasUser = true)
                } ?: SignInResult()
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            SignInResult()
        }
    }
}
