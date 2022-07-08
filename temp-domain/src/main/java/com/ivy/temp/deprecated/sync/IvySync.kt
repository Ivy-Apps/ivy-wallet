package com.ivy.wallet.domain.deprecated.sync

import com.ivy.wallet.domain.deprecated.sync.item.*
import com.ivy.wallet.io.network.IvySession

class IvySync(
    private val accountSync: AccountSync,
    private val categorySync: CategorySync,
    private val budgetSync: BudgetSync,
    private val transactionSync: TransactionSync,
    private val plannedPaymentSync: PlannedPaymentSync,
    private val loanSync: LoanSync,
    private val loanRecordSync: LoanRecordSync,
    private val ivySession: IvySession
) {
    suspend fun isSynced(): Boolean {
        return accountSync.isSynced() &&
                categorySync.isSynced() &&
                transactionSync.isSynced() &&
                plannedPaymentSync.isSynced() &&
                budgetSync.isSynced() &&
                loanSync.isSynced() &&
                loanRecordSync.isSynced()
    }

    suspend fun sync() {
        if (ivySession.isLoggedIn()) {
            accountSync.sync()
            categorySync.sync()
            transactionSync.sync()
            plannedPaymentSync.sync()
            budgetSync.sync()
            loanSync.sync()
            loanRecordSync.sync()
        }
    }

    suspend fun syncCategories() {
        categorySync.sync()
    }

    suspend fun syncAccounts() {
        accountSync.sync()
    }
}