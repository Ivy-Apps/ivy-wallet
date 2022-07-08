package com.ivy.wallet.ui.analytics.tab

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.base.IvyWalletPreview
import com.ivy.wallet.ui.analytics.DataCircle
import com.ivy.wallet.ui.analytics.model.KPIs
import com.ivy.wallet.ui.theme.*

@Composable
fun ColumnScope.KPIsTab(
    kpis: KPIs,
    activeUsersCount: Int,
    onboardingCompletedEventCount: Int,
    onboardingStartedEventCount: Int,
) {
    Row(
        modifier = Modifier
            .weight(1f)
            .horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(Modifier.width(32.dp))

        DataCircle(
            count = kpis.onboardedUsers,
            metric = "Have 1 account\non server",
            circleColor = Ivy,
            parentCount = null
        )

        Spacer(Modifier.width(32.dp))

        AccountCategoryTrnPlanned(
            kpis = kpis
        )

        Spacer(Modifier.width(32.dp))

        DataCircle(
            count = onboardingStartedEventCount,
            metric = "Onboarding\nStarted",
            circleColor = Blue,
            parentCount = null
        )

        Spacer(Modifier.width(32.dp))

        DataCircle(
            count = onboardingCompletedEventCount,
            metric = "Onboarding\nCompleted",
            circleColor = BlueDark,
            parentCount = onboardingStartedEventCount
        )

        Spacer(Modifier.width(32.dp))

        val totalPaywallHits = kpis.paywallAccounts + kpis.paywallCategories +
                kpis.paywallNoReason + kpis.paywallExportCSV + kpis.paywallPremiumColor
        DataCircle(
            count = totalPaywallHits,
            metric = "Paywall hits",
            circleColor = Orange,
            parentCount = onboardingCompletedEventCount
        )

        Spacer(Modifier.width(32.dp))

        Paywall(
            kpis = kpis,
            onboardingCompletedEventCount = onboardingCompletedEventCount
        )

        Spacer(Modifier.width(32.dp))

        DataCircle(
            count = kpis.choosePlan,
            metric = "Choose plan",
            circleColor = IvyLight,
            parentCount = totalPaywallHits
        )

        Spacer(Modifier.width(32.dp))

        ChoosePlanOptions(kpis = kpis)

        Spacer(Modifier.width(32.dp))

        DataCircle(
            count = kpis.startBuyPremium,
            metric = "Buy Prompt",
            circleColor = IvyLight,
            parentCount = kpis.choosePlan
        )

        Spacer(Modifier.width(32.dp))

        BuyPlanOptions(kpis = kpis)

        Spacer(Modifier.width(32.dp))

        DataCircle(
            count = kpis.activePremium,
            metric = "Active Premium",
            circleColor = IvyDark,
            parentCount = kpis.startBuyPremium
        )

        Spacer(Modifier.width(32.dp))
    }
}

@Composable
private fun AccountCategoryTrnPlanned(kpis: KPIs) {
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(32.dp))

        DataCircle(
            count = kpis.usersCreatedAccount,
            metric = "Have at least\n2 accounts",
            circleColor = Green,
            parentCount = kpis.onboardedUsers,
            avgCount = kpis.avgAccountsPerUser
        )

        Spacer(Modifier.height(32.dp))

        DataCircle(
            count = kpis.usersCreatedCategory,
            metric = "Created category",
            circleColor = Orange,
            parentCount = kpis.onboardedUsers,
            avgCount = kpis.avgCategoriesPerUser
        )

        Spacer(Modifier.height(32.dp))

        DataCircle(
            count = kpis.usersCreatedTransaction,
            metric = "Created transaction",
            circleColor = IvyDark,
            parentCount = kpis.onboardedUsers,
            avgCount = kpis.avgTransactionsPerUser
        )

        Spacer(Modifier.height(32.dp))

        DataCircle(
            count = kpis.usersCreatedPlannedPayment,
            metric = "Created planned\npayment",
            circleColor = Red,
            parentCount = kpis.onboardedUsers,
            avgCount = kpis.avgPlannedPaymentsPerUser
        )

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun Paywall(
    kpis: KPIs,
    onboardingCompletedEventCount: Int
) {
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(32.dp))

        DataCircle(
            count = kpis.paywallAccounts,
            metric = "Paywall Accounts",
            circleColor = RedDark,
            parentCount = onboardingCompletedEventCount,
            avgCount = kpis.avgPaywallAccounts
        )

        Spacer(Modifier.height(32.dp))

        DataCircle(
            count = kpis.paywallCategories,
            metric = "Paywall Categories",
            circleColor = Red,
            parentCount = onboardingCompletedEventCount,
            avgCount = kpis.avgPaywallCategories
        )

        Spacer(Modifier.height(32.dp))

        DataCircle(
            count = kpis.paywallNoReason,
            metric = "Paywall Settings",
            circleColor = RedLight,
            parentCount = onboardingCompletedEventCount,
            avgCount = kpis.avgPaywallNoReason
        )

        Spacer(Modifier.height(32.dp))

        DataCircle(
            count = kpis.paywallExportCSV,
            metric = "Paywall CSV",
            circleColor = Ivy,
            parentCount = onboardingCompletedEventCount,
            avgCount = kpis.avgPaywallExportCSV
        )

        Spacer(Modifier.height(32.dp))

        DataCircle(
            count = kpis.paywallPremiumColor,
            metric = "Paywall Color",
            circleColor = Blue,
            parentCount = onboardingCompletedEventCount,
            avgCount = kpis.avgPaywallPremiumColor
        )

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun ChoosePlanOptions(kpis: KPIs) {
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(32.dp))

        DataCircle(
            count = kpis.choosePlanMonthly,
            metric = "Choose Monthly",
            circleColor = Ivy,
            parentCount = kpis.choosePlan,
        )

        Spacer(Modifier.height(32.dp))

        DataCircle(
            count = kpis.choosePlanYearly,
            metric = "Choose Yearly",
            circleColor = Green,
            parentCount = kpis.choosePlan,
        )

        Spacer(Modifier.height(32.dp))

        DataCircle(
            count = kpis.choosePlanLifetime,
            metric = "Choose Lifetime",
            circleColor = Orange,
            parentCount = kpis.choosePlan,
        )

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun BuyPlanOptions(kpis: KPIs) {
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(32.dp))

        DataCircle(
            count = kpis.startBuyPremiumMonthly,
            metric = "Buy Monthly",
            circleColor = Ivy,
            parentCount = kpis.startBuyPremium,
        )

        Spacer(Modifier.height(32.dp))

        DataCircle(
            count = kpis.startBuyPremiumYearly,
            metric = "Buy Yearly",
            circleColor = Green,
            parentCount = kpis.startBuyPremium,
        )

        Spacer(Modifier.height(32.dp))

        DataCircle(
            count = kpis.startBuyPremiumLifetime,
            metric = "Buy Lifetime",
            circleColor = Orange,
            parentCount = kpis.startBuyPremium,
        )

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
@Preview
private fun Preview() {
    IvyWalletPreview {
        Column(Modifier.fillMaxSize()) {
            KPIsTab(
                kpis = KPIs(
                    onboardedUsers = 1000,

                    usersCreatedAccount = 700,
                    avgAccountsPerUser = 2.4,

                    usersCreatedCategory = 560,
                    avgCategoriesPerUser = 5.5,

                    usersCreatedTransaction = 522,
                    avgTransactionsPerUser = 18.6,

                    usersCreatedPlannedPayment = 430,
                    avgPlannedPaymentsPerUser = 13.0,

                    paywallAccounts = 15,
                    avgPaywallAccounts = 2.3,
                    paywallCategories = 13,
                    avgPaywallCategories = 3.7,
                    paywallNoReason = 3,
                    avgPaywallNoReason = null,
                    paywallExportCSV = 2,
                    avgPaywallExportCSV = 1.7,
                    paywallPremiumColor = 1,
                    avgPaywallPremiumColor = 1.0,

                    choosePlan = 10,
                    choosePlanMonthly = 3,
                    choosePlan6Month = 1,
                    choosePlanYearly = 6,
                    choosePlanLifetime = 8,

                    startBuyPremium = 7,
                    startBuyPremiumMonthly = 2,
                    startBuyPremium6Month = 1,
                    startBuyPremiumYearly = 4,
                    startBuyPremiumLifetime = 5,

                    activePremium = 3,
                ),
                activeUsersCount = 60,
                onboardingCompletedEventCount = 823,
                onboardingStartedEventCount = 1000
            )
        }
    }
}