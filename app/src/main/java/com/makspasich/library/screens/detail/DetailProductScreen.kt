package com.makspasich.library.screens.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.makspasich.library.image
import com.makspasich.library.model.Product
import com.makspasich.library.model.State

@Composable
fun DetailProductScreen() {

}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun DetailProductScreenContent(
    product: Product,
    onStateChange: (State) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier
            .fillMaxSize()
            .padding(16.dp)
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