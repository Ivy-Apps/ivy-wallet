package com.ivy.temp.deprecated.logic.zip

import com.ivy.wallet.io.persistence.data.*

data class IvyWalletCompleteData(
    val accounts: List<AccountEntity> = emptyList(),
    val budgets: List<BudgetEntity> = emptyList(),
    val categories: List<CategoryEntity> = emptyList(),
    val loanRecords: List<LoanRecordEntity> = emptyList(),
    val loans: List<LoanEntity> = emptyList(),
    val plannedPaymentRules: List<PlannedPaymentRuleEntity> = emptyList(),
    val settings: List<SettingsEntity> = emptyList(),
    val transactions: List<TransactionEntity> = emptyList(),
    val sharedPrefs: HashMap<String, String> = HashMap()
)