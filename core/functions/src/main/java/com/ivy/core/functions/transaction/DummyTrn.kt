package com.ivy.core.functions.transaction

import com.ivy.common.timeNowLocal
import com.ivy.core.functions.account.dummyAcc
import com.ivy.core.functions.category.dummyCategory
import com.ivy.core.functions.sync.dummySync
import com.ivy.data.CurrencyCode
import com.ivy.data.SyncMetadata
import com.ivy.data.account.Account
import com.ivy.data.category.Category
import com.ivy.data.transaction.*
import java.time.LocalDateTime
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
    value = Value(
        amount = amount,
        currency = account.currency,
    ),
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

fun dummyValue(
    amount: Double = 0.0,
    currency: CurrencyCode = "USD"
): Value = Value(
    amount = amount,
    currency = currency,
)

fun dummyTransfer(
    toValue: Value = dummyValue(),
    toAccount: Account = dummyAcc()
): TransactionType.Transfer = TransactionType.Transfer(
    toValue = toValue,
    toAccount = toAccount,
)

fun dummyActual(
    time: LocalDateTime = timeNowLocal()
): TrnTime.Actual = TrnTime.Actual(time)


fun dummyDue(
    time: LocalDateTime = timeNowLocal()
): TrnTime.Due = TrnTime.Due(time)