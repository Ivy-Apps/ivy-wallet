package com.ivy.data.repository

import com.ivy.data.model.primitive.AssetCode

interface CurrencyRepository {
    suspend fun getBaseCurrency(): AssetCode
    suspend fun setBaseBaseCurrency(newCurrency: AssetCode)
}