package com.makspasich.library

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardDoubleArrowDown
import androidx.compose.material.icons.filled.KeyboardDoubleArrowUp
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.ui.graphics.vector.ImageVector
import com.makspasich.library.model.State
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import kotlin.math.absoluteValue

fun Long.formatDate(format: String): String? {
    val sdf: DateFormat = SimpleDateFormat(format, Locale.getDefault())
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = this.absoluteValue
    sdf.timeZone = TimeZone.getDefault()
    return sdf.format(calendar.time)
}

fun State.toText(): String {
    return when (this) {
        State.CREATED -> "CREATED"
        State.UNDERGROUND -> "UNDERGROUND"
        State.FOREGROUND -> "FOREGROUND"
        State.UNDEFINED -> "UNDEFINED"
        State.DELETED -> "DELETED"
    }
}

fun State.image(): ImageVector {
    return when (this) {
        State.CREATED -> Icons.Filled.Create
        State.UNDERGROUND -> Icons.Filled.KeyboardDoubleArrowDown
        State.FOREGROUND -> Icons.Filled.KeyboardDoubleArrowUp
        State.UNDEFINED -> Icons.Filled.QuestionMark
        State.DELETED -> Icons.Filled.Delete
    }
}