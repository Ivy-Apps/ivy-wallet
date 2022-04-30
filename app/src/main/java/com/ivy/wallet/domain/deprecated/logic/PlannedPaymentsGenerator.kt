package com.ivy.wallet.domain.deprecated.logic

import com.ivy.wallet.domain.data.core.PlannedPaymentRule
import com.ivy.wallet.domain.data.core.Transaction
import com.ivy.wallet.io.persistence.dao.TransactionDao
import java.time.LocalDateTime

@Deprecated("Migrate to FP Style")
class PlannedPaymentsGenerator(
    private val transactionDao: TransactionDao
) {
    companion object {
        private const val GENERATED_INSTANCES_LIMIT = 72
    }

    suspend fun generate(rule: PlannedPaymentRule) {
        //delete all not happened transactions
        transactionDao.flagDeletedByRecurringRuleIdAndNoDateTime(
            recurringRuleId = rule.id
        )

        if (rule.oneTime) {
            generateOneTime(rule)
        } else {
            generateRecurring(rule)
        }
    }

    private suspend fun generateOneTime(rule: PlannedPaymentRule) {
        val trns = transactionDao.findAllByRecurringRuleId(recurringRuleId = rule.id)

        if (trns.isEmpty()) {
            generateTransaction(rule, rule.startDate!!)
        }
    }

    private suspend fun generateRecurring(rule: PlannedPaymentRule) {
        val startDate = rule.startDate!!
        val endDate = startDate.plusYears(3)

        val trns = transactionDao.findAllByRecurringRuleId(recurringRuleId = rule.id)
        var trnsToSkip = trns.size

        var generatedTransactions = 0

        var date = startDate
        while (date.isBefore(endDate)) {
            if (generatedTransactions >= GENERATED_INSTANCES_LIMIT) {
                break
            }

            if (trnsToSkip > 0) {
                //skip first N happened transactions
                trnsToSkip--
            } else {
                //generate transaction
                generateTransaction(
                    rule = rule,
                    dueDate = date
                )
                generatedTransactions++
            }

            val intervalN = rule.intervalN!!.toLong()
            date = rule.intervalType!!.incrementDate(
                date = date,
                intervalN = intervalN
            )
        }
    }

    private suspend fun generateTransaction(rule: PlannedPaymentRule, dueDate: LocalDateTime) {
        transactionDao.save(
            Transaction(
                type = rule.type,
                accountId = rule.accountId,
                recurringRuleId = rule.id,
                categoryId = rule.categoryId,
                amount = rule.amount.toBigDecimal(),
                title = rule.title,
                description = rule.description,
                dueDate = dueDate,
                dateTime = null,
                toAccountId = null,

                isSynced = false
            ).toEntity()
        )
    }

}