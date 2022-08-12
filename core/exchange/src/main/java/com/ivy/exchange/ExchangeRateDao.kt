package com.ivy.exchange

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ExchangeRateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(value: com.ivy.exchange.ExchangeRateEntity)

    @Query("SELECT * FROM exchange_rates WHERE baseCurrency = :baseCurrency AND currency = :currency")
    suspend fun findByBaseCurrencyAndCurrency(
        baseCurrency: String,
        currency: String
    ): com.ivy.exchange.ExchangeRateEntity?

    @Query("DELETE FROM exchange_rates")
    suspend fun deleteALl()
}