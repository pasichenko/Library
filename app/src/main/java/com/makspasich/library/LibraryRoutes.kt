package com.makspasich.library

sealed class NavigationRoute(var route: String) {
    object LoginScreen : NavigationRoute("login")
    object Products : NavigationRoute("products")
    object ProductDetails : NavigationRoute("product/{$PRODUCT_ID}") {
        fun routeWithArgs(productId: String): String {
            return route.replace("{$PRODUCT_ID}", productId)
        }
    }

    object ProductEdit : NavigationRoute("product/{$PRODUCT_ID}/edit") {
        fun routeWithArgs(productId: String): String {
            return route.replace("{$PRODUCT_ID}", productId)
        }
    }

}

const val PRODUCT_ID = "productId"