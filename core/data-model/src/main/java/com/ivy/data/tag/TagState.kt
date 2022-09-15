package com.ivy.data.tag

enum class TagState(val code: Int) {
    Default(1), Archived(2);

    companion object {
        fun fromCode(code: Int) = values().firstOrNull { it.code == code }
    }
}