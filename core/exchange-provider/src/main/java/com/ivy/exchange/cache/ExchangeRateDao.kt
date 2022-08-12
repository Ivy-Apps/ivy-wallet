package com.ivy.exchange.cache

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ExchangeRateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(value: ExchangeRateEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(values: List<ExchangeRateEntity>)

    @Query("SELECT * FROM exchange_rates")
    suspend fun findAll(): List<ExchangeRateEntity>

    @Query("SELECT * FROM exchange_rates WHERE baseCurrency = :baseCurrency AND currency = :currency")
    suspend fun findByBaseCurrencyAndCurrency(
        baseCurrency: String,
        currency: String
    ): ExchangeRateEntity?

    @Query("DELETE FROM exchange_rates")
    suspend fun deleteALl()
}