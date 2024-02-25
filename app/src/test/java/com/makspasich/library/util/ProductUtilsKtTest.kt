package com.makspasich.library.util

import com.makspasich.library.model.Product
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ProductUtilsKtTest {
    @Test
    fun shortKey() {
        val product = Product(key = "{dd_123}")
        val actual = product.shortKey()
        Assertions.assertEquals("123", actual)
    }
}