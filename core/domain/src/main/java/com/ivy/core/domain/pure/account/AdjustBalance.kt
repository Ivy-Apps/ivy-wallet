package com.ivy.core.domain.pure.account

import com.ivy.common.time.provider.TimeProvider
import com.ivy.core.domain.pure.isFiat
import com.ivy.core.domain.pure.util.isInsignificant
import com.ivy.data.Sync
import com.ivy.data.SyncState
import com.ivy.data.Value
import com.ivy.data.account.Account
import com.ivy.data.transaction.*
import java.util.*
import kotlin.math.abs

fun adjustBalanceTrn(
    timeProvider: TimeProvider,
    account: Account,
    currentBalance: Double,
    desiredBalance: Double,
    hiddenTrn: Boolean,
): Transaction? {
    // if the acc has 50$ and we want to adjust it to 40$
    // => we need to create an Expense for $10
    val amountMissing = currentBalance - desiredBalance

    if (isFiat(account.currency) && isInsignificant(amountMissing)) {
        // Balance diff is insignificant less than 1 "penny" (0.01)
        // no need to adjust
        return null
    }

    return Transaction(
        id = UUID.randomUUID(),
        account = account,
        category = null, // unspecified
        type = if (amountMissing > 0) TransactionType.Expense else TransactionType.Income,
        value = Value(amount = abs(amountMissing), currency = account.currency),
        title = "Adjust balance",
        description = null,
        time = TrnTime.Actual(timeProvider.timeNow()),
        state = if (hiddenTrn) TrnState.Hidden else TrnState.Default,
        purpose = TrnPurpose.AdjustBalance,

        attachments = emptyList(),
        sync = Sync(
            state = SyncState.Syncing,
            lastUpdated = timeProvider.timeNow()
        ),
        tags = emptyList(),
        metadata = TrnMetadata(recurringRuleId = null, loanId = null, loanRecordId = null),
    )
}