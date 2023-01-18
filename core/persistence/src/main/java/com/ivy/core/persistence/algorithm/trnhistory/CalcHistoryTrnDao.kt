package com.ivy.core.persistence.algorithm.trnhistory

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import java.time.Instant

@Dao
interface CalcHistoryTrnDao {
    /**
     * - hidden transactions must not appear in the history
     */
    @Query(
        "SELECT * FROM CalcHistoryTrnView WHERE time >= :from AND time <= :to"
    )
    fun findAllInPeriod(
        from: Instant,
        to: Instant
    ): Flow<List<CalcHistoryTrnView>>
}