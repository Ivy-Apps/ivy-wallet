package com.ivy.data.exchange

@Deprecated("will be removed!")
enum class ExchangeProvider(val code: Int) {
    Old(1),
    Fawazahmed0(2);

    companion object {
        fun fromCode(code: Int): ExchangeProvider? =
            values().firstOrNull { it.code == code }
    }
}