package com.ivy.core.persistence.dao.exchange

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ivy.core.persistence.entity.exchange.ExchangeRateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExchangeRateDao {
    // region Save
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(values: List<ExchangeRateEntity>)
    // endregion

    // region Select
    @Query("SELECT * FROM exchange_rates WHERE baseCurrency = :baseCurrency")
    fun findAllByBaseCurrency(baseCurrency: String): Flow<List<ExchangeRateEntity>>
    // endregion
}