package com.makspasich.library.screens.products

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.makspasich.library.model.State

@Composable
fun StatisticCard(
    modifier: Modifier = Modifier,
    statistics: Map<Int, Map<State, Long>>,
) {
    OutlinedCard(modifier = modifier) {
        Text(
            modifier = Modifier.padding(12.dp),
            text = "Implement later"
        )
    }
}