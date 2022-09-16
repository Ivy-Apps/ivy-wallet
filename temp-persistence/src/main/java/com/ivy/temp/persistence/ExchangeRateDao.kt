package com.ivy.temp.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Deprecated("old")
@Dao
interface ExchangeRateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(value: ExchangeRateEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(values: List<ExchangeRateEntity>)

    @Query("SELECT * FROM exchange_rates")
    suspend fun findAllSuspend(): List<ExchangeRateEntity>

    @Query("SELECT * FROM exchange_rates")
    fun findAll(): Flow<List<ExchangeRateEntity>>

    @Query("SELECT * FROM exchange_rates WHERE baseCurrency = :baseCurrency AND currency = :currency")
    suspend fun findByBaseCurrencyAndCurrency(
        baseCurrency: String,
        currency: String
    ): ExchangeRateEntity?

    @Query("DELETE FROM exchange_rates")
    suspend fun deleteALl()
}