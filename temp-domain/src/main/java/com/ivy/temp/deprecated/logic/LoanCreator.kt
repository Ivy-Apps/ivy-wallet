package com.ivy.wallet.domain.deprecated.logic

import androidx.compose.ui.graphics.toArgb
import com.ivy.data.loan.Loan
import com.ivy.wallet.domain.deprecated.logic.model.CreateLoanData
import com.ivy.wallet.domain.deprecated.sync.uploader.LoanUploader
import com.ivy.wallet.domain.pure.util.nextOrderNum
import com.ivy.wallet.io.persistence.dao.LoanDao
import com.ivy.wallet.io.persistence.data.toEntity
import com.ivy.wallet.utils.ioThread
import java.util.*
import javax.inject.Inject

class LoanCreator @Inject constructor(
    private val dao: LoanDao,
    private val uploader: LoanUploader
) {
    suspend fun create(
        data: CreateLoanData,
        onRefreshUI: suspend (Loan) -> Unit
    ): UUID? {
        val name = data.name
        if (name.isBlank()) return null
        if (data.amount <= 0) return null

        var loanId: UUID? = null

        try {

            val newItem = ioThread {
                val item = Loan(
                    name = name.trim(),
                    amount = data.amount,
                    type = data.type,
                    color = data.color.toArgb(),
                    icon = data.icon,
                    orderNum = dao.findMaxOrderNum().nextOrderNum(),
                    isSynced = false,
                    accountId = data.account?.id
                )
                loanId = item.id
                dao.save(item.toEntity())
                item
            }

            onRefreshUI(newItem)

            ioThread {
                uploader.sync(newItem)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return loanId
    }


    suspend fun edit(
        updatedItem: Loan,
        onRefreshUI: suspend (Loan) -> Unit
    ) {
        if (updatedItem.name.isBlank()) return
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

            ioThread {
                uploader.sync(updatedItem)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun delete(
        item: Loan,
        onRefreshUI: suspend () -> Unit
    ) {
        try {
            ioThread {
                dao.flagDeleted(item.id)
            }

            onRefreshUI()

            ioThread {
                uploader.delete(item.id)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}