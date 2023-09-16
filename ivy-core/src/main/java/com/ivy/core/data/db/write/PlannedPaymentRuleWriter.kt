package com.ivy.core.data.db.write

import arrow.core.Either
import com.ivy.core.data.db.entity.PlannedPaymentRuleEntity
import com.ivy.core.data.db.write.dao.WritePlannedPaymentRuleDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PlannedPaymentRuleWriter @Inject constructor(
    private val dao: WritePlannedPaymentRuleDao,
) : DbWriter<PlannedPaymentRuleEntity> {
    override suspend fun save(value: PlannedPaymentRuleEntity): Either<String, Unit> {
        return withContext(Dispatchers.IO) {
            dao.save(value)
            Either.Right(Unit)
        }
    }

    override suspend fun saveMany(values: List<PlannedPaymentRuleEntity>): Either<String, Unit> {
        return withContext(Dispatchers.IO) {
            dao.saveMany(values)
            Either.Right(Unit)
        }
    }
}