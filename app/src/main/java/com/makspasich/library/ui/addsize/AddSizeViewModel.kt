package com.makspasich.library.ui.addsize

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.makspasich.library.source.ProductRepositoryImpl

class AddSizeViewModel : ViewModel() {

    private val _sizeProduct = MutableLiveData<String>()
    val sizeProduct: LiveData<String> = _sizeProduct

    fun addSizeProduct() {
        val productRepository = ProductRepositoryImpl()
        productRepository.writeProductSize(_sizeProduct.value!!)
    }

    fun setSizeProduct(name: String) {
        _sizeProduct.value = name
    }
}