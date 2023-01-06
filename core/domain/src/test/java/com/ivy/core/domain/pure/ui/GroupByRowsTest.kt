package com.ivy.core.domain.pure.ui

import io.kotest.core.spec.style.FreeSpec
import io.kotest.data.row
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

class GroupByRowsTest : FreeSpec({
    "grouping by rows" - {
        withData(
            nameFn = { (items, itemsPerRow, expected) ->
                "$items - $itemsPerRow per row = $expected"
            },
            // Dataset (by) Items per row (results in) Grouped
            row(
                listOf(1, 2, 3, 4, 5, 6, 7), 3, listOf(
                    listOf(1, 2, 3),
                    listOf(4, 5, 6),
                    listOf(7),
                )
            ),
            row(
                listOf(1, 2, 3), 1, listOf(
                    listOf(1),
                    listOf(2),
                    listOf(3),
                )
            ),
            row(listOf(), 4, listOf()),
            row(
                (1..12).toList(), 4, listOf(
                    listOf(1, 2, 3, 4),
                    listOf(5, 6, 7, 8),
                    listOf(9, 10, 11, 12),
                )
            ),
            row(
                (1..12).toList(), 5, listOf(
                    listOf(1, 2, 3, 4, 5),
                    listOf(6, 7, 8, 9, 10),
                    listOf(11, 12),
                )
            ),
        ) { (items, itemsPerRow, expected) ->
            val res = groupByRows(items = items, itemsPerRow = itemsPerRow)

            res shouldBe expected
        }
    }
})