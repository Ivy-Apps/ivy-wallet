package com.ivy.core.persistence.dao.exchange

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ivy.core.persistence.entity.exchange.ExchangeRateOverrideEntity
import com.ivy.data.DELETING
import kotlinx.coroutines.flow.Flow

@Dao
interface ExchangeRateOverrideDao {
    // region Save
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(values: List<ExchangeRateOverrideEntity>)
    // endregion

    // region Select
    @Query("SELECT * FROM exchange_rates_override WHERE sync != $DELETING")
    suspend fun findAllBlocking(): List<ExchangeRateOverrideEntity>

    @Query("SELECT * FROM exchange_rates_override WHERE baseCurrency = :baseCurrency")
    fun findAllByBaseCurrency(baseCurrency: String): Flow<List<ExchangeRateOverrideEntity>>
    // endregion
    @Query("DELETE FROM exchange_rates_override WHERE baseCurrency = :baseCurrency AND currency = :currency")
    suspend fun deleteByBaseCurrencyAndCurrency(
        baseCurrency: String,
        currency: String
    )

}