package com.ivy.wallet.domain.data

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.ivy.wallet.io.persistence.data.*

@Keep
data class IvyWalletCompleteData(
    @SerializedName("accounts")
    val accounts: List<AccountEntity> = emptyList(),
    @SerializedName("budgets")
    val budgets: List<BudgetEntity> = emptyList(),
    @SerializedName("categories")
    val categories: List<CategoryEntity> = emptyList(),
    @SerializedName("loanRecords")
    val loanRecords: List<LoanRecordEntity> = emptyList(),
    @SerializedName("loans")
    val loans: List<LoanEntity> = emptyList(),
    @SerializedName("plannedPaymentRules")
    val plannedPaymentRules: List<PlannedPaymentRuleEntity> = emptyList(),
    @SerializedName("settings")
    val settings: List<SettingsEntity> = emptyList(),
    @SerializedName("transactions")
    val transactions: List<TransactionEntity> = emptyList(),
    @SerializedName("sharedPrefs")
    val sharedPrefs: HashMap<String, String> = HashMap()
)
