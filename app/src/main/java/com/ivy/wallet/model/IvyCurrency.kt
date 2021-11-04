package com.ivy.wallet.model

import android.icu.util.Currency
import com.ivy.wallet.base.getDefaultFIATCurrency

data class IvyCurrency(
    val code: String,
    val name: String,
    val isCrypto: Boolean
) {
    companion object {
        val CRYPTO = listOf(
            IvyCurrency(
                code = "BTC",
                name = "Bitcoin",
                isCrypto = true
            ),
            IvyCurrency(
                code = "ETH",
                name = "Ethereum",
                isCrypto = true
            ),
            IvyCurrency(
                code = "USDT",
                name = "Tether USD",
                isCrypto = true
            ),
            IvyCurrency(
                code = "BNB",
                name = "Binance Coin",
                isCrypto = true
            ),
            IvyCurrency(
                code = "ADA",
                name = "Cardano",
                isCrypto = true
            ),
            IvyCurrency(
                code = "XRP",
                name = "Ripple",
                isCrypto = true
            ),
            IvyCurrency(
                code = "DOGE",
                name = "Dogecoin",
                isCrypto = true
            ),
            IvyCurrency(
                code = "USDC",
                name = "USD Coin",
                isCrypto = true
            ),
            IvyCurrency(
                code = "DOT",
                name = "Polkadot",
                isCrypto = true
            ),
            IvyCurrency(
                code = "UNI",
                name = "Uniswap",
                isCrypto = true
            ),
            IvyCurrency(
                code = "BUSD",
                name = "Binance USD",
                isCrypto = true
            ),
            IvyCurrency(
                code = "BCH",
                name = "Bitcoin Cash",
                isCrypto = true
            ),
            IvyCurrency(
                code = "SOL",
                name = "Solana",
                isCrypto = true
            ),
            IvyCurrency(
                code = "LTC",
                name = "Litecoin",
                isCrypto = true
            ),
            IvyCurrency(
                code = "LINK",
                name = "ChainLink Token",
                isCrypto = true
            ),
        )

        fun getAvailable(): List<IvyCurrency> {
            return Currency.getAvailableCurrencies()
                .map {
                    IvyCurrency(
                        code = it.currencyCode,
                        name = it.displayName,
                        isCrypto = false
                    )
                }
                .plus(CRYPTO)
        }

        fun fromCode(code: String): IvyCurrency? {
            if (code.isBlank()) return null

            val crypto = CRYPTO.find { it.code == code }
            if (crypto != null) {
                return crypto
            }

            return try {
                val fiat = Currency.getInstance(code)
                IvyCurrency(
                    fiatCurrency = fiat
                )
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        fun getDefault(): IvyCurrency = IvyCurrency(
            fiatCurrency = getDefaultFIATCurrency()
        )
    }

    constructor(fiatCurrency: Currency) : this(
        code = fiatCurrency.currencyCode,
        name = fiatCurrency.displayName,
        isCrypto = false
    )
}