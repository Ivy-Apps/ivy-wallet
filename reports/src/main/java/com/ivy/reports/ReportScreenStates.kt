package com.ivy.reports

import androidx.compose.runtime.Immutable
import com.ivy.core.ui.temp.trash.TimePeriod
import com.ivy.data.CurrencyCode
import com.ivy.data.transaction.TransactionOld
import com.ivy.data.transaction.TransactionsList
import com.ivy.data.transaction.TrnType
import com.ivy.reports.data.ReportPlannedPaymenttType
import com.ivy.reports.data.SelectableAccount
import com.ivy.reports.data.SelectableReportsCategory
import java.util.*

//------------------------------------------  Reports Main State -----------------------------------

@Immutable
data class ReportState(
    val baseCurrency: CurrencyCode,
    val loading: Boolean,
    val headerState: ImmutableData<HeaderState>,
    val trnsList: ImmutableData<TransactionsList>,

    val filterVisible: Boolean,
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
    val accountIdFilters: ImmutableData<List<UUID>>,
    val transactionsOld: ImmutableData<List<TransactionOld>>,
)

//----------------------------------------  Reports Filter State -----------------------------------

@Immutable
data class FilterState(
    val visible: Boolean = false,

    val selectedTrnTypes: ImmutableData<List<TrnType>>,

    val period: ImmutableData<TimePeriod?>,

    val selectedAcc: ImmutableData<List<SelectableAccount>>,

    val selectedCat: ImmutableData<List<SelectableReportsCategory>>,

    val minAmount: Double?,
    val maxAmount: Double?,

    val includeKeywords: ImmutableData<List<String>>,
    val excludeKeywords: ImmutableData<List<String>>,

    val selectedPlannedPayments: ImmutableData<List<ReportPlannedPaymenttType>>
)