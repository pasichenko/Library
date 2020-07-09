package com.makspasich.library.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
import com.google.firebase.database.*
import com.makspasich.library.BaseActivity
import com.makspasich.library.MainActivity
import com.makspasich.library.R
import com.makspasich.library.databinding.SigninActivityBinding
import com.makspasich.library.models.User

class SignInActivity : BaseActivity() {
    private lateinit var binding: SigninActivityBinding
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val mRootReference: DatabaseReference = FirebaseDatabase.getInstance().reference
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

    // [START on_start_check_user]
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth.currentUser
        currentUser?.let { onAuthSuccess(it) }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
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
                        Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show()
                        FirebaseAuth.getInstance().signOut()
                    }
                    hideProgressBar()
                    binding.signInGoogleAccount.isEnabled = true
                }
    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun onAuthSuccess(user: FirebaseUser?) {
        showProgressBar()
        binding.signInGoogleAccount.isEnabled = false
        mRootReference.child("granted").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var isFound = false
                for (variableSnapshot in dataSnapshot.children) {
                    if (variableSnapshot.key == user!!.uid) {
                        isFound = true
                    }
                }
                if (isFound) {
                    val username = usernameFromEmail(user!!.email)
                    writeNewUser(user.uid, username, user.email)
                    val intent = Intent(this@SignInActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@SignInActivity, "AccessDenied", Toast.LENGTH_SHORT).show()
                }
                hideProgressBar()
                binding.signInGoogleAccount.isEnabled = true
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun writeNewUser(userId: String, name: String?, email: String?) {
        val user = User(userId, name, email)
        mRootReference.child("users").child(userId).setValue(user)
    }

    private fun usernameFromEmail(email: String?): String? {
        return if (email!!.contains("@")) {
            email.split("@".toRegex()).toTypedArray()[0]
        } else {
            email
        }
    }

    companion object {
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 9001
    }

}