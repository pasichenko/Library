package com.makspasich.library.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.makspasich.library.PRODUCT_ID
import com.makspasich.library.model.Product
import com.makspasich.library.model.State
import com.makspasich.library.model.service.StorageService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetailProductViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    private val storageService = StorageService()
    private val productId: String = checkNotNull(savedStateHandle[PRODUCT_ID])
    private val loading = MutableStateFlow(true)
    private val _state = MutableStateFlow(ProductDetailsState(product = Product(key = productId)))
    val state: StateFlow<ProductDetailsState>
        get() = _state

    init {
        viewModelScope.launch {
            combine(
                storageService.getProduct(productId)
                    .mapNotNull { it },
                loading
            ) { product, loading ->
                ProductDetailsState(loading = false, product = product)
            }.catch { throwable ->
                throw throwable
            }.collect {
                _state.value = it
            }
        }
    }

    fun onStateChanged(newState: State) {
        _state.update { productDetailsState ->
            productDetailsState.copy(
                product = productDetailsState.product.copy(
                    state = newState
                )
            )
        }
        viewModelScope.launch {
            storageService.update(_state.value.product)
        }
    }
}

data class ProductDetailsState(
    val loading: Boolean = true,
    val product: Product = Product()
)