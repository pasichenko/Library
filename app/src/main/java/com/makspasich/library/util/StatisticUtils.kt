package com.makspasich.library.util

import com.makspasich.library.model.Product
import com.makspasich.library.model.State
import java.util.Calendar
import java.util.stream.Collectors


fun convertProductsToStatistic(products: List<Product>): Map<Int, Map<State, Long>> {
    val collect: MutableMap<Int, MutableMap<State, Long>> = products.stream()
        .collect(
            Collectors.groupingBy(
                { timestampToYear(it.timestamp) },
                Collectors.groupingBy({ it.state }, Collectors.counting())
            )
        )

    /* //TODO uncomment later
    collect.values.forEach { yearStat ->
        State.entries.forEach { state ->
            yearStat.putIfAbsent(state, 0L)
        }
    }*/
    return collect
}

private fun timestampToYear(timestamp: Long?): Int {
    val calendarTimestamp = Calendar.getInstance()
    calendarTimestamp.timeInMillis = timestamp ?: 0L
    val yearTimestamp = calendarTimestamp.get(Calendar.YEAR)
    return yearTimestamp
}