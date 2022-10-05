package com.ivy.core.domain.pure.ui

fun <T> groupByRows(
    items: List<T>,
    iconsPerRow: Int,
): List<List<T>> {
    val rows = mutableListOf<List<T>>()
    var row = mutableListOf<T>()
    for (icon in items) {
        if (row.size < iconsPerRow) {
            // row not finished
            row.add(icon)
        } else {
            // row is finished, add it and start the next row
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
