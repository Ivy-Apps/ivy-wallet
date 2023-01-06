package com.ivy.core.persistence.query

import androidx.sqlite.db.SimpleSQLiteQuery
import com.ivy.common.time.provider.TimeProvider
import com.ivy.core.persistence.dao.trn.TrnDao
import com.ivy.core.persistence.entity.trn.TrnEntity
import javax.inject.Inject

class TrnQueryExecutor @Inject constructor(
    private val trnDao: TrnDao,
    private val timeProvider: TimeProvider
) {
    suspend fun query(where: TrnWhere): List<TrnEntity> {
        val whereClause = generateWhereClause(where, timeProvider = timeProvider)
        return trnDao.findBySQL(
            SimpleSQLiteQuery(
                "SELECT * FROM transactions WHERE ${whereClause.query}" +
                        " ORDER BY timeType DESC, time DESC",
                whereClause.args.toTypedArray()
            )
        )
    }
}