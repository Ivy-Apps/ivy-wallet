package com.ivy.navigation.destinations

import com.ivy.navigation.destinations.debug.DebugGraph
import com.ivy.navigation.destinations.imports.ImportGraph
import com.ivy.navigation.destinations.main.Categories
import com.ivy.navigation.destinations.main.Main
import com.ivy.navigation.destinations.onboarding.OnboardingGraph
import com.ivy.navigation.destinations.settings.Settings
import com.ivy.navigation.destinations.transaction.*

object Destination {
    val onboarding = OnboardingGraph
    val import = ImportGraph

    val main = Main

    // region Transaction
    val transaction = Transaction
    val newTransaction = NewTransaction
    val transfer = Transfer
    val newTransfer = NewTransfer
    val accountTransactions = AccountTransactions
    val categoryTransactions = CategoryTransactions
    // endregion

    val categories = Categories
    val settings = Settings

    val debug = DebugGraph
}