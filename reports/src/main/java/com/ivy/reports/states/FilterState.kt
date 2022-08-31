package com.ivy.reports.states

import androidx.compose.runtime.Immutable
import com.ivy.core.ui.temp.trash.TimePeriod
import com.ivy.data.account.Account
import com.ivy.data.category.Category
import com.ivy.data.transaction.TrnType
import com.ivy.reports.ImmutableData

@Immutable
data class FilterState(
    val visible: Boolean,

    val selectedTrnTypes: ImmutableData<List<TrnType>>,

    val period: ImmutableData<TimePeriod?>,

    val allAccounts: ImmutableData<List<Account>>,
    val selectedAcc: ImmutableData<List<Account>>,

    val allCategories: ImmutableData<List<Category>>,
    val selectedCat: ImmutableData<List<Category>>,

    val minAmount: Double?,
    val maxAmount: Double?,

    val includeKeywords: ImmutableData<List<String>>,
    val excludeKeywords: ImmutableData<List<String>>,
)
