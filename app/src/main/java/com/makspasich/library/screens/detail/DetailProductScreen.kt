package com.makspasich.library.screens.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.makspasich.library.image
import com.makspasich.library.model.Product
import com.makspasich.library.model.State

@Composable
fun DetailProductScreen(
    editProduct: (String) -> Unit,
    viewModel: DetailProductViewModel = viewModel()
) {
    val viewState by viewModel.state.collectAsStateWithLifecycle()
    Scaffold(
        floatingActionButton = {
            AnimatedVisibility(visible = !viewState.loading) {
                FloatingActionButton(onClick = { editProduct(viewState.product.key) }) {
                    Icon(imageVector = Icons.Filled.Edit, contentDescription = null)
                }
            }
        }
    ) {
        DetailProductScreenContent(
            modifier = Modifier.padding(it),
            product = viewState.product,
            onStateChange = viewModel::onStateChanged
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun DetailProductScreenContent(
    modifier: Modifier = Modifier,
    product: Product,
    onStateChange: (State) -> Unit,
) {
    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Name: ${product.name}",
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = "Key: ${product.key}",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "UID: ${product.uid}",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "Size: ${product.size}",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "Timestamp: ${product.created()}",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "Expired at: ${product.expiration()}",
            style = MaterialTheme.typography.bodyMedium
        )
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            val entries = State.entries
            val selectedState = product.state
            entries.forEach { state ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(
                        index = state.ordinal,
                        count = entries.size
                    ),
                    icon = { },
                    onClick = { onStateChange(state) },
                    selected = state == selectedState
                ) {
                    Icon(
                        imageVector = state.image(),
                        contentDescription = null,
                        modifier = Modifier.size(SegmentedButtonDefaults.IconSize)
                    )
                }
            }
        }
    }
}