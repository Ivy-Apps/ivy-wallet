package com.ivy.core.data.db.write

import arrow.core.Either
import com.ivy.core.data.db.entity.PlannedPaymentRuleEntity
import com.ivy.core.data.db.write.dao.WritePlannedPaymentRuleDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class PlannedPaymentRuleWriter @Inject constructor(
    private val dao: WritePlannedPaymentRuleDao,
) {
    suspend fun save(value: PlannedPaymentRuleEntity): Either<String, Unit> {
        return withContext(Dispatchers.IO) {
            dao.save(value)
            Either.Right(Unit)
        }
    }

    suspend fun saveMany(values: List<PlannedPaymentRuleEntity>): Either<String, Unit> {
        return withContext(Dispatchers.IO) {
            dao.saveMany(values)
            Either.Right(Unit)
        }
    }

    suspend fun flagDeleted(id: UUID) {
        withContext(Dispatchers.IO) {
            dao.flagDeleted(id)
        }
    }
}