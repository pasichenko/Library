package com.makspasich.library.ui.detail

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.makspasich.library.Event
import com.makspasich.library.models.Product
import com.makspasich.library.source.ProductRepositoryImpl
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


        this.productId?.let {
//            viewModelScope.launch {
                rootReference.child("active")
                        .child(it)
                        .addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(rootSnapshot: DataSnapshot) {
                                if (rootSnapshot.exists()) {
                                    val product = rootSnapshot.getValue<Product>()
                                    onProductLoaded(product!!)
                                } else {
                                    onDataNotAvailable()
                                }
                            }

                            override fun onCancelled(p0: DatabaseError) {
                                onDataNotAvailable()
                                TODO("Not yet implemented")
                            }
                        })
//            }
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
            val productRepository = ProductRepositoryImpl()
            productRepository.deleteProduct(it)
            _deleteTaskEvent.value = Event(Unit)
        }
    }

    fun archiveProduct() {
        val productRepository = ProductRepositoryImpl()
        if (_product.value!!.isActive) {
            productRepository.moveToArchive(_product.value!!)
        } else {
            productRepository.moveToActive(_product.value!!)
        }
    }

    private fun showSnackbarMessage(@StringRes message: Int) {
        _snackbarText.value = Event(message)
    }
}