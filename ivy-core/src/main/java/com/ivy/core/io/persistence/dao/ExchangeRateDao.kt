package com.ivy.wallet.io.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ivy.wallet.io.persistence.data.ExchangeRateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExchangeRateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(value: ExchangeRateEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAll(value: List<ExchangeRateEntity>)

    @Query("SELECT * FROM exchange_rates")
    fun findAll(): Flow<List<ExchangeRateEntity>>

    @Query("SELECT * FROM exchange_rates WHERE baseCurrency = :baseCurrency AND currency = :currency")
    suspend fun findByBaseCurrencyAndCurrency(
        baseCurrency: String,
        currency: String
    ): ExchangeRateEntity?

    @Query("DELETE FROM exchange_rates WHERE baseCurrency = :baseCurrency AND currency = :currency")
    suspend fun deleteByBaseCurrencyAndCurrency(
        baseCurrency: String,
        currency: String
    )

    @Query("DELETE FROM exchange_rates")
    suspend fun deleteALl()
}
