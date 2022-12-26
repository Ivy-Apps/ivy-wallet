package com.ivy.data.category

enum class CategoryType(val code: Int) {
    Income(1), Expense(2), Both(3);

    companion object {
        fun fromCode(code: Int) = values().firstOrNull { it.code == code }
    }
}