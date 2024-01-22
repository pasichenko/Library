package com.makspasich.library

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.makspasich.library.databinding.ActivityMainBinding
import com.makspasich.library.databinding.NavHeaderMainBinding
import com.makspasich.library.ui.login.SignInActivity
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity() {
    private var mAppBarConfiguration: AppBarConfiguration? = null
    private lateinit var binding: ActivityMainBinding
    private lateinit var navBinding: NavHeaderMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        navBinding = NavHeaderMainBinding.bind(binding.navView.getHeaderView(0))
        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)
        bindViews()
        binding.navView.menu.findItem(R.id.nav_logout)
            .setOnMenuItemClickListener { item: MenuItem ->
                if (item.itemId == R.id.nav_logout) {
                    FirebaseAuth.getInstance().signOut()
                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build()
                    val mGoogleSignInClient = GoogleSignIn.getClient(this@MainActivity, gso)
                    // Google sign out
                    mGoogleSignInClient.signOut()
                        .addOnCompleteListener(this@MainActivity) { task: Task<Void?>? ->
                            val intent = Intent(this@MainActivity, SignInActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                }
                binding.drawerLayout.closeDrawer(GravityCompat.START)
                true
            }

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = AppBarConfiguration.Builder(
            R.id.nav_active, R.id.nav_statistic
        )
            .setOpenableLayout(binding.drawerLayout)
            .build()
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration!!)
        NavigationUI.setupWithNavController(binding.navView, navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        return (NavigationUI.navigateUp(navController, mAppBarConfiguration!!)
                || super.onSupportNavigateUp())
    }

    private fun bindViews() {
        navBinding.displayName.text = FirebaseAuth.getInstance().currentUser!!.displayName
        navBinding.email.text = FirebaseAuth.getInstance().currentUser!!.email
        val photoUri = FirebaseAuth.getInstance().currentUser!!.photoUrl
        Picasso.get()
            .load(photoUri)
            .into(navBinding.avatarImage)
    }
}