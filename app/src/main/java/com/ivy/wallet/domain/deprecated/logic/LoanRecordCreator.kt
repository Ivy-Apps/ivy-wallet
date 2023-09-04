package com.ivy.wallet.domain.deprecated.logic

import com.ivy.wallet.domain.data.core.LoanRecord
import com.ivy.wallet.domain.deprecated.logic.model.CreateLoanRecordData
import com.ivy.wallet.io.persistence.dao.LoanRecordDao
import com.ivy.wallet.utils.ioThread
import java.util.UUID

@Deprecated("Use FP style, look into `domain.fp` package")
class LoanRecordCreator(
    private val paywallLogic: PaywallLogic,
    private val dao: LoanRecordDao,
) {
    suspend fun create(
        loanId: UUID,
        data: CreateLoanRecordData,
        onRefreshUI: suspend (LoanRecord) -> Unit
    ): UUID? {
        val note = data.note
        if (data.amount <= 0) return null

        try {
            var newItem: LoanRecord? = null
            paywallLogic.protectQuotaExceededWithPaywall {
                newItem = ioThread {
                    val item = LoanRecord(
                        loanId = loanId,
                        note = note?.trim(),
                        amount = data.amount,
                        dateTime = data.dateTime,
                        isSynced = false,
                        interest = data.interest,
                        accountId = data.account?.id,
                        convertedAmount = data.convertedAmount
                    )

                    dao.save(item.toEntity())
                    item
                }

                onRefreshUI(newItem!!)
            }
            return newItem?.id
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    suspend fun edit(
        updatedItem: LoanRecord,
        onRefreshUI: suspend (LoanRecord) -> Unit
    ) {
        if (updatedItem.amount <= 0.0) return

        try {
            ioThread {
                dao.save(
                    updatedItem.toEntity().copy(
                        isSynced = false
                    )
                )
            }

            onRefreshUI(updatedItem)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun delete(
        item: LoanRecord,
        onRefreshUI: suspend () -> Unit
    ) {
        try {
            ioThread {
                dao.flagDeleted(item.id)
            }

            onRefreshUI()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
