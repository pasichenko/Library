package com.makspasich.library.ui.products

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.makspasich.library.ui.products.ProductsFilterType.*

class ProductsViewModel : ViewModel() {
    private val _query: MutableLiveData<Query> = MutableLiveData()
    val query: LiveData<Query> = _query

    fun setFiltering(requestType: ProductsFilterType) {
        when (requestType) {
            ALL_PRODUCTS -> {
                _query.value = FirebaseDatabase.getInstance().reference.child("active")
            }
            SORT_BY_NAME -> {
                _query.value = FirebaseDatabase.getInstance().reference.child("active").orderByChild("name")
            }
            SORT_BY_SIZE -> {
                _query.value = FirebaseDatabase.getInstance().reference.child("active").orderByChild("size")
            }
        }
    }

    init {
        setFiltering(ALL_PRODUCTS)
    }
}