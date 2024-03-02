package com.makspasich.library.screens.products

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import com.makspasich.library.NavigationRoute
import com.makspasich.library.model.Product
import com.makspasich.library.model.State
import com.makspasich.library.util.PRODUCT_KEY_PATTERN

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProductsScreen(
    onProductItemClick: (String) -> Unit,
    signOutClick: () -> Unit,
    barcodeResult: (String) -> Unit,
    viewModel: ProductsViewModel = viewModel()
) {
    val viewState by viewModel.state.collectAsStateWithLifecycle()
    Scaffold(
        floatingActionButton = { SearchProductButton(barcodeResult) },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, top = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(onClick = signOutClick) {
                    Text(text="Sign out")
                }
            }
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

}

@Composable
private fun SearchProductButton(
    barcodeResult: (String) -> Unit
) {
    val context = LocalContext.current

    FloatingActionButton(
        onClick = {
            val optionsBuilder = GmsBarcodeScannerOptions.Builder()
            GmsBarcodeScanning.getClient(context, optionsBuilder.build())
                .startScan()
                .addOnSuccessListener { barcode ->
                    val checkNotNull = checkNotNull(barcode.rawValue)
                    if (PRODUCT_KEY_PATTERN.containsMatchIn(checkNotNull)) {
                        barcodeResult(checkNotNull)
                    } else {
//                                Snackbar.make(binding.root, "Невідомий QR", Snackbar.LENGTH_SHORT)
//                                    .show()
                    }
                }
                .addOnFailureListener { e: Exception ->
//                            Snackbar.make(
//                                binding.root,
//                                "Помилка при скануванні QR",
//                                Snackbar.LENGTH_SHORT
//                            )
//                                .show()
                }
                .addOnCanceledListener { }
        }
    ) {
        Icon(imageVector = Icons.Filled.Search, contentDescription = null)
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
    onProductItemClick: (String) -> Unit,
    products: SnapshotStateList<Product>
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(products, key = { it.key }) { productItem ->
            ProductItem(
                product = productItem,
                onClick = {
                    onProductItemClick(
                        NavigationRoute.ProductDetails.routeWithArgs(
                            productItem.key
                        )
                    )
                }
            )
        }
    }
}
