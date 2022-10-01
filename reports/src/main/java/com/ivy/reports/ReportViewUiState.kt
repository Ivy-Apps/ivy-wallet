package com.ivy.reports

import androidx.compose.runtime.Immutable
import com.ivy.core.ui.temp.trash.TimePeriod
import com.ivy.data.CurrencyCode
import com.ivy.data.transaction.TransactionOld
import com.ivy.data.transaction.TransactionsList
import com.ivy.data.transaction.TrnType
import com.ivy.reports.data.ReportPlannedPaymentType
import com.ivy.reports.data.SelectableAccount
import com.ivy.reports.data.SelectableReportsCategory
import com.ivy.reports.extensions.ImmutableData
import com.ivy.reports.template.TemplateDataHolder
import com.ivy.reports.template.ui.TemplateUiState
import java.util.*

//------------------------------------------  Reports Main State -----------------------------------

@Immutable
data class ReportUiState(
    val baseCurrency: CurrencyCode,
    val loading: Boolean,
    val headerUiState: ImmutableData<HeaderUiState>,
    val trnsList: ImmutableData<TransactionsList>,

    val filterVisible: Boolean,
    val filterUiState: FilterUiState,

    val templateVisible: Boolean=false,
    val templateSaveModalVisible :Boolean = false,
    val selectedTemplateUiState: TemplateUiState? = null,

    val templateDataHolder: TemplateDataHolder
)

//----------------------------------------  Reports Header State -----------------------------------

@Immutable
data class HeaderUiState(
    val balance: Double,

    val income: Double,
    val expenses: Double,

    val incomeTransactionsCount: Int,
    val expenseTransactionsCount: Int,

    // TODO(Reports): Need to remove the variables below,
    //  Kept for Reports -> PieChart Screen Compatibility
    val accountIdFilters: ImmutableData<List<UUID>>,
    val transactionsOld: ImmutableData<List<TransactionOld>>,
    val treatTransfersAsIncExp: Boolean
)

//----------------------------------------  Reports Filter State -----------------------------------

@Immutable
data class FilterUiState(
    val selectedTrnTypes: ImmutableData<List<TrnType>>,

    val period: ImmutableData<TimePeriod?>,

    val selectedAcc: ImmutableData<List<SelectableAccount>>,

    val selectedCat: ImmutableData<List<SelectableReportsCategory>>,

    val minAmount: Double?,
    val maxAmount: Double?,

    val includeKeywords: ImmutableData<List<String>>,
    val excludeKeywords: ImmutableData<List<String>>,

    val selectedPlannedPayments: ImmutableData<List<ReportPlannedPaymentType>>,

    val treatTransfersAsIncExp : Boolean,

    val showSaveTemplateOption : Boolean = false
)