package com.ivy.accounts.compute

import com.ivy.base.time.asInstant
import com.ivy.data.model.Account
import com.ivy.legacy.data.model.AccountData
import com.ivy.legacy.data.model.FromToTimeRange
import com.ivy.legacy.data.model.toCloseTimeRange
import com.ivy.wallet.domain.pure.data.ClosedTimeRange


fun ClosedTimeRange.toScopedTimeRange() : ScopedTimeRange {
    return ScopedTimeRange(
        from = this.from.asInstant(),
        to = this.to.asInstant()
    )
}

suspend fun BalanceComputeUseCase.computeForAccount(
    account: Account,
    range: FromToTimeRange
) : AccountData {
    val scopedTimeRange = range.toCloseTimeRange().toScopedTimeRange()
    val computeResult = this.compute(
        ComputeTypes.FromAccount(
            account = account,
            scopedTimeRange = scopedTimeRange
        )
    )

    return AccountData(
        account = account,
        monthlyIncome = computeResult.incomeInScopedTimeRange,
        monthlyExpenses = computeResult.expenseInScopedTimeRange,
        balance = computeResult.balance,
        balanceBaseCurrency = computeResult.balanceInBaseCurrency
    )
}