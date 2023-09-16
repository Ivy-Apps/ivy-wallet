package com.ivy.core.data.model

enum class NumpadType(val layout: Array<String>) {
    Calc(
        arrayOf(
            "7", "8", "9",
            "4", "5", "6",
            "1", "2", "3",
            "0"
        )
    ),
    Dialer(
        arrayOf(
            "1", "2", "3",
            "4", "5", "6",
            "7", "8", "9",
            "0"
        )
    ),
}
