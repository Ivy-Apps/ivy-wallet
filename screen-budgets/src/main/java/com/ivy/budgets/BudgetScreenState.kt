package com.ivy.budgets

import com.ivy.budgets.model.DisplayBudget
import com.ivy.legacy.data.model.FromToTimeRange
import com.ivy.legacy.datamodel.Account
import com.ivy.legacy.datamodel.Category
import kotlinx.collections.immutable.ImmutableList
import javax.annotation.concurrent.Immutable

@Immutable
data class BudgetScreenState(
    val baseCurrency: String,
    val budgets: ImmutableList<DisplayBudget>,
    val categories: ImmutableList<Category>,
    val accounts: ImmutableList<Account>,
    val categoryBudgetsTotal: Double,
    val appBudgetMax: Double,
    val timeRange: FromToTimeRange?,
    val reorderModalVisible: Boolean,
    val budgetModalData: BudgetModalData?
)