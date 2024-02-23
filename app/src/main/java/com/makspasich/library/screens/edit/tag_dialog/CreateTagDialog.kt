package com.makspasich.library.screens.edit.tag_dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun CreateTagDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
) {
    val viewModel = CreateTagViewModel()
    var tagName by remember { mutableStateOf("") }
    AddTagDialogContent(
        onDismissRequest = onDismissRequest,
        onConfirmation = {
            viewModel.onTagNameChanged(tagName)
            viewModel.onDoneClick(onConfirmation)
        },
        value = tagName,
        onNewValue = { tagName = it },
    )
}

@Composable
fun AddTagDialogContent(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    value: String,
    onNewValue: (String) -> Unit,
) {
    AlertDialog(
        title = {
            Text(text = "Add tag name")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.Center
            ) {
                OutlinedTextField(
                    singleLine = true,
                    modifier = Modifier,
                    value = value,
                    onValueChange = { onNewValue(it) },
                    placeholder = { Text("Tag name") }
                )
            }
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                },
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onDismissRequest()
            }) {
                Text("Cancel")
            }
        })
}

@Preview(showBackground = false)
@Composable
fun AddTagDialogPreview() {
    val (s, function) = remember { mutableStateOf("") }
    AddTagDialogContent(
        onDismissRequest = {},
        onConfirmation = {},
        value = s,
        onNewValue = { function(it) },
    )
}