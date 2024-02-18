package com.makspasich.library.model.service

import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.dataObjects
import com.google.firebase.firestore.firestore
import com.makspasich.library.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

private const val PRODUCTS = "products"

class StorageService(
    private val firestore: FirebaseFirestore = Firebase.firestore
) {
    fun getProduct(productKey: String): Flow<Product?> =
        firestore.collection(PRODUCTS).document(productKey).dataObjects<Product>()

    suspend fun update(product: Product) {
        firestore.collection("products").document(product.key).set(product).await()
    }
}