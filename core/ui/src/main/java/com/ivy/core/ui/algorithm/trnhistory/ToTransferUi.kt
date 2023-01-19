package com.ivy.core.ui.algorithm.trnhistory

import android.content.Context
import com.ivy.common.time.provider.TimeProvider
import com.ivy.core.domain.pure.format.format
import com.ivy.core.persistence.algorithm.trnhistory.CalcHistoryTrnView
import com.ivy.core.ui.algorithm.trnhistory.data.TransferUi
import com.ivy.core.ui.data.CategoryUi
import com.ivy.core.ui.data.account.AccountUi
import com.ivy.data.Value
import com.ivy.data.transaction.TrnPurpose
import java.time.LocalDateTime

fun parseTransfer(
    appContext: Context,
    batchId: String,
    batch: List<CalcHistoryTrnView>,
    accounts: Map<String, AccountUi>,
    categories: Map<String, CategoryUi>,
    timeProvider: TimeProvider,
    timeNow: LocalDateTime,
): TransferUi? {
    val from = batch.firstOrNull { it.purpose == TrnPurpose.TransferFrom } ?: return null
    val to = batch.firstOrNull { it.purpose == TrnPurpose.TransferTo } ?: return null
    val fee = batch.firstOrNull { it.purpose == TrnPurpose.Fee }

    return TransferUi(
        batchId = batchId,
        fromAmount = format(Value(from.amount, from.currency), shortenFiat = false),
        fromAccount = accounts[from.accountId] ?: return null,
        toAmount = format(Value(to.amount, to.currency), shortenFiat = false),
        toAccount = accounts[to.accountId] ?: return null,
        fee = fee?.let { format(Value(it.amount, it.currency), shortenFiat = false) },
        time = toTrnTimeUi(
            appContext = appContext,
            time = from.time,
            timeType = from.timeType,
            timeProvider = timeProvider,
            timeNow = timeNow,
        ),
        category = from.categoryId?.let { categories[it] },
        title = from.title,
        description = from.description,
    )
}