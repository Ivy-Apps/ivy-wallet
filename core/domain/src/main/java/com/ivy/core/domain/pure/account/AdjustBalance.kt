package com.ivy.core.domain.pure.account

import com.ivy.common.timeNowLocal
import com.ivy.core.domain.pure.isFiat
import com.ivy.data.SyncState
import com.ivy.data.Value
import com.ivy.data.account.Account
import com.ivy.data.transaction.*
import java.util.*
import kotlin.math.abs

fun adjustBalanceTran(
    account: Account,
    currentBalance: Double,
    desiredBalance: Double,
    hiddenTransactions: Boolean
): Transaction? {
    // if the acc has 50$ and we want it to have 40%
    // => we need to create an Expense for $10
    val adjustTrnAmount = currentBalance - desiredBalance

    if (!isFiat(account.currency) && abs(adjustTrnAmount) < 0.009) {
        // Note: Crypto cannot be considered insignificant in any case.
        // balance diff is insignificant, don't adjust
        return null
    }

    return Transaction(
        id = UUID.randomUUID(),
        account = account,
        category = null, // unspecified
        type = if (adjustTrnAmount > 0) TrnType.Income else TrnType.Expense,
        value = Value(amount = adjustTrnAmount, currency = account.currency),
        title = "Adjust balance",
        description = TODO(),
        time = TrnTime.Actual(timeNowLocal()),
        state = if (hiddenTransactions) TrnState.Hidden else TrnState.Default,
        purpose = TrnPurpose.AdjustBalance,

        attachments = emptyList(),
        sync = SyncState.Syncing,
        tags = emptyList(),
        metadata = TrnMetadata(recurringRuleId = null, loanId = null, loanRecordId = null),
    )
}