package com.ivy.core.persistence.query

import androidx.sqlite.db.SimpleSQLiteQuery
import com.ivy.common.time.provider.TimeProvider
import com.ivy.core.persistence.dao.trn.TransactionDao
import com.ivy.core.persistence.entity.trn.TransactionEntity
import javax.inject.Inject

class TrnQueryExecutor @Inject constructor(
    private val transactionDao: TransactionDao,
    private val timeProvider: TimeProvider
) {
    suspend fun query(where: TrnWhere): List<TransactionEntity> {
        val whereClause = generateWhereClause(where, timeProvider = timeProvider)
        return transactionDao.findBySQL(
            SimpleSQLiteQuery(
                "SELECT * FROM transactions WHERE ${whereClause.query}" +
                        " ORDER BY timeType DESC, time DESC",
                whereClause.args.toTypedArray()
            )
        )
    }
}