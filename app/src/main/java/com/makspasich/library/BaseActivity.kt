package com.makspasich.library

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {
    @VisibleForTesting
    var mProgressBar: ProgressBar? = null
    fun setProgressBar(resId: Int) {
        mProgressBar = findViewById(resId)
    }

    fun showProgressBar() {
        mProgressBar?.let {
            it.visibility = View.VISIBLE
        }
    }

    fun hideProgressBar() {
        mProgressBar?.let {
            it.visibility = View.INVISIBLE
        }
    }

    fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    public override fun onStop() {
        super.onStop()
        hideProgressBar()
    }
}