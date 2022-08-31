package com.ivy.reports.states

import androidx.compose.runtime.Immutable
import com.ivy.core.ui.temp.trash.TimePeriod
import com.ivy.data.account.Account
import com.ivy.data.category.Category
import com.ivy.data.transaction.TrnType
import com.ivy.reports.ImmutableItem

@Immutable
data class FilterState(
    val visible: Boolean,

    val selectedTrnTypes: ImmutableItem<List<TrnType>>,

    val period: ImmutableItem<TimePeriod?>,

    val allAccounts: ImmutableItem<List<Account>>,
    val selectedAcc: ImmutableItem<List<Account>>,

    val allCategories: ImmutableItem<List<Category>>,
    val selectedCat: ImmutableItem<List<Category>>,

    val minAmount: Double?,
    val maxAmount: Double?,

    val includeKeywords: ImmutableItem<List<String>>,
    val excludeKeywords: ImmutableItem<List<String>>,
)
