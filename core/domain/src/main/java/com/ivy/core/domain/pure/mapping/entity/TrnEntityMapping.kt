package com.ivy.core.domain.pure.mapping.entity

import com.ivy.common.time.provider.TimeProvider
import com.ivy.common.time.time
import com.ivy.common.time.toUtc
import com.ivy.core.persistence.entity.trn.TrnEntity
import com.ivy.core.persistence.entity.trn.data.TrnTimeType
import com.ivy.data.transaction.Transaction
import com.ivy.data.transaction.TrnTime

fun mapToEntity(
    trn: Transaction,
    timeProvider: TimeProvider,
) = with(trn) {
    TrnEntity(
        id = id.toString(),
        accountId = account.id.toString(),
        type = type,
        state = state,
        sync = sync,
        purpose = purpose,
        currency = value.currency,
        amount = value.amount,
        categoryId = category?.id?.toString(),
        title = title,
        description = description,
        time = time.time().toUtc(timeProvider),
        timeType = if (time is TrnTime.Actual) TrnTimeType.Actual else TrnTimeType.Due,
    )
}