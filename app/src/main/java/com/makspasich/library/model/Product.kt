package com.makspasich.library.model

import com.makspasich.library.formatDate

data class Product(
    val key: String = "",
    val uid: String? = "",
    val name: String? = "",
    val timestamp: Long? = 0L,
    val expirationTimestamp: Long? = 0L,
    val size: String? = "0.0",
    val state: State = State.CREATED,
    val tags: Map<String, TagName> = hashMapOf()
){
    fun created(): String? {
        return timestamp?.formatDate("yyyy-MM-dd")
    }

    fun expiration(): String? {
        return expirationTimestamp?.formatDate("yyyy-MM-dd")
    }
}