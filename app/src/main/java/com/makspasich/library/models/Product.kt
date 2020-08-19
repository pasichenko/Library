package com.makspasich.library.models

data class Product(
        var key: String? = null,
        var uid: String? = null,
        var name: String? = null,
        var year: String? = null,
        var nameObj: ProductName? = null,
        var size: String? = null,
        var month: String? = null,
        var expirationDate: String? = null,
        var isActive: Boolean = true
) {
}