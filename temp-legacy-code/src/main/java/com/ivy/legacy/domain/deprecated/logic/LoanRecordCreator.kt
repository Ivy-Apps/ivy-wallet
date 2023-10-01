package com.ivy.wallet.domain.deprecated.logic

import com.ivy.legacy.datamodel.LoanRecord
import com.ivy.legacy.utils.ioThread
import com.ivy.data.db.dao.write.WriteLoanRecordDao
import com.ivy.wallet.domain.deprecated.logic.model.CreateLoanRecordData
import java.util.UUID
import javax.inject.Inject

class LoanRecordCreator @Inject constructor(
    private val loanRecordWriter: WriteLoanRecordDao,
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

                loanRecordWriter.save(item.toEntity())
                item
            }

            onRefreshUI(newItem!!)
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
                loanRecordWriter.save(
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
                loanRecordWriter.flagDeleted(item.id)
            }

            onRefreshUI()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
