package com.ivy.core.persistence.query

import androidx.sqlite.db.SimpleSQLiteQuery
import com.ivy.core.persistence.dao.trn.TrnDao
import com.ivy.core.persistence.entity.trn.TrnEntity
import javax.inject.Inject

class TrnQueryExecutor @Inject constructor(
    private val trnDao: TrnDao
) {
    suspend fun query(where: TrnWhere): List<TrnEntity> {
        val whereClause = toWhereClause(where)
        return trnDao.findBySQL(
            SimpleSQLiteQuery(
                "SELECT * FROM transactions WHERE ${whereClause.query}" +
                        " ORDER BY timeType DESC, time DESC",
                whereClause.args.toTypedArray()
            )
        )
    }
}