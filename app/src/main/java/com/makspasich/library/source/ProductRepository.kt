package com.makspasich.library.source

import com.makspasich.library.models.Product
import com.makspasich.library.models.ProductName

interface ProductRepository {
    fun addProduct(key: String, product: Product)
    fun updateProduct(key: String, oldProduct: Product, newProduct: Product)
    fun moveToActive(product: Product)
    fun moveToArchive(product: Product)
    fun deleteProduct(product: Product)
    fun writeProductName(productName: String, onComplete: (ProductName) -> Unit)
    fun writeProductSize(productSize: String)
}