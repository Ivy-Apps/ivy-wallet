package com.ivy.reports

import androidx.compose.runtime.Immutable
import com.ivy.core.ui.temp.trash.TimePeriod
import com.ivy.data.CurrencyCode
import com.ivy.data.account.Account
import com.ivy.data.category.Category
import com.ivy.data.transaction.TransactionOld
import com.ivy.data.transaction.TransactionsList
import com.ivy.data.transaction.TrnType
import com.ivy.reports.data.PlannedPaymentTypes
import java.util.*

//------------------------------------------  Reports Main State -----------------------------------

@Immutable
data class ReportState(
    val baseCurrency: CurrencyCode,
    val loading: Boolean,
    val headerState: HeaderState,
    val trnsList: ImmutableData<TransactionsList>,

    val filterState: FilterState,
)

//----------------------------------------  Reports Header State -----------------------------------

@Immutable
data class HeaderState(
    val balance: Double,

    val income: Double,
    val expenses: Double,

    val incomeTransactionsCount: Int,
    val expenseTransactionsCount: Int,

    val showTransfersAsIncExpCheckbox: Boolean,
    val treatTransfersAsIncExp: Boolean,

    // TODO(Reports): Need to remove the variables below,
    //  Kept for Reports -> PieChart Screen Compatibility
    val accountIdFilters: List<UUID>,
    val transactionsOld: List<TransactionOld>,
)

//----------------------------------------  Reports Filter State -----------------------------------

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

    val selectedPlannedPayments : ImmutableData<List<PlannedPaymentTypes>>
)