package com.ivy.navigation.destinations

import com.ivy.navigation.destinations.debug.DebugGraph
import com.ivy.navigation.destinations.imports.ImportBackup
import com.ivy.navigation.destinations.imports.ImportGraph
import com.ivy.navigation.destinations.main.Accounts
import com.ivy.navigation.destinations.main.Categories
import com.ivy.navigation.destinations.main.Home
import com.ivy.navigation.destinations.main.MoreMenu
import com.ivy.navigation.destinations.onboarding.OnboardingGraph
import com.ivy.navigation.destinations.other.AddFrame
import com.ivy.navigation.destinations.other.ExchangeRate
import com.ivy.navigation.destinations.settings.Settings
import com.ivy.navigation.destinations.transaction.*

object Destination {
    val onboarding = OnboardingGraph
    val import = ImportGraph

    val importBackup = ImportBackup

    // region Main
    val categories = Categories
    val home = Home
    val moreMenu = MoreMenu
    val accounts = Accounts
    // endregion

    // region Transaction
    val transaction = Transaction
    val newTransaction = NewTransaction
    val transfer = Transfer
    val newTransfer = NewTransfer
    val accountTransactions = AccountTransactions
    val categoryTransactions = CategoryTransactions
    // endregion

    val settings = Settings

    val addFrame = AddFrame

    val exchangeRates = ExchangeRate

    val debug = DebugGraph
}