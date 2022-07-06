package com.makspasich.library.ui.filter_products_dialog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.makspasich.library.models.TagName

class FilterProductsViewModel : ViewModel() {

    private val _allTagsLiveData = MutableLiveData<MutableList<TagName>>()
    val allTagsLiveData: LiveData<MutableList<TagName>> = _allTagsLiveData
    private val _filterTagsLiveData = MutableLiveData<MutableList<TagName>>()
    val filterTagsLiveData: LiveData<MutableList<TagName>> = _filterTagsLiveData

    init {
        _allTagsLiveData.value = ArrayList()
        _filterTagsLiveData.value = ArrayList()
        Firebase.firestore.collection("tags")
            .addSnapshotListener { documents, error ->
                if (error != null) {
                    return@addSnapshotListener
                }

                if (documents == null) {
                    return@addSnapshotListener
                }
                for (change in documents.documentChanges) {
                    when (change.type) {
                        DocumentChange.Type.ADDED -> {
                            _allTagsLiveData.value?.add(change.newIndex, change.document.toObject())
                        }
                        DocumentChange.Type.MODIFIED -> {
                            if (change.oldIndex == change.newIndex) {
                                // Item changed but remained in same position
                                _allTagsLiveData.value?.set(
                                    change.oldIndex,
                                    change.document.toObject()
                                )
                            } else {
                                // Item changed and changed position
                                _allTagsLiveData.value?.removeAt(change.oldIndex)
                                _allTagsLiveData.value?.add(
                                    change.newIndex,
                                    change.document.toObject()
                                )
                            }
                        }
                        DocumentChange.Type.REMOVED -> {
                            _allTagsLiveData.value?.removeAt(change.oldIndex)
                        }
                    }
                    _allTagsLiveData.value?.sortBy { it.name }
                    _allTagsLiveData.value = _allTagsLiveData.value
                }
            }
    }

    fun addTagProduct(tag: TagName) {
        _filterTagsLiveData.value?.add(tag)
        _filterTagsLiveData.value = _filterTagsLiveData.value
    }

    fun removeTagProduct(tag: TagName) {
        _filterTagsLiveData.value?.remove(tag)
        _filterTagsLiveData.value = _filterTagsLiveData.value
    }
}