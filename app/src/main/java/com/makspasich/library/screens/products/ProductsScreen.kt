package com.makspasich.library.screens.products

import android.view.View
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.makspasich.library.model.Product
import com.makspasich.library.model.State

@Composable
fun ProductsScreen(
    onProductItemClick: (String, View) -> Unit = { key, view -> },
    viewModel: ProductsViewModel = viewModel()
) {
    val viewState by viewModel.state.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, top = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatisticContent(
            modifier = Modifier.fillMaxWidth(),
            statistics = viewState.statistics
        )
        ProductsContent(
            onProductItemClick = onProductItemClick,
            products = viewState.products
        )
    }

}

@Composable
fun StatisticContent(
    modifier: Modifier = Modifier,
    statistics: Map<Int, Map<State, Long>>
) {
    StatisticCard(
        modifier = modifier,
        statistics = statistics
    )
}

@Composable
fun ProductsContent(
    modifier: Modifier = Modifier,
    onProductItemClick: (String, View) -> Unit,
    products: SnapshotStateList<Product>
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(products, key = { it.key }) { productItem ->
            val current = LocalView.current
            ProductItem(product = productItem) {
                onProductItemClick(productItem.key, current)
            }
        }
    }
}
