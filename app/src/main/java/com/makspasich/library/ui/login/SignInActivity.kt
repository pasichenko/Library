package com.makspasich.library.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.persistentCacheSettings
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.makspasich.library.BaseActivity
import com.makspasich.library.MainActivity
import com.makspasich.library.R
import com.makspasich.library.databinding.SigninActivityBinding
import com.makspasich.library.models.User

class SignInActivity : BaseActivity() {
    private lateinit var binding: SigninActivityBinding
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    //    private val mRootReference: DatabaseReference = FirebaseDatabase.getInstance().reference
    private var mGoogleSignInClient: GoogleSignInClient? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SigninActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setProgressBar(R.id.progressBar)
        binding.signInGoogleAccount.setOnClickListener { signIn() }

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = mAuth.currentUser
        currentUser?.let { onAuthSuccess(it) }
    }

    private val activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount?) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct!!.id)
        showProgressBar()
        binding.signInGoogleAccount.isEnabled = false
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task: Task<AuthResult?> ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    val user = mAuth.currentUser
                    onAuthSuccess(user)
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Snackbar.make(
                        findViewById(R.id.main_layout),
                        "Authentication Failed.",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    FirebaseAuth.getInstance().signOut()
                }
                hideProgressBar()
                binding.signInGoogleAccount.isEnabled = true
            }
    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient!!.signInIntent
        activityResultLauncher.launch(signInIntent)
    }

    private fun onAuthSuccess(user: FirebaseUser?) {
        showProgressBar()
        binding.signInGoogleAccount.isEnabled = false

        user?.let {
            Firebase.firestore.collection("users")
                .document(user.uid)
                .get()
                .addOnSuccessListener { document ->
                    val username = usernameFromEmail(user.email)
                    var userDB = User(user.uid, username, user.email!!, false)
                    if (document.exists()) {
                        userDB = document.toObject<User>() ?: userDB
                    } else {
                        Firebase.firestore.collection("users").document(user.uid).set(userDB)
                    }

                    if (userDB.granted) {
                        val intent = Intent(this@SignInActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@SignInActivity, "AccessDenied", Toast.LENGTH_SHORT)
                            .show()
                    }
                    hideProgressBar()
                    binding.signInGoogleAccount.isEnabled = true
                }
        }
    }

    private fun usernameFromEmail(email: String?): String {
        return if (email!!.contains("@")) {
            email.split("@".toRegex()).toTypedArray()[0]
        } else {
            email
        }
    }

    companion object {
        private const val TAG = "GoogleActivity"

        init {
            Firebase.database.setPersistenceEnabled(true)
            Firebase.database.reference.keepSynced(true)
            Firebase.firestore.firestoreSettings = firestoreSettings {
                setLocalCacheSettings(persistentCacheSettings { })
            }
        }
    }

}