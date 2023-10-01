package com.ivy.legacy.domain.deprecated.logic

import com.ivy.legacy.datamodel.Budget
import com.ivy.legacy.utils.ioThread
import com.ivy.data.db.dao.read.BudgetDao
import com.ivy.data.db.dao.write.WriteBudgetDao
import com.ivy.wallet.domain.deprecated.logic.model.CreateBudgetData
import com.ivy.wallet.domain.pure.util.nextOrderNum
import javax.inject.Inject

class BudgetCreator @Inject constructor(
    private val budgetDao: BudgetDao,
    private val budgetWriter: WriteBudgetDao,
) {
    suspend fun createBudget(
        data: CreateBudgetData,
        onRefreshUI: suspend (Budget) -> Unit
    ) {
        val name = data.name
        if (name.isBlank()) return
        if (data.amount <= 0) return

        try {
            val newBudget = ioThread {
                val budget = Budget(
                    name = name.trim(),
                    amount = data.amount,
                    categoryIdsSerialized = data.categoryIdsSerialized,
                    accountIdsSerialized = data.accountIdsSerialized,
                    orderId = budgetDao.findMaxOrderNum().nextOrderNum(),
                    isSynced = false
                )

                budgetWriter.save(budget.toEntity())
                budget
            }

            onRefreshUI(newBudget)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun editBudget(
        updatedBudget: Budget,
        onRefreshUI: suspend (Budget) -> Unit
    ) {
        if (updatedBudget.name.isBlank()) return
        if (updatedBudget.amount <= 0.0) return

        try {
            ioThread {
                budgetWriter.save(
                    updatedBudget.toEntity().copy(
                        isSynced = false
                    )
                )
            }

            onRefreshUI(updatedBudget)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun deleteBudget(
        budget: Budget,
        onRefreshUI: suspend () -> Unit
    ) {
        try {
            ioThread {
                budgetWriter.flagDeleted(budget.id)
            }

            onRefreshUI()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
