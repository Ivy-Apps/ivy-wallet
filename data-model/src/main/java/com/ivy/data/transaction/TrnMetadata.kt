package com.ivy.data.transaction

import com.ivy.data.SyncMetadata
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

    val sync: SyncMetadata,
)