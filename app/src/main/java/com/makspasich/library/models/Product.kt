package com.makspasich.library.models

data class Product(
    var key: String? = "",
    var uid: String? = "",
    var name: String? = "",
    var timestamp: Long? = 0L,
    var expirationTimestamp: Long? = 0L,
    var size: String? = "0.0",
    var state: State = State.CREATED,
    var tags: Map<String, TagName> = hashMapOf()
) {
}