package com.ivy.base

enum class SortOrder(val orderNum: Int, val displayName: String) {
    DEFAULT(0, "Default"),
    BALANCE_AMOUNT(1, "Balance Amount"),
    EXPENSES(2, "Expenses"),
    ALPHABETICAL(3, "Alphabetical");

    companion object {
        fun from(orderNum: Int): SortOrder {
            return values().firstOrNull { it.orderNum == orderNum } ?: DEFAULT
        }
    }
}