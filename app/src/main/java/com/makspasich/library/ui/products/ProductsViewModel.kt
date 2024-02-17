package com.makspasich.library.ui.products

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.makspasich.library.model.TagName

class ProductsViewModel : ViewModel() {
    var filters: List<TagName> = ArrayList()
    private val _query: MutableLiveData<Query> = MutableLiveData()
    val query: LiveData<Query> = _query

    fun setQuery(query: Query) {
        _query.value = query
    }

    init {
        setQuery(Firebase.firestore.collection("products"))
    }
}