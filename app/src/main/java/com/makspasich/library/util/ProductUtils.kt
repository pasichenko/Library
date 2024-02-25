package com.makspasich.library.util

import com.makspasich.library.model.Product

val PRODUCT_KEY_PATTERN = Regex("^\\{dd_(?<shortId>\\d+)\\}$")

fun Product.shortKey(): String {
    val regex = PRODUCT_KEY_PATTERN
    return regex.find(key)
        ?.let { it.groups["shortId"] }
        ?.value ?: "---"
}