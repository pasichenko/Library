package com.makspasich.library.ui.addeditproduct

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.makspasich.library.Event
import com.makspasich.library.model.Product
import com.makspasich.library.model.State
import com.makspasich.library.model.TagName
import kotlinx.coroutines.launch
import java.math.BigDecimal

class AddEditProductViewModel : ViewModel() {

    val uid: String = Firebase.auth.currentUser!!.uid

    private val _keyLiveData = MutableLiveData<String>()
    val keyLiveData: LiveData<String> = _keyLiveData

    private val _timestampLiveData = MutableLiveData<Long>()
    val timestampLiveData: LiveData<Long> = _timestampLiveData

    private val _expirationTimestampLiveData = MutableLiveData<Long>()
    val expirationTimestampLiveData: LiveData<Long> = _expirationTimestampLiveData

    private val _nameLiveData = MutableLiveData<String>()
    val nameLiveData: LiveData<String> = _nameLiveData

    private val _sizeLiveData = MutableLiveData<String>()
    val sizeLiveData: LiveData<String> = _sizeLiveData

    private val _stateLiveData = MutableLiveData<State>()
    val stateLiveData: LiveData<State> = _stateLiveData

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _productUpdatedEvent = MutableLiveData<Event<Unit>>()
    val productUpdatedEvent: LiveData<Event<Unit>> = _productUpdatedEvent

    private val _allTagsLiveData = MutableLiveData<MutableList<TagName>>()
    val allTagsLiveData: LiveData<MutableList<TagName>> = _allTagsLiveData
    private val _productTagsLiveData = MutableLiveData<MutableList<TagName>>()
    val productTagsLiveData: LiveData<MutableList<TagName>> = _productTagsLiveData

    private val _isSavedLiveData = MutableLiveData<Boolean>()
    val isSavedLiveData: LiveData<Boolean> = _isSavedLiveData

    private var isNewProduct: Boolean = false

    private var isDataLoaded = false

    fun start(keyProduct: String, isNewProduct: Boolean) {
        if (_dataLoading.value == true) {
            return
        }

        _keyLiveData.value = keyProduct

        if (isDataLoaded) {
            // No need to populate, already have data.
            return
        }

        this.isNewProduct = isNewProduct
        _dataLoading.value = true
        if (!isNewProduct) {
            viewModelScope.launch {
                Firebase.firestore.collection("products")
                    .document(keyProduct)
                    .get()
                    .addOnSuccessListener { documents ->
                        if (documents.exists()) {
                            val product = documents.toObject<Product>()
                            initLiveDataByProduct(product, keyProduct)
                        }
                    }
            }
        } else {
            setStateProduct(State.CREATED)
            Firebase.firestore.collection("last-inserted")
                .document(Firebase.auth.uid!!)
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.exists()) {
                        val product = documents.toObject<Product>()
                        initLiveDataByProduct(product, keyProduct)
                    }
                }
        }
    }

    private fun initLiveDataByProduct(product: Product?, keyProduct: String) {
        product?.let {
            _keyLiveData.value = keyProduct
            _timestampLiveData.value = it.timestamp ?: 0
            _expirationTimestampLiveData.value = it.expirationTimestamp ?: 0
            _nameLiveData.value = it.name ?: ""
            _sizeLiveData.value = it.size.toString() ?: BigDecimal("0.0").toString()
            _stateLiveData.value = it.state ?: State.CREATED
            val map = it.tags
            _productTagsLiveData.value = map.entries
//                .filter { mutableEntry -> mutableEntry.value }
                .map { entry -> entry.value }
                .toMutableList()
            _dataLoading.value = false
            isDataLoaded = true
        }

    }

    private fun onDataNotAvailable() {
        _dataLoading.value = false
    }

    fun saveProduct() {
        val tags:MutableMap<String,TagName> = HashMap()
        for (tag in _productTagsLiveData.value!!.iterator()) {
            tags[tag.key] = tag
        }
        val product = Product(
            key = _keyLiveData.value ?: "",
            uid = uid,
            name = _nameLiveData.value,
            timestamp = _timestampLiveData.value,
            expirationTimestamp = _expirationTimestampLiveData.value,
            size = _sizeLiveData.value,
            state = _stateLiveData.value?:State.CREATED,
            tags = tags
        )
        Firebase.firestore.collection("last-inserted").document(Firebase.auth.uid!!).set(product)

        Firebase.firestore.collection("products").document(_keyLiveData.value!!).set(product)
        _isSavedLiveData.value = true
        _productUpdatedEvent.value = Event(Unit)
    }

    fun updateQRState() {
        val hashMap = HashMap<String, Any>()
        hashMap["state"] = "USE"
        Firebase.firestore.collection("qr-codes")
            .document(_keyLiveData.value!!)
            .update(hashMap)
    }

    init {
        _allTagsLiveData.value = ArrayList()
        _productTagsLiveData.value = ArrayList()
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

    fun setKeyProduct(key: String) {
        _keyLiveData.value = key
    }

    fun setTimestampProduct(timestamp: Long?) {
        _timestampLiveData.value = timestamp ?: 0
    }

    fun setExpirationTimestampProduct(expirationTimestamp: Long?) {
        _expirationTimestampLiveData.value = expirationTimestamp ?: 0
    }

    fun setNameProduct(name: String?) {
        _nameLiveData.value = name ?: ""
    }

    fun setSizeProduct(size: BigDecimal?) {
        _sizeLiveData.value = size?.toString().let { BigDecimal(0.0).toString() }
    }

    fun setSizeProduct(size: String) {
        _sizeLiveData.value = size
    }

    fun setStateProduct(state: State) {
        _stateLiveData.value = state
    }

    fun addTagProduct(tag: TagName) {
        _productTagsLiveData.value?.add(tag)
        _productTagsLiveData.value = _productTagsLiveData.value
    }

    fun removeTagProduct(tag: TagName) {
        _productTagsLiveData.value?.remove(tag)
        _productTagsLiveData.value = _productTagsLiveData.value
    }
}