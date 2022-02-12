package com.ivy.wallet.logic

import androidx.compose.ui.graphics.toArgb
import com.ivy.wallet.base.ioThread
import com.ivy.wallet.logic.model.CreateLoanData
import com.ivy.wallet.model.entity.Loan
import com.ivy.wallet.persistence.dao.LoanDao
import com.ivy.wallet.sync.uploader.LoanUploader
import java.util.*

class LoanCreator(
    private val paywallLogic: PaywallLogic,
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
            paywallLogic.protectAddWithPaywall(
                addLoan = true
            ) {
                val newItem = ioThread {
                    val item = Loan(
                        name = name.trim(),
                        amount = data.amount,
                        type = data.type,
                        color = data.color.toArgb(),
                        icon = data.icon,
                        orderNum = dao.findMaxOrderNum() + 1,
                        isSynced = false,
                        accountId = data.account?.id
                    )
                    loanId = item.id
                    dao.save(item)
                    item
                }

                onRefreshUI(newItem)

                ioThread {
                    uploader.sync(newItem)
                }
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
                    updatedItem.copy(
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