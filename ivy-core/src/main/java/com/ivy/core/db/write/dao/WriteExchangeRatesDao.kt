package com.ivy.core.db.write.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.ivy.core.db.entity.ExchangeRateEntity

@Dao
interface WriteExchangeRatesDao {
    @Upsert
    suspend fun save(value: ExchangeRateEntity)

    @Upsert
    suspend fun saveMany(value: List<ExchangeRateEntity>)

    @Query("DELETE FROM exchange_rates WHERE baseCurrency = :baseCurrency AND currency = :currency")
    suspend fun deleteByBaseCurrencyAndCurrency(
        baseCurrency: String,
        currency: String
    )

    @Query("DELETE FROM exchange_rates")
    suspend fun deleteALl()
}