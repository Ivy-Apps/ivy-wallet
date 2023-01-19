package com.ivy.core.ui.algorithm.trnhistory

import android.content.Context
import com.ivy.common.time.deviceFormat
import com.ivy.common.time.provider.TimeProvider
import com.ivy.common.time.toLocal
import com.ivy.core.domain.pure.format.format
import com.ivy.core.persistence.algorithm.trnhistory.CalcHistoryTrnView
import com.ivy.core.persistence.entity.trn.data.TrnTimeType
import com.ivy.core.ui.R
import com.ivy.core.ui.algorithm.trnhistory.data.TransactionUi
import com.ivy.core.ui.data.CategoryUi
import com.ivy.core.ui.data.account.AccountUi
import com.ivy.core.ui.data.transaction.TrnTimeUi
import com.ivy.core.ui.time.formatNicely
import com.ivy.data.Value
import java.time.Instant
import java.time.LocalDateTime


fun parseTransactionUi(
    appContext: Context,
    trn: CalcHistoryTrnView,
    accounts: Map<String, AccountUi>,
    categories: Map<String, CategoryUi>,
    timeProvider: TimeProvider,
    timeNow: LocalDateTime,
): TransactionUi? {
    return TransactionUi(
        id = trn.id,
        value = format(Value(trn.amount, trn.currency), shortenFiat = false),
        type = trn.type,
        time = toTrnTimeUi(
            appContext = appContext,
            time = trn.time,
            timeType = trn.timeType,
            timeProvider = timeProvider,
            timeNow = timeNow,
        ),
        account = accounts[trn.accountId] ?: return null,
        category = trn.categoryId?.let { categories[it] },
        title = trn.title,
        description = trn.description,
    )
}

fun toTrnTimeUi(
    appContext: Context,
    time: Instant,
    timeType: TrnTimeType,
    timeProvider: TimeProvider,
    timeNow: LocalDateTime, // used for optimization purposes
): TrnTimeUi {
    fun formatDateTime(
        time: LocalDateTime,
    ): String = time.formatNicely(
        context = appContext,
        timeProvider = timeProvider,
        includeWeekDay = true
    )

    val trnDateTime = time.toLocal(timeProvider)

    return when (timeType) {
        TrnTimeType.Actual -> TrnTimeUi.Actual(
            actualDate = formatDateTime(trnDateTime).uppercase(),
            actualTime = trnDateTime.toLocalTime().deviceFormat(appContext),
        )
        TrnTimeType.Due -> TrnTimeUi.Due(
            dueOnDate = appContext.getString(
                R.string.due_on, formatDateTime(trnDateTime)
            ).uppercase(),
            dueOnTime = trnDateTime.toLocalTime().deviceFormat(appContext),
            upcoming = timeNow.isBefore(trnDateTime)
        )
    }
}
