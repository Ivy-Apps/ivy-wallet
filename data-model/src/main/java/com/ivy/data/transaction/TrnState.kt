package com.ivy.data.transaction

enum class TrnState(val code: Int) {
    Default(1), Hidden(2);

    companion object {
        fun fromCode(code: Int): TrnState? = values().firstOrNull { it.code == code }
    }
}