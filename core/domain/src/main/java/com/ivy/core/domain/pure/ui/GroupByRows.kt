package com.ivy.core.domain.pure.ui

fun <T> groupByRows(
    items: List<T>,
    itemsPerRow: Int,
): List<List<T>> {
    val rows = mutableListOf<List<T>>()
    var row = mutableListOf<T>()
    for (icon in items) {
        row.add(icon)
        if (row.size == itemsPerRow) {
            // row finished => add it and start a new row
            rows.add(row)
            // row.clear() won't work because it clears the already added row
            row = mutableListOf()
        }
    }
    if (row.isNotEmpty()) {
        // add the last not finished row
        rows.add(row)
    }
    return rows
}
