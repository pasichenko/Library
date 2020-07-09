package com.makspasich.library.models

data class ProductStatistic(
        var key: String? = null,
        var name: String? = null,
        var countActive: Int = 0,
        var countArchive: Int = 0
) {
}