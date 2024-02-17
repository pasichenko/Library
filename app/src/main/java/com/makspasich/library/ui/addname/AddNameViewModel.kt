package com.makspasich.library.ui.addname

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.makspasich.library.model.TagName

class AddNameViewModel : ViewModel() {

    private val _nameProduct = MutableLiveData<String>()
    val nameProduct: LiveData<String> = _nameProduct

    fun addNameProduct() {
        val collection = Firebase.firestore.collection("tags")

        collection.get().addOnSuccessListener { documents ->
            var exist = false
            for (tag in documents) {
                if (tag.toObject<TagName>().name == _nameProduct.value!!) {
                    exist = true
                    break
                }
            }
            if (!exist) {
                val id = collection.document().id
                collection.document(id).set(TagName(id, _nameProduct.value!!))
            }
        }
    }

    fun setNameProduct(name: String) {
        _nameProduct.value = name
    }
}