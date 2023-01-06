package com.ivy.data

import android.icu.util.Currency
import java.util.*

data class IvyCurrency(
    val code: CurrencyCode,
    val name: String,
    val isCrypto: Boolean
) {
    companion object {
        val CRYPTO = setOf(
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
            IvyCurrency(
                code = "SHIB",
                name = "Shiba Inu coin",
                isCrypto = true
            ),
            IvyCurrency(
                code = "LUNA",
                name = "Terra",
                isCrypto = true
            ),
            IvyCurrency(
                code = "AVAX",
                name = "Avalanche",
                isCrypto = true
            ),
            IvyCurrency(
                code = "MATIC",
                name = "Polygon",
                isCrypto = true
            ),
            IvyCurrency(
                code = "CRO",
                name = "Cronos",
                isCrypto = true
            ),
            IvyCurrency(
                code = "WBTC",
                name = "Wrapped Bitcoin",
                isCrypto = true
            ),
            IvyCurrency(
                code = "ALGO",
                name = "Algorand",
                isCrypto = true
            ),
            IvyCurrency(
                code = "XLM",
                name = "Stellar",
                isCrypto = true
            ),
            IvyCurrency(
                code = "MANA",
                name = "Decentraland",
                isCrypto = true
            ),
            IvyCurrency(
                code = "AXS",
                name = "Axie Infinity",
                isCrypto = true
            ),
            IvyCurrency(
                code = "DAI",
                name = "Dai",
                isCrypto = true
            ),
            IvyCurrency(
                code = "ICP",
                name = "Internet Computer",
                isCrypto = true
            ),
            IvyCurrency(
                code = "ATOM",
                name = "Cosmos",
                isCrypto = true
            ),
            IvyCurrency(
                code = "FIL",
                name = "Filecoin",
                isCrypto = true
            ),
            IvyCurrency(
                code = "ETC",
                name = "Ethereum Classic",
                isCrypto = true
            ),
            IvyCurrency(
                code = "DASH",
                name = "Dash",
                isCrypto = true
            ),
            IvyCurrency(
                code = "TRX",
                name = "Tron",
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

fun getDefaultFIATCurrency(): Currency =
    Currency.getInstance(Locale.getDefault()) ?: Currency.getInstance("USD")
    ?: Currency.getInstance("usd") ?: Currency.getAvailableCurrencies().firstOrNull()
    ?: Currency.getInstance("EUR")
