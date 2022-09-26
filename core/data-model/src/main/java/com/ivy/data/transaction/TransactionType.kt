package com.ivy.data.transaction

enum class TransactionType(val code: Int) {
    Income(1), Expense(-1);

    companion object {
        fun fromCode(code: Int): TransactionType? = values().firstOrNull { it.code == code }
    }
}