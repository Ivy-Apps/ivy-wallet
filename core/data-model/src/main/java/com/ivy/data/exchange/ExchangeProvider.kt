package com.ivy.data.exchange

enum class ExchangeProvider(val code: Int) {
    Coinbase(1);

    companion object {
        fun fromCode(code: Int): ExchangeProvider? =
            values().firstOrNull { it.code == code }
    }
}