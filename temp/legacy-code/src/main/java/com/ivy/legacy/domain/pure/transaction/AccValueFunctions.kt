package com.ivy.wallet.domain.pure.transaction

import com.ivy.data.model.Expense
import com.ivy.data.model.Income
import com.ivy.data.model.Transaction
import com.ivy.data.model.Transfer
import com.ivy.data.model.getAccountId
import com.ivy.data.model.getValue
import java.math.BigDecimal
import java.util.UUID

typealias AccountValueFunction = ValueFunction<UUID>

object AccountValueFunctions {
    fun balance(
        transaction: Transaction,
        accountId: UUID
    ): BigDecimal = with(transaction) {
        if (this.getAccountId() == accountId) {
            // Account's transactions
            when (this) {
                is Income -> getValue()
                is Expense -> getValue().negate()
                is Transfer -> {
                    if (this.toAccount.value != accountId) {
                        // transfer to another account
                        getValue().negate()
                    } else {
                        // transfer to self
                        toValue.amount.value.toBigDecimal().minus(getValue())
                    }
                }
            }
        } else if (this is Transfer) {
            // potential transfer to account?
            this.toAccount.value.takeIf { it == getAccountId() } ?: return BigDecimal.ZERO
            this.toValue.amount.value.toBigDecimal()
        } else {
            BigDecimal.ZERO
        }
    }

    fun income(
        transaction: Transaction,
        accountId: UUID
    ): BigDecimal = with(transaction) {
        if (this.getAccountId() == accountId && this is Income) {
            getValue()
        } else {
            BigDecimal.ZERO
        }
    }

    fun transferIncome(
        transaction: Transaction,
        accountId: UUID
    ): BigDecimal = with(transaction) {
        if (this.getAccountId() == accountId && this is Transfer) {
            this.toValue.amount.value.toBigDecimal()
        } else {
            BigDecimal.ZERO
        }
    }

    fun expense(
        transaction: Transaction,
        accountId: UUID
    ): BigDecimal = with(transaction) {
        if (this.getAccountId() == accountId && this is Expense) {
            getValue()
        } else {
            BigDecimal.ZERO
        }
    }

    fun transferExpense(
        transaction: Transaction,
        accountId: UUID
    ): BigDecimal = with(transaction) {
        if (this.getAccountId() == accountId && this is Transfer) {
            getValue()
        } else {
            BigDecimal.ZERO
        }
    }

    fun incomeCount(
        transaction: Transaction,
        accountId: UUID
    ): BigDecimal = with(transaction) {
        if (this.getAccountId() == accountId && this is Income) {
            BigDecimal.ONE
        } else {
            BigDecimal.ZERO
        }
    }

    fun expenseCount(
        transaction: Transaction,
        accountId: UUID
    ): BigDecimal = with(transaction) {
        if (this.getAccountId() == accountId && this is Expense) {
            BigDecimal.ONE
        } else {
            BigDecimal.ZERO
        }
    }
}
