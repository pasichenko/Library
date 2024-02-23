package com.makspasich.library.ui.addeditproduct

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.makspasich.library.model.Product
import com.makspasich.library.model.State
import com.makspasich.library.model.TagName
import com.makspasich.library.model.service.StorageService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddEditProductViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    private val storageService = StorageService()
    private val productId: String = checkNotNull(savedStateHandle["productId"])
    private val _state = MutableStateFlow(
        ProductEditState(
            product = Product(key = productId, state = State.CREATED),
            tags = listOf()
        )
    )
    val state: StateFlow<ProductEditState>
        get() = _state

    init {
        viewModelScope.launch {
            _state.update { it.copy(loading = true) }
            combine(
                storageService.getProduct(productId),
                storageService.getLastProduct(Firebase.auth.uid.orEmpty()),
                storageService.getTags()
            ) { product, lastProduct, tags ->
                product?.let {
                    ProductEditState(
                        loading = false,
                        newProduct = false,
                        product = it,
                        tags = tags
                    )
                } ?: lastProduct?.let {
                    ProductEditState(
                        loading = false,
                        newProduct = false,
                        product = it.copy(key = productId, state = State.CREATED),
                        tags = tags
                    )
                } ?: ProductEditState(
                    loading = false,
                    newProduct = true,
                    product = Product(key = productId, state = State.CREATED),
                    tags = tags
                )
            }.catch { throwable ->
                throw throwable
            }.collect { editState ->
                _state.update { editState }
            }
        }
    }

    fun onNameChange(newValue: String) {
        _state.update { it.copy(product = it.product.copy(name = newValue)) }
    }

    fun onTimestampChange(newValue: Long) {
        _state.update { it.copy(product = it.product.copy(timestamp = newValue)) }
    }

    fun onExpiredTimestampChange(newValue: Long) {
        _state.update { it.copy(product = it.product.copy(expirationTimestamp = newValue)) }
    }

    fun onSizeChange(newValue: String) {
        _state.update { it.copy(product = it.product.copy(size = newValue)) }
    }

    fun addOrRemoveTag(value: TagName) {
        _state.update { state ->
            val productTags = state.product.tags.toMutableMap()
            if (productTags.containsKey(value.key)) {
                productTags.remove(value.key)
            } else {
                productTags[value.key] = value
            }
            state.copy(product = state.product.copy(tags = productTags))
        }
    }


    fun onStateChange(newValue: State) {
        _state.update { it.copy(product = it.product.copy(state = newValue)) }
    }

    fun saveProduct(popUp: () -> Unit) {
        viewModelScope.launch {
            storageService.update(_state.value.product)
            storageService.updateLastInserted(_state.value.product)
            storageService.updateQrState(productId, "USE")
            popUp()
        }
    }
}

data class ProductEditState(
    val loading: Boolean = true,
    val newProduct: Boolean = false,
    val product: Product = Product(),
    val tags: List<TagName> = listOf()
)