package com.ivy.base

import com.ivy.base.Reorderable
import com.ivy.data.Account

data class AccountData(
    val account: Account,
    val balance: Double,
    val balanceBaseCurrency: Double?,
    val monthlyExpenses: Double,
    val monthlyIncome: Double
) : Reorderable {
    override fun getItemOrderNum() = account.orderNum

    override fun withNewOrderNum(newOrderNum: Double) = this.copy(
        account = account.copy(
            orderNum = newOrderNum
        )
    )
}