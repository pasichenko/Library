package com.makspasich.library

import android.view.View
import android.widget.ProgressBar
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {
    @VisibleForTesting
   lateinit var mProgressBar: ProgressBar
    fun setProgressBar(resId: Int) {
        mProgressBar = findViewById(resId)
    }

    fun showProgressBar() {
        mProgressBar.visibility = View.VISIBLE
    }

    fun hideProgressBar() {
        mProgressBar.visibility = View.INVISIBLE
    }

    public override fun onStop() {
        super.onStop()
        hideProgressBar()
    }
}