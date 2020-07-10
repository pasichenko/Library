package com.makspasich.library.source

import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.makspasich.library.models.*

class ProductRepositoryImpl : ProductRepository {

    private val activeReference = Firebase.database.reference.child("active")
    private val productNameReference = Firebase.database.reference.child("product-names")
    private val productNameIndexReference = Firebase.database.reference.child("name-index")
    private val productSizeIndexReference = Firebase.database.reference.child("size-index")
    private val statisticReference = Firebase.database.reference.child("product-statistic")
    private val productSizeReference = Firebase.database.reference.child("product-sizes")

    override fun writeProduct(key: String, product: Product) {
        product.name?.let { name ->
            writeProductName(name) { productNameObj ->
                product.nameObj = productNameObj
                increment(product)
                activeReference.child(key).setValue(product)
            }
        }
        product.size?.let {
            writeProductSize(it)
        }
    }

    override fun moveToActive(product: Product) {
        decrement(product)
        val newProduct = product.copy(isActive = true)
        increment(newProduct)
        activeReference.child(product.key!!).setValue(newProduct)
    }

    override fun moveToArchive(product: Product) {
        decrement(product)
        val newProduct = product.copy(isActive = false)
        increment(newProduct)
        activeReference.child(product.key!!).setValue(newProduct)
    }


    override fun deleteProduct(product: Product) {
        decrement(product)
        activeReference.child(product.key!!).removeValue()
    }

    override fun writeProductName(productName: String, onComplete: (ProductName) -> Unit) {
        productNameIndexReference.addListenerForSingleValueEvent(object : ValueEventListener {
            fun createName(productName: String, index: Index): ProductName {
                val key: String = statisticReference.push().key!!
                val productStatistic = ProductStatistic(
                        name = productName,
                        key = key
                )
                statisticReference.child(key).setValue(productStatistic)
                val productNameObj = ProductName(
                        name = productName,
                        key = key
                )
                productNameReference.child(key).setValue(productNameObj)
                index.index[productName] = key
                productNameIndexReference.setValue(index)
                return productNameObj
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                var index = snapshot.getValue(Index::class.java)
                val productNameObj: ProductName
                if (index != null) {
                    productNameObj = if (!index.index.containsKey(productName)) {
                        createName(productName = productName, index = index)
                    } else {
                        ProductName(
                                name = productName,
                                key = index.index.getValue(productName)
                        )
                    }
                } else {
                    index = Index()
                    productNameObj = createName(productName = productName, index = index)
                }
                onComplete.invoke(productNameObj)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun increment(product: Product) {
        statisticReference.child(product.nameObj!!.key!!).runTransaction(object : Transaction.Handler {
            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                val p = mutableData.getValue<ProductStatistic>()
                        ?: return Transaction.success(mutableData)
                if (product.isActive) {
                    p.countActive = p.countActive + 1
                } else {
                    p.countArchive = p.countArchive + 1
                }
                // Set value and report transaction success
                mutableData.value = p
                return Transaction.success(mutableData)
            }

            override fun onComplete(
                    databaseError: DatabaseError?,
                    committed: Boolean,
                    currentData: DataSnapshot?
            ) {

            }
        })
    }

    private fun decrement(product: Product) {
        statisticReference.child(product.nameObj!!.key!!).runTransaction(object : Transaction.Handler {
            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                val p = mutableData.getValue<ProductStatistic>()
                        ?: return Transaction.success(mutableData)
                if (product.isActive) {
                    p.countActive = p.countActive - 1
                } else {
                    p.countArchive = p.countArchive - 1
                }
                // Set value and report transaction success
                mutableData.value = p
                return Transaction.success(mutableData)
            }

            override fun onComplete(
                    databaseError: DatabaseError?,
                    committed: Boolean,
                    currentData: DataSnapshot?
            ) {

            }
        })
    }

    override fun writeProductSize(productSize: String) {
        productSizeReference.addListenerForSingleValueEvent(object : ValueEventListener {
            fun createSize(productSize: String): ProductSize {
                val key: String = productSizeReference.push().key!!
                val productSizeObj = ProductSize(
                        size = productSize,
                        key = key
                )
                productSizeReference.child(key).setValue(productSizeObj)
                return productSizeObj
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                var flag: Boolean = false
                var productSizeObj: ProductSize
                for (child in snapshot.children) {
                    val productSizeFromDB = child.getValue<ProductSize>()
                    productSizeFromDB?.let {
                        flag = productSize == it.size
                        productSizeObj = it
                    }
                    if (flag) break
                }
                if (!flag) {
                    productSizeObj = createSize(productSize = productSize)
                }
//                onComplete.invoke(productSizeObj)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}