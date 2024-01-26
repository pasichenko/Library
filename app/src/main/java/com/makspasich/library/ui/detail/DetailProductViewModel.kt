package com.makspasich.library.ui.detail

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.makspasich.library.Event
import com.makspasich.library.models.Product
import com.makspasich.library.models.State
import kotlinx.coroutines.launch

class DetailProductViewModel : ViewModel() {
    private val rootReference: DatabaseReference = Firebase.database.reference

    private val _product = MutableLiveData<Product>()
    val product: LiveData<Product> = _product

    private val _editTaskEvent = MutableLiveData<Event<Unit>>()
    val editTaskEvent: LiveData<Event<Unit>> = _editTaskEvent

    private val _deleteTaskEvent = MutableLiveData<Event<Unit>>()
    val deleteTaskEvent: LiveData<Event<Unit>> = _deleteTaskEvent

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText

    private lateinit var productId: String

    private var isDataLoaded = false

    fun start(productId: String) {
        // Trigger the load
        this.productId = productId
        // If we're already loading or already loaded, return (might be a config change)
        if (_dataLoading.value == true) {
            return
        }
        if (isDataLoaded) {
            // No need to populate, already have data.
            return
        }


        this.productId.let {
            //            viewModelScope.launch {
            Firebase.firestore.collection("products")
                .document(it)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val product = document.toObject<Product>()
                        onProductLoaded(product!!)
                    } else {
                        onDataNotAvailable()
                    }
                }
        }
    }

    private fun onProductLoaded(product: Product) {
        _product.value = product
        _dataLoading.value = false
        isDataLoaded = true
    }

    private fun onDataNotAvailable() {
        _dataLoading.value = false
    }

    fun editTask() {
        _editTaskEvent.value = Event(Unit)
    }

    fun deleteProduct() = viewModelScope.launch {
        this@DetailProductViewModel._product.value?.let {
            Firebase.firestore.collection("products").document(it.key!!).delete()
            _deleteTaskEvent.value = Event(Unit)
        }
    }

    fun updateState(state: State) {
        Firebase.firestore.collection("products")
            .document(_product.value!!.key!!)
            .update("state", state)
    }

    private fun showSnackbarMessage(@StringRes message: Int) {
        _snackbarText.value = Event(message)
    }
}