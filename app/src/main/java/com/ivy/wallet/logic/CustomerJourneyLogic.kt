package com.ivy.wallet.logic

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.ivy.wallet.Constants
import com.ivy.wallet.R
import com.ivy.wallet.logic.model.CustomerJourneyCardData
import com.ivy.wallet.model.TransactionType
import com.ivy.wallet.persistence.SharedPrefs
import com.ivy.wallet.persistence.dao.PlannedPaymentRuleDao
import com.ivy.wallet.persistence.dao.TransactionDao
import com.ivy.wallet.ui.IvyWalletCtx
import com.ivy.wallet.ui.Screen
import com.ivy.wallet.ui.home.CustomerJourneyCard
import com.ivy.wallet.ui.main.MainTab
import com.ivy.wallet.ui.theme.*
import com.ivy.wallet.widget.AddTransactionWidgetCompact

class CustomerJourneyLogic(
    private val transactionDao: TransactionDao,
    private val plannedPaymentRuleDao: PlannedPaymentRuleDao,
    private val sharedPrefs: SharedPrefs,
    private val ivyContext: IvyWalletCtx
) {

    fun loadCards(): List<CustomerJourneyCardData> {
        val trnCount = transactionDao.countHappenedTransactions()
        val plannedPaymentsCount = plannedPaymentRuleDao.countPlannedPayments()

        return ACTIVE_CARDS
            .filter {
                it.condition(trnCount, plannedPaymentsCount, ivyContext) && !isCardDismissed(it)
            }
    }

    private fun isCardDismissed(cardData: CustomerJourneyCardData): Boolean {
        return sharedPrefs.getBoolean(sharedPrefsKey(cardData), false)
    }

    fun dismissCard(cardData: CustomerJourneyCardData) {
        sharedPrefs.putBoolean(sharedPrefsKey(cardData), true)
    }

    private fun sharedPrefsKey(cardData: CustomerJourneyCardData): String {
        return "${cardData.id}${SharedPrefs._CARD_DISMISSED}"
    }

    companion object {
        val ACTIVE_CARDS = listOf(
            adjustBalanceCard(),
            addPlannedPaymentCard(),
            didYouKnow_pinAddTransactionWidgetCard(),
            addBudgetCard(),
            didYouKnow_expensesPieChart(),
            rateUsCard(),
            shareIvyWalletCard(),
            buyLifetimeOfferCard(),
            makeReportCard(),
            rateUsCard_2(),
            shareIvyWalletCard_2(),
            ivyWalletIsOpenSource()
        )

        fun adjustBalanceCard() = CustomerJourneyCardData(
            id = "adjust_balance",
            condition = { trnCount, _, _ ->
                trnCount == 0L
            },
            title = "Adjust your initial balance",
            description = "Let's get started. Go to \"Accounts\" -> Tap an account -> Tap its balance -> Enter current balance. That's it!",
            cta = "To accounts",
            ctaIcon = R.drawable.ic_custom_account_s,
            backgroundColor = Ivy,
            hasDismiss = false,
            onAction = { ivyContext, _ ->
                ivyContext.selectMainTab(MainTab.ACCOUNTS)
            }
        )

        fun addPlannedPaymentCard() = CustomerJourneyCardData(
            id = "add_planned_payment",
            condition = { trnCount, plannedPaymentCount, _ ->
                trnCount >= 1 && plannedPaymentCount == 0L
            },
            title = "Create your first planned payment",
            description = "Automate the tracking of recurring transactions like your subscriptions, rent, salary, etc." +
                    " Stay ahead of your finances by knowing how much you have to pay/get in advance.",
            cta = "Add planned payment",
            ctaIcon = R.drawable.ic_planned_payments,
            backgroundColor = Orange,
            hasDismiss = true,
            onAction = { ivyContext, _ ->
                ivyContext.navigateTo(
                    Screen.EditPlanned(
                        type = TransactionType.EXPENSE,
                        plannedPaymentRuleId = null
                    )
                )
            }
        )

        fun didYouKnow_pinAddTransactionWidgetCard() = CustomerJourneyCardData(
            id = "add_transaction_widget",
            condition = { trnCount, _, _ ->
                trnCount >= 3
            },
            title = "Did you know?",
            description = "Ivy Wallet has a cool widget that lets you add INCOME/EXPENSES/TRANSFER transactions with 1-click from your home screen. " +
                    "\n\nNote: If the \"Add widget\" button doesn't work, please add it manually from your launcher's widgets menu.",
            cta = "Add widget",
            ctaIcon = R.drawable.ic_custom_atom_s,
            backgroundColor = GreenLight,
            hasDismiss = true,
            onAction = { _, ivyActivity ->
                ivyActivity.pinWidget(AddTransactionWidgetCompact::class.java)
            }
        )

        fun addBudgetCard() = CustomerJourneyCardData(
            id = "add_budget",
            condition = { trnCount, _, _ ->
                trnCount >= 5
            },
            title = "Set a budget",
            description = "Ivy Wallet not only helps you to passively track your expenses" +
                    " but also proactively create your financial future by setting budgets" +
                    " and sticking to them.",
            cta = "Add budget",
            ctaIcon = R.drawable.ic_budget_xs,
            backgroundColor = Green2,
            hasDismiss = true,
            onAction = { ivyContext, _ ->
                ivyContext.navigateTo(Screen.Budget)
            }
        )

        fun didYouKnow_expensesPieChart() = CustomerJourneyCardData(
            id = "expenses_pie_chart",
            condition = { trnCount, _, _ ->
                trnCount >= 7
            },
            title = "Did you know?",
            description = "You can see your expenses structure by categories! Try it, tap the gray/black \"Expenses\" button just below your balance.",
            cta = "Expenses PieChart",
            ctaIcon = R.drawable.ic_custom_bills_s,
            backgroundColor = Red,
            hasDismiss = true,
            onAction = { ivyContext, _ ->
                ivyContext.navigateTo(Screen.PieChartStatistic(type = TransactionType.EXPENSE))
            }
        )

        fun rateUsCard() = CustomerJourneyCardData(
            id = "rate_us",
            condition = { trnCount, _, _ ->
                trnCount >= 10
            },
            title = "Review Ivy Wallet",
            description = "Give us your feedback! Help Ivy Wallet become better and grow by writing us a review." +
                    " Compliments, ideas, and critics are all welcome!" +
                    " We do our best.\n\nCheers,\nIvy Team",
            cta = "Rate us on Google Play",
            ctaIcon = R.drawable.ic_custom_star_s,
            backgroundColor = Green,
            hasDismiss = true,
            onAction = { _, ivyActivity ->
                ivyActivity.reviewIvyWallet(dismissReviewCard = true)
            }
        )

        fun shareIvyWalletCard() = CustomerJourneyCardData(
            id = "share_ivy_wallet",
            condition = { trnCount, _, _ ->
                trnCount >= 14
            },
            title = "Share Ivy Wallet",
            description = "Help us grow so we can invest more in development and make the app better for you." +
                    " By sharing Ivy Wallet you'll make two developers happy and also help a friend to take control of their finances.",
            cta = "Share with friends",
            ctaIcon = R.drawable.ic_custom_family_s,
            backgroundColor = Red3,
            hasDismiss = true,
            onAction = { _, ivyActivity ->
                ivyActivity.shareIvyWallet()
            }
        )

        fun buyLifetimeOfferCard() = CustomerJourneyCardData(
            id = "buy_lifetime_offer",
            condition = { trnCount, _, ivyContext ->
                trnCount >= 16 && !ivyContext.isPremium
            },
            title = "Lifetime Premium",
            description = "We understand that owning something is better than just paying a subscription for it." +
                    " That's why we've included this special limited lifetime offer only for our best users like you.",
            cta = "Get Lifetime Premium",
            ctaIcon = R.drawable.ic_custom_crown_s,
            backgroundColor = Ivy,
            hasDismiss = true,
            onAction = { ivyContext, _ ->
                ivyContext.navigateTo(Screen.Paywall(paywallReason = null))
            }
        )

        fun makeReportCard() = CustomerJourneyCardData(
            id = "make_report",
            condition = { trnCount, _, _ ->
                trnCount >= 18
            },
            title = "Did you know?",
            description = "You can generate reports to get deep insights about your income and spending." +
                    " Filter your transactions by type, time period, category, accounts, amount, keywords and more" +
                    " to gain better view on your finances.",
            cta = "Make a report",
            ctaIcon = R.drawable.ic_statistics_xs,
            backgroundColor = Green2,
            hasDismiss = true,
            onAction = { ivyContext, _ ->
                ivyContext.navigateTo(Screen.Report)
            }
        )

        fun rateUsCard_2() = CustomerJourneyCardData(
            id = "rate_us_2",
            condition = { trnCount, _, _ ->
                trnCount >= 22
            },
            title = "Review Ivy Wallet",
            description = "Want to make Ivy Wallet better? Write us a review." +
                    " That's the only way for us to develop what you want and need." +
                    " Also it help us rank higher in the PlayStore so we can spend money on the product rather than marketing." +
                    "\n\nWe do our best.\nIvy Team",
            cta = "Rate us on Google Play",
            ctaIcon = R.drawable.ic_custom_star_s,
            backgroundColor = GreenLight,
            hasDismiss = true,
            onAction = { _, ivyActivity ->
                ivyActivity.reviewIvyWallet(dismissReviewCard = true)
            }
        )

        fun shareIvyWalletCard_2() = CustomerJourneyCardData(
            id = "share_ivy_wallet_2",
            condition = { trnCount, _, _ ->
                trnCount >= 24
            },
            title = "We need your help!",
            description = "We're just a designer and a developer" +
                    " working on the app after our 9-5 jobs. Currently, we invest a lot of time and money" +
                    " to generate only losses and exhaustion." +
                    " If you want us to keep developing Ivy Wallet please share it with friends and family." +
                    "\n\nP.S. Google PlayStore reviews also helps a lot!",
            cta = "Share Ivy Wallet",
            ctaIcon = R.drawable.ic_custom_family_s,
            backgroundColor = Purple2,
            hasDismiss = true,
            onAction = { _, ivyActivity ->
                ivyActivity.shareIvyWallet()
            }
        )

        fun ivyWalletIsOpenSource() = CustomerJourneyCardData(
            id = "open_source",
            condition = { trnCount, _, _ ->
                trnCount >= 28
            },
            title = "Ivy Wallet is open-source!",
            description = "Ivy Wallet's code is open and everyone can see it." +
                    " We believe that transparency and ethics are must for every software product." +
                    " If you like our work and want to make the app better you can contribute in our public Github repository.",
            cta = "Contribute",
            ctaIcon = R.drawable.github_logo,
            backgroundColor = Blue3,
            hasDismiss = true,
            onAction = { _, ivyActivity ->
                ivyActivity.openUrlInBrowser(Constants.URL_IVY_WALLET_REPO)
            }
        )
    }
}

@Preview
@Composable
private fun PreviewAdjustBalanceCard() {
    IvyComponentPreview {
        CustomerJourneyCard(
            cardData = CustomerJourneyLogic.adjustBalanceCard(),
            onCTA = { },
            onDismiss = {}
        )
    }
}

@Preview
@Composable
private fun PreviewAddPlannedPaymentCard() {
    IvyComponentPreview {
        CustomerJourneyCard(
            cardData = CustomerJourneyLogic.addPlannedPaymentCard(),
            onCTA = { },
            onDismiss = {}
        )
    }
}

@Preview
@Composable
private fun PreviewDidYouKnow_PinAddTransactionWidgetCard() {
    IvyComponentPreview {
        CustomerJourneyCard(
            cardData = CustomerJourneyLogic.didYouKnow_pinAddTransactionWidgetCard(),
            onCTA = { },
            onDismiss = {}
        )
    }
}

@Preview
@Composable
private fun PreviewAddBudgetCard() {
    IvyComponentPreview {
        CustomerJourneyCard(
            cardData = CustomerJourneyLogic.addBudgetCard(),
            onCTA = { },
            onDismiss = {}
        )
    }
}

@Preview
@Composable
private fun PreviewDidYouKnow_ExpensesPieChart() {
    IvyComponentPreview {
        CustomerJourneyCard(
            cardData = CustomerJourneyLogic.didYouKnow_expensesPieChart(),
            onCTA = { },
            onDismiss = {}
        )
    }
}

@Preview
@Composable
private fun PreviewRateUsCard() {
    IvyComponentPreview {
        CustomerJourneyCard(
            cardData = CustomerJourneyLogic.rateUsCard(),
            onCTA = { },
            onDismiss = {}
        )
    }
}

@Preview
@Composable
private fun PreviewShareIvyWallet() {
    IvyComponentPreview {
        CustomerJourneyCard(
            cardData = CustomerJourneyLogic.shareIvyWalletCard(),
            onCTA = { },
            onDismiss = {}
        )
    }
}

@Preview
@Composable
private fun PreviewBuyLifetimeOffer() {
    IvyComponentPreview {
        CustomerJourneyCard(
            cardData = CustomerJourneyLogic.buyLifetimeOfferCard(),
            onCTA = { },
            onDismiss = {}
        )
    }
}

@Preview
@Composable
private fun PreviewMakeReport() {
    IvyComponentPreview {
        CustomerJourneyCard(
            cardData = CustomerJourneyLogic.makeReportCard(),
            onCTA = { },
            onDismiss = {}
        )
    }
}

@Preview
@Composable
private fun PreviewRateUs_2() {
    IvyComponentPreview {
        CustomerJourneyCard(
            cardData = CustomerJourneyLogic.rateUsCard_2(),
            onCTA = { },
            onDismiss = {}
        )
    }
}

@Preview
@Composable
private fun PreviewShaveIvyWallet_2() {
    IvyComponentPreview {
        CustomerJourneyCard(
            cardData = CustomerJourneyLogic.shareIvyWalletCard_2(),
            onCTA = { },
            onDismiss = {}
        )
    }
}

@Preview
@Composable
private fun PreviewIvyWallet_isOpenSource() {
    IvyComponentPreview {
        CustomerJourneyCard(
            cardData = CustomerJourneyLogic.ivyWalletIsOpenSource(),
            onCTA = { },
            onDismiss = {}
        )
    }
}




