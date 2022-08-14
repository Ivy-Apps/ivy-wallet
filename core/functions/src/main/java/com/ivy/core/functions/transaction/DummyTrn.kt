package com.ivy.core.functions.transaction

import com.ivy.common.timeNowLocal
import com.ivy.core.functions.account.dummyAcc
import com.ivy.core.functions.category.dummyCategory
import com.ivy.core.functions.sync.dummySync
import com.ivy.data.SyncMetadata
import com.ivy.data.account.Account
import com.ivy.data.category.Category
import com.ivy.data.transaction.Transaction
import com.ivy.data.transaction.TransactionType
import com.ivy.data.transaction.TrnMetadata
import com.ivy.data.transaction.TrnTime
import java.util.*

fun dummyTrn(
    id: UUID = UUID.randomUUID(),
    account: Account = dummyAcc(),
    type: TransactionType = TransactionType.Income,
    amount: Double = 0.0,
    category: Category? = dummyCategory(),
    time: TrnTime = TrnTime.Actual(timeNowLocal()),
    title: String? = "Dummy trn",
    description: String? = null,
    attachmentUrl: String? = null,
    metadata: TrnMetadata = dummyTrnMetadata(),
): Transaction = Transaction(
    id = id,
    account = account,
    type = type,
    amount = amount,
    category = category,
    time = time,
    title = title,
    description = description,
    attachmentUrl = attachmentUrl,
    metadata = metadata
)

fun dummyTrnMetadata(
    recurringRuleId: UUID? = null,
    loanId: UUID? = null,
    loanRecordId: UUID? = null,
    sync: SyncMetadata = dummySync(),
): TrnMetadata = TrnMetadata(
    recurringRuleId = recurringRuleId,
    loanId = loanId,
    loanRecordId = loanRecordId,
    sync = sync
)