package com.ivy.wallet.domain.data

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.ivy.core.db.entity.AccountEntity
import com.ivy.core.db.entity.BudgetEntity
import com.ivy.core.db.entity.CategoryEntity
import com.ivy.core.db.entity.LoanEntity
import com.ivy.core.db.entity.LoanRecordEntity
import com.ivy.core.db.entity.PlannedPaymentRuleEntity
import com.ivy.core.db.entity.SettingsEntity
import com.ivy.core.db.entity.TransactionEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class IvyWalletCompleteData(
    @SerialName("accounts")
    val accounts: List<AccountEntity> = emptyList(),
    @SerialName("budgets")
    val budgets: List<BudgetEntity> = emptyList(),
    @SerialName("categories")
    val categories: List<CategoryEntity> = emptyList(),
    @SerialName("loanRecords")
    val loanRecords: List<LoanRecordEntity> = emptyList(),
    @SerialName("loans")
    val loans: List<LoanEntity> = emptyList(),
    @SerialName("plannedPaymentRules")
    val plannedPaymentRules: List<PlannedPaymentRuleEntity> = emptyList(),
    @SerialName("settings")
    val settings: List<SettingsEntity> = emptyList(),
    @SerialName("transactions")
    val transactions: List<TransactionEntity> = emptyList(),
    @SerialName("sharedPrefs")
    val sharedPrefs: HashMap<String, String> = HashMap()
)
