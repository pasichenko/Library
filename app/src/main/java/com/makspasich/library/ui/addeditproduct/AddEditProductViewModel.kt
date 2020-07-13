package com.makspasich.library.ui.addeditproduct

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.makspasich.library.Event
import com.makspasich.library.models.Product
import com.makspasich.library.models.ProductName
import com.makspasich.library.models.ProductSize
import com.makspasich.library.source.ProductRepositoryImpl
import kotlinx.coroutines.launch

class AddEditProductViewModel : ViewModel() {

    private val _keyLiveData = MutableLiveData<String>()
    val keyLiveData: LiveData<String> = _keyLiveData

    val uid: String = Firebase.auth.currentUser!!.uid

    private val _yearLiveData = MutableLiveData<String>()
    val yearLiveData: LiveData<String> = _yearLiveData

    private val _nameLiveData = MutableLiveData<String>()
    val nameLiveData: LiveData<String> = _nameLiveData

    private val _sizeLiveData = MutableLiveData<String>()
    val sizeLiveData: LiveData<String> = _sizeLiveData

    private val _monthLiveData = MutableLiveData<String>()
    val monthLiveData: LiveData<String> = _monthLiveData

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _sizesLiveData = MutableLiveData<List<ProductSize>>()
    val sizesLiveData: LiveData<List<ProductSize>> = _sizesLiveData

    private val _namesLiveData = MutableLiveData<List<ProductName>>()
    val namesLiveData: LiveData<List<ProductName>> = _namesLiveData

    private val _productUpdatedEvent = MutableLiveData<Event<Unit>>()
    val productUpdatedEvent: LiveData<Event<Unit>> = _productUpdatedEvent

    private val _isSavedLiveData = MutableLiveData<Boolean>()
    val isSavedLiveData: LiveData<Boolean> = _isSavedLiveData

    private var isNewProduct: Boolean = false

    private var isDataLoaded = false
    private lateinit var oldProduct: Product
    private fun initSizes() {
        Firebase.database.reference.child("product-sizes").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list: MutableList<ProductSize> = mutableListOf()
                for (name in snapshot.children) {
                    val productSize: ProductSize? = name.getValue<ProductSize>()
                    productSize?.let {
                        list.add(productSize)
                    }
                }
                _sizesLiveData.value = list
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun initNames() {
        Firebase.database.reference.child("product-names").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list: MutableList<ProductName> = mutableListOf()
                for (name in snapshot.children) {
                    val productName: ProductName? = name.getValue<ProductName>()
                    productName?.let {
                        list.add(productName)
                    }
                }

                _namesLiveData.value = list
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

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
                Firebase.database.reference.child("active")
                        .child(keyProduct)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(rootSnapshot: DataSnapshot) {
                                if (rootSnapshot.exists()) {
                                    val product = rootSnapshot.getValue<Product>()
                                    onPostLoaded(product!!)
                                } else {
                                    onDataNotAvailable()
                                }
                            }

                            override fun onCancelled(p0: DatabaseError) {
                                TODO("Not yet implemented")
                            }
                        })
            }
        }
    }

    private fun onPostLoaded(product: Product) {
        oldProduct = product
        _keyLiveData.value = product.key
        _yearLiveData.value = product.year
        _nameLiveData.value = product.name
        _sizeLiveData.value = product.size
        _monthLiveData.value = product.month
        _dataLoading.value = false
        isDataLoaded = true
    }

    private fun onDataNotAvailable() {
        _dataLoading.value = false
    }

    fun saveProduct() {
        val product = Product(
                key = _keyLiveData.value,
                uid = uid,
                year = _yearLiveData.value,
                name = _nameLiveData.value,
                size = _sizeLiveData.value,
                month = _monthLiveData.value,
                isActive = true
        )
        val productRepository = ProductRepositoryImpl()
        if (isNewProduct) {
            productRepository.addProduct(_keyLiveData.value!!, product)
        } else {
            productRepository.updateProduct(_keyLiveData.value!!, oldProduct = oldProduct, newProduct = product)

        }
        _isSavedLiveData.value = true


        _productUpdatedEvent.value = Event(Unit)
    }

    init {
        initSizes()
        initNames()
    }

    fun setKeyProduct(key: String?) {
        _keyLiveData.value = key
    }

    fun setYearProduct(year: String?) {
        _yearLiveData.value = year
    }

    fun setNameProduct(name: String?) {
        _nameLiveData.value = name
    }

    fun setSizeProduct(size: String?) {
        _sizeLiveData.value = size
    }

    fun setMonthProduct(month: String?) {
        _monthLiveData.value = month
    }
}