package com.ivy.legacy.domain.pure.transaction

import com.ivy.data.model.Expense
import com.ivy.data.model.Income
import com.ivy.data.model.Transaction
import com.ivy.data.model.Transfer
import com.ivy.data.temp.migration.getAccountId
import com.ivy.data.temp.migration.getValue
import java.math.BigDecimal
import java.util.UUID

object AccountValueFunctions {
    fun balance(
        transaction: Transaction,
        accountId: UUID
    ): BigDecimal = with(transaction) {
        when (this) {
            is Transfer -> handleTransfer(accountId)
            is Expense -> getValue().negate()
            is Income -> getValue()
        }
    }

    private fun Transfer.handleTransfer(accountId: UUID): BigDecimal {
        return if (this.getAccountId() == accountId) {
            if (this.toAccount.value != accountId) {
                // transfer to another account
                getValue().negate()
            } else {
                // transfer to self
                toValue.amount.value.toBigDecimal().minus(getValue())
            }
        } else {
            // potential transfer to account?
            this.toAccount.value.takeIf { it == accountId } ?: return BigDecimal.ZERO
            this.toValue.amount.value.toBigDecimal()
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
        if (this is Transfer && this.toAccount.value == accountId) {
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
}