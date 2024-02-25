package com.makspasich.library.screens.products

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.Query
import com.makspasich.library.model.Product
import com.makspasich.library.model.State
import com.makspasich.library.model.service.StorageService
import com.makspasich.library.util.convertProductsToStatistic
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class ProductsViewModel : ViewModel() {
    private val storageService = StorageService()

    private val loading = MutableStateFlow(true)
    private val _state = MutableStateFlow(ProductsScreenState())
    val state: StateFlow<ProductsScreenState>
        get() = _state

    private fun query(): (Query) -> Query =
        { it }

    init {
        viewModelScope.launch {
            combine(
                storageService.getProducts(query()),
                loading
            ) { products, loading ->
                ProductsScreenState(
                    loading = loading,
                    products = products.toMutableStateList(),
                    statistics = convertProductsToStatistic(products)
                )
            }
                .catch { throwable ->
                    throw throwable
                }.collect {
                    _state.value = it
                }
        }
    }
}

data class ProductsScreenState(
    val loading: Boolean = true,
    val products: SnapshotStateList<Product> = mutableStateListOf(),
    val statistics: Map<Int, Map<State, Long>> = mapOf()
)