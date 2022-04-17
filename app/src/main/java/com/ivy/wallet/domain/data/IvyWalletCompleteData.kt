package com.ivy.wallet.domain.data

import com.ivy.wallet.domain.data.entity.*

data class IvyWalletCompleteData(
    val accounts: List<Account> = emptyList(),
    val budgets: List<Budget> = emptyList(),
    val categories: List<Category> = emptyList(),
    val loanRecords: List<LoanRecord> = emptyList(),
    val loans: List<Loan> = emptyList(),
    val plannedPaymentRules: List<PlannedPaymentRule> = emptyList(),
    val settings: List<Settings> = emptyList(),
    val transactions: List<Transaction> = emptyList(),
    val sharedPrefs: HashMap<String, String> = HashMap()
)