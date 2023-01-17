package com.ivy.core.persistence.algorithm.calc

import androidx.room.Dao
import androidx.room.Query
import com.ivy.core.persistence.entity.trn.data.ActualCode
import com.ivy.data.DELETING
import kotlinx.coroutines.flow.Flow
import java.time.Instant

@Dao
interface CalcTrnDao {
    @Query(
        "SELECT amount, currency, type, time FROM transactions WHERE" +
                " accountId = :accountId AND time > :timestamp AND timeType = $ActualCode" +
                " AND sync != $DELETING"
    )
    fun findActualByAccountAfter(
        accountId: String,
        timestamp: Instant
    ): Flow<List<CalcTrn>>

    @Query(
        "SELECT amount, currency, type, time FROM transactions WHERE" +
                " accountId = :accountId AND  timeType = $ActualCode" +
                " AND sync != $DELETING"
    )
    fun findAllActualByAccount(accountId: String): Flow<List<CalcTrn>>
}
