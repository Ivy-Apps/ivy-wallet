package com.ivy.data.transaction

import java.util.*

data class TrnMetadata(
    /**
     * Links transaction with a recurring rule
     */
    val recurringRuleId: UUID?,

    /**
     * This refers to the loan id that is linked with a transaction
     */
    val loanId: UUID?,

    /**
     * This refers to the loan record id that is linked with a transaction
     */
    val loanRecordId: UUID?,
) {
    companion object {
        const val RECURRING_RULE_ID = "recurringRuleId"
        const val LOAN_ID = "loanId"
        const val LOAN_RECORD_ID = "loanRecordId"
    }
}