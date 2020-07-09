package com.makspasich.library.source

import com.makspasich.library.models.Product
import com.makspasich.library.models.ProductName
import com.makspasich.library.models.ProductSize

interface ProductRepository {
    fun writeProduct(key: String, product: Product)
    fun moveToActive(product: Product)
    fun moveToArchive(product: Product)
    fun deleteProduct(product: Product)
    fun writeProductName(productName: String, onComplete: (ProductName) -> Unit)
    fun writeProductSize(productSize: ProductSize)
}