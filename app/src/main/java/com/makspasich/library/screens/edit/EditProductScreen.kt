package com.makspasich.library.screens.edit

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.makspasich.library.model.Product
import com.makspasich.library.model.TagName
import com.makspasich.library.screens.edit.tag_dialog.CreateTagDialog
import java.util.Locale

@Composable
fun EditProductScreen(
    popUp: () -> Unit,
    viewModel: EditProductViewModel = viewModel()
) {
    val viewState by viewModel.state.collectAsStateWithLifecycle()
    Scaffold(
        floatingActionButton = {
            AnimatedVisibility(visible = !viewState.loading) {
                FloatingActionButton(onClick = { viewModel.saveProduct(popUp) }) {
                    Icon(imageVector = Icons.Filled.Check, contentDescription = null)
                }
            }
        }
    ) {
        var showDialog by remember { mutableStateOf(false) }

        if (showDialog) {
            CreateTagDialog(
                onDismissRequest = { showDialog = false },
                onConfirmation = { showDialog = false }
            )
        }
        if (viewState.loading) {
            CircularProgressIndicator()
        } else {
            EditProductScreenContent(
                modifier = Modifier.padding(it),
                product = viewState.product,
                tags = viewState.tags,
                onNameChange = viewModel::onNameChange,
                onTimestampChange = viewModel::onTimestampChange,
                onExpiredTimestampChange = viewModel::onExpiredTimestampChange,
                onSizeChange = viewModel::onSizeChange,
                onTagChange = viewModel::addOrRemoveTag,
                onClickAddTag = {
                    showDialog = true
                }
            )
        }
    }
}


@Composable
fun EditProductScreenContent(
    modifier: Modifier = Modifier,
    product: Product,
    tags: List<TagName>,
    onNameChange: (String) -> Unit,
    onTimestampChange: (Long) -> Unit,
    onExpiredTimestampChange: (Long) -> Unit,
    onSizeChange: (String) -> Unit,
    onTagChange: (TagName) -> Unit,
    onClickAddTag: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val (showTimestampDialog, onShowTimestampDialogChange) =
            remember { mutableStateOf(false) }
        val (showExpiredTimestampDialog, onShowExpiredTimestampDialogChange) =
            remember { mutableStateOf(false) }
        DisplayingDatePickerDialog(
            value = showTimestampDialog,
            onChange = onShowTimestampDialogChange,
            onTimestampChange = onTimestampChange,
            initialSelectedDateMillis = product.timestamp
        )
        DisplayingDatePickerDialog(
            value = showExpiredTimestampDialog,
            onChange = onShowExpiredTimestampDialogChange,
            onTimestampChange = onExpiredTimestampChange,
            initialSelectedDateMillis = product.expirationTimestamp
        )
        OutlinedTextField(
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            value = product.key,
            onValueChange = { },
            enabled = false,
            placeholder = { Text("Key") }
        )
        BasicField(text = "Name product", value = product.name!!, onNewValue = onNameChange)
        DatePickerField(
            onShowTimestampDialogChange = onShowTimestampDialogChange,
            timestamp = product.timestamp,
            label = "Timestamp"
        )
        DatePickerField(
            onShowTimestampDialogChange = onShowExpiredTimestampDialogChange,
            timestamp = product.expirationTimestamp,
            label = "Expiration timestamp"
        )
        BasicField(text = "Size", value = product.size!!, onNewValue = onSizeChange)
        GroupChipTags(tags, product, onTagChange, onClickAddTag)
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun DatePickerField(
    onShowTimestampDialogChange: (Boolean) -> Unit,
    timestamp: Long?,
    label: String
) {
    OutlinedTextField(
        singleLine = true,
        enabled = false,
        modifier = Modifier
            .clickable { onShowTimestampDialogChange(true) }
            .fillMaxWidth(),
        value = DatePickerDefaults.dateFormatter()
            .formatDate(timestamp, Locale.getDefault()) ?: "",
        onValueChange = { },
        label = { Text(text = label) },
        placeholder = { Text(text = label) }
    )
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun GroupChipTags(
    tags: List<TagName>,
    product: Product,
    onTagChange: (TagName) -> Unit,
    onClickAddTag: () -> Unit
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        tags.forEachIndexed { index, tag ->
            val selected = product.tags.containsKey(tag.key)
            FilterChip(
                onClick = { onTagChange(tag) },
                selected = selected,
                label = { Text(text = tag.name) },
                leadingIcon = {
                    if (selected) {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = "Done icon",
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                },
            )
            if (index == tags.size - 1) {
                ChipAddTag(onClickAddTag)

            }
        }

        if (tags.isEmpty()) {
            ChipAddTag(onClickAddTag)
        }
    }
}

@Composable
private fun ChipAddTag(onClickAddTag: () -> Unit) {
    AssistChip(
        onClick = { onClickAddTag() },
        label = { Text(text = "Add tag") },
        leadingIcon = {
            Icon(
                Icons.Filled.Add,
                contentDescription = "Localized description",
                Modifier.size(AssistChipDefaults.IconSize)
            )
        }
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun DisplayingDatePickerDialog(
    value: Boolean,
    onChange: (Boolean) -> Unit,
    onTimestampChange: (Long) -> Unit,
    initialSelectedDateMillis: Long?
) {
    val timestampDatePickerState =
        rememberDatePickerState(initialSelectedDateMillis = initialSelectedDateMillis)

    if (value) {
        DatePickerDialog(
            onDismissRequest = { onChange(false) },
            confirmButton = {
                TextButton(onClick = {
                    onChange(false)
                    onTimestampChange(timestampDatePickerState.selectedDateMillis!!)
                }) {
                    Text(text = "Confirm")
                }
            },
        ) {
            DatePicker(state = timestampDatePickerState)
        }
    }
}

@Composable
fun BasicField(
    @StringRes text: Int,
    value: String,
    onNewValue: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        singleLine = true,
        modifier = modifier,
        value = value,
        onValueChange = { onNewValue(it) },
        placeholder = { Text(stringResource(text)) }
    )
}

@Composable
fun BasicField(
    text: String,
    value: String,
    onNewValue: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        singleLine = true,
        modifier = modifier.fillMaxWidth(),
        value = value,
        onValueChange = { onNewValue(it) },
        label = { Text(text = text) },
        placeholder = { Text(text = text) }
    )
}

