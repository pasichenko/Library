package com.makspasich.library

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}

/**
 * Custom realization two way binding without DataBinding library
 */
fun EditText.twoWayBinding(
    lifecycleOwner: LifecycleOwner,
    getDataFromViewModel: LiveData<String>,
    setDataToViewModel: (String) -> Unit
) {
    getDataFromViewModel.observe(lifecycleOwner, Observer {
        if (it == this.text.toString()) {
            return@Observer
        }
        this.setText(it)
    })
    this.afterTextChanged(setDataToViewModel)
}

/**
 * Transforms static java function Snackbar.make() to an extension function on View.
 */
fun View.showSnackbar(
    snackbarText: CharSequence,
    timeLength: Int,
    textAction: String? = null,
    listener: View.OnClickListener? = null
) {
    Snackbar.make(this, snackbarText, timeLength).setAction(textAction, listener).run {
        show()
    }
}

/**
 * Overload extension for using string resources
 */
fun View.showSnackbar(
        snackbarText: Int,
        timeLength: Int,
        textAction: String? = null,
        listener: View.OnClickListener? = null
) {
    showSnackbar(context.getString(snackbarText), timeLength, textAction, listener)
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
    imm?.hideSoftInputFromWindow(this.windowToken, 0)
}