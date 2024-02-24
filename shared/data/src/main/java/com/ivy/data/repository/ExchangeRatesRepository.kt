package com.ivy.data.repository

interface ExchangeRatesRepository {

    fun fetchExchangeRates()

    fun saveExchangeRates()

    fun readExchangeRates()
}