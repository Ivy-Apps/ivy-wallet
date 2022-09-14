package com.ivy.data.transaction

enum class TrnType(val code: Int) {
    Income(1), Expense(-1);

    companion object {
        fun fromCode(code: Int): TrnType? = values().firstOrNull { it.code == code }
    }
}