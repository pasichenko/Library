package com.makspasich.library.model.service

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.dataObjects
import com.google.firebase.firestore.firestore
import com.makspasich.library.model.Product
import com.makspasich.library.model.TagName
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

private const val PRODUCTS = "products"
private const val LAST_INSERTED = "last-inserted"
private const val TAGS = "tags"

class StorageService(
    private val firestore: FirebaseFirestore = Firebase.firestore
) {
    fun getProducts(): Flow<List<Product>> =
        firestore.collection(PRODUCTS).dataObjects<Product>()

    fun getProducts(query: (Query) -> Query): Flow<List<Product>> =
        query(firestore.collection(PRODUCTS)).dataObjects<Product>()

    fun getProduct(productId: String): Flow<Product?> =
        firestore.collection(PRODUCTS).document(productId).dataObjects<Product>()

    fun getLastProduct(uid: String): Flow<Product?> =
        firestore.collection(LAST_INSERTED).document(uid).dataObjects<Product>()

    fun getTags(): Flow<List<TagName>> =
        firestore.collection(TAGS).dataObjects<TagName>()

    suspend fun update(product: Product) {
        firestore.collection(PRODUCTS).document(product.key).set(product).await()
    }

    suspend fun updateLastInserted(product: Product) {
        firestore.collection(LAST_INSERTED)
            .document(Firebase.auth.uid.orEmpty()).set(product).await()
    }

    suspend fun updateQrState(productId: String, state: String) {
        val hashMap = HashMap<String, Any>()
        hashMap["state"] = state
        firestore.collection("qr-codes").document(productId).update(hashMap).await()
    }

    suspend fun saveIfNotExists(tagName: String) {
        val empty = firestore.collection(TAGS).whereEqualTo("name", tagName).get().await().isEmpty
        if (empty) {
            val id = firestore.collection(TAGS).document().id
            firestore.collection(TAGS).document(id).set(TagName(id, tagName))
        }
    }
}