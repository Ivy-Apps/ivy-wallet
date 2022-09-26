package com.ivy.navigation.destinations

import com.ivy.navigation.destinations.imports.ImportGraph
import com.ivy.navigation.destinations.onboarding.OnboardingGraph
import com.ivy.navigation.destinations.transaction.*

object Destination {
    val onboarding = OnboardingGraph
    val import = ImportGraph

    // region Transaction
    val transaction = Transaction
    val newTransaction = NewTransaction
    val transfer = Transfer
    val newTransfer = NewTransfer
    val accountTransactions = AccountTransactions
    val categoryTransactions = CategoryTransactions
    // endregion
}