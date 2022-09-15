package com.ivy.data.category

enum class CategoryState(val code: Int) {
    Default(1), Archived(2);

    companion object {
        fun fromCode(code: Int): CategoryState? = values().firstOrNull { it.code == code }
    }
}