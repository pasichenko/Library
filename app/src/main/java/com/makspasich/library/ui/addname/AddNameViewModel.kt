package com.makspasich.library.ui.addname

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.makspasich.library.source.ProductRepositoryImpl

class AddNameViewModel : ViewModel() {

    private val _nameProduct = MutableLiveData<String>()
    val nameProduct: LiveData<String> = _nameProduct

    fun addNameProduct() {
        val productRepository = ProductRepositoryImpl()
        productRepository.writeProductName(_nameProduct.value!!) {

        }
    }

    fun setNameProduct(name: String) {
        _nameProduct.value = name
    }
}