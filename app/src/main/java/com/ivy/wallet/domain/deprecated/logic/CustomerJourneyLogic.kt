package com.ivy.wallet.domain.deprecated.logic

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.ivy.wallet.Constants
import com.ivy.wallet.R
import com.ivy.wallet.domain.data.TransactionType
import com.ivy.wallet.domain.deprecated.logic.model.CustomerJourneyCardData
import com.ivy.wallet.io.persistence.SharedPrefs
import com.ivy.wallet.io.persistence.dao.PlannedPaymentRuleDao
import com.ivy.wallet.io.persistence.dao.TransactionDao
import com.ivy.wallet.stringRes
import com.ivy.wallet.ui.*
import com.ivy.wallet.ui.home.CustomerJourneyCard
import com.ivy.wallet.ui.main.MainTab
import com.ivy.wallet.ui.theme.*
import com.ivy.wallet.ui.widget.AddTransactionWidgetCompact

@Deprecated("Use FP style, look into `domain.fp` package")
class CustomerJourneyLogic(
    private val transactionDao: TransactionDao,
    private val plannedPaymentRuleDao: PlannedPaymentRuleDao,
    private val sharedPrefs: SharedPrefs,
    private val ivyContext: IvyWalletCtx
) {

    suspend fun loadCards(): List<CustomerJourneyCardData> {
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
//            buyLifetimeOfferCard(),
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
            title = stringRes(R.string.adjust_initial_balance),
            description = stringRes(R.string.adjust_initial_balance_description),
            cta = stringRes(R.string.to_accounts),
            ctaIcon = R.drawable.ic_custom_account_s,
            backgroundColor = Ivy,
            hasDismiss = false,
            onAction = { _, ivyContext, _ ->
                ivyContext.selectMainTab(MainTab.ACCOUNTS)
            }
        )

        fun addPlannedPaymentCard() = CustomerJourneyCardData(
            id = "add_planned_payment",
            condition = { trnCount, plannedPaymentCount, _ ->
                trnCount >= 1 && plannedPaymentCount == 0L
            },
            title = stringRes(R.string.create_first_planned_payment),
            description = stringRes(R.string.create_first_planned_payment_description),
            cta = stringRes(R.string.add_planned_payment),
            ctaIcon = R.drawable.ic_planned_payments,
            backgroundColor = Orange,
            hasDismiss = true,
            onAction = { navigation, _, _ ->
                navigation.navigateTo(
                    EditPlanned(
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
            title = stringRes(R.string.did_you_know),
            description = stringRes(R.string.widget_description),
            cta = stringRes(R.string.add_widget),
            ctaIcon = R.drawable.ic_custom_atom_s,
            backgroundColor = GreenLight,
            hasDismiss = true,
            onAction = { _, _, ivyActivity ->
                ivyActivity.pinWidget(AddTransactionWidgetCompact::class.java)
            }
        )

        fun addBudgetCard() = CustomerJourneyCardData(
            id = "add_budget",
            condition = { trnCount, _, _ ->
                trnCount >= 5
            },
            title = stringRes(R.string.set_a_budget),
                description = stringRes(R.string.set_a_budget_description),
            cta = stringRes(R.string.add_budget),
            ctaIcon = R.drawable.ic_budget_xs,
            backgroundColor = Green2,
            hasDismiss = true,
            onAction = { navigation, _, _ ->
                navigation.navigateTo(BudgetScreen)
            }
        )

        fun didYouKnow_expensesPieChart() = CustomerJourneyCardData(
            id = "expenses_pie_chart",
            condition = { trnCount, _, _ ->
                trnCount >= 7
            },
            title = stringRes(R.string.did_you_know),
            description = stringRes(R.string.expenses_piechart_description),
            cta = stringRes(R.string.expenses_piechart),
            ctaIcon = R.drawable.ic_custom_bills_s,
            backgroundColor = Red,
            hasDismiss = true,
            onAction = { navigation, _, _ ->
                navigation.navigateTo(PieChartStatistic(type = TransactionType.EXPENSE))
            }
        )

        fun rateUsCard() = CustomerJourneyCardData(
            id = "rate_us",
            condition = { trnCount, _, _ ->
                trnCount >= 10
            },
            title = stringRes(R.string.review_ivy_wallet),
            description = stringRes(R.string.review_ivy_wallet_description),
            cta = stringRes(R.string.rate_us_on_google_play),
            ctaIcon = R.drawable.ic_custom_star_s,
            backgroundColor = Green,
            hasDismiss = true,
            onAction = { _, _, ivyActivity ->
                ivyActivity.reviewIvyWallet(dismissReviewCard = true)
            }
        )

        fun shareIvyWalletCard() = CustomerJourneyCardData(
            id = "share_ivy_wallet",
            condition = { trnCount, _, _ ->
                trnCount >= 14
            },
            title = stringRes(R.string.share_ivy_wallet),
            description = stringRes(R.string.help_us_grow),
            cta = stringRes(R.string.share_with_friends),
            ctaIcon = R.drawable.ic_custom_family_s,
            backgroundColor = Red3,
            hasDismiss = true,
            onAction = { _, _, ivyActivity ->
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
            onAction = { navigation, _, _ ->
                navigation.navigateTo(Paywall(paywallReason = null))
            }
        )

        fun makeReportCard() = CustomerJourneyCardData(
            id = "make_report",
            condition = { trnCount, _, _ ->
                trnCount >= 18
            },
            title = stringRes(R.string.did_you_know),
            description = stringRes(R.string.make_a_report_description),
            cta = stringRes(R.string.make_a_report),
            ctaIcon = R.drawable.ic_statistics_xs,
            backgroundColor = Green2,
            hasDismiss = true,
            onAction = { navigation, _, _ ->
                navigation.navigateTo(Report)
            }
        )

        fun rateUsCard_2() = CustomerJourneyCardData(
            id = "rate_us_2",
            condition = { trnCount, _, _ ->
                trnCount >= 22
            },
            title = stringRes(R.string.review_ivy_wallet),
            description = stringRes(R.string.make_ivy_wallet_better_description),
            cta = stringRes(R.string.rate_us_on_google_play),
            ctaIcon = R.drawable.ic_custom_star_s,
            backgroundColor = GreenLight,
            hasDismiss = true,
            onAction = { _, _, ivyActivity ->
                ivyActivity.reviewIvyWallet(dismissReviewCard = true)
            }
        )

        fun shareIvyWalletCard_2() = CustomerJourneyCardData(
            id = "share_ivy_wallet_2",
            condition = { trnCount, _, _ ->
                trnCount >= 24
            },
            title = stringRes(R.string.we_need_your_help),
            description = stringRes(R.string.we_need_your_help_description),
            cta = stringRes(R.string.share_ivy_wallet),
            ctaIcon = R.drawable.ic_custom_family_s,
            backgroundColor = Purple2,
            hasDismiss = true,
            onAction = { _, _, ivyActivity ->
                ivyActivity.shareIvyWallet()
            }
        )

        fun ivyWalletIsOpenSource() = CustomerJourneyCardData(
            id = "open_source",
            condition = { trnCount, _, _ ->
                trnCount >= 28
            },
            title = stringRes(R.string.ivy_wallet_is_opensource),
            description = stringRes(R.string.ivy_wallet_is_opensource_description),
            cta = stringRes(R.string.contribute),
            ctaIcon = R.drawable.github_logo,
            backgroundColor = Blue3,
            hasDismiss = true,
            onAction = { _, _, ivyActivity ->
                ivyActivity.openUrlInBrowser(Constants.URL_IVY_WALLET_REPO)
            }
        )
    }
}

@Preview
@Composable
private fun PreviewAdjustBalanceCard() {
    IvyWalletComponentPreview {
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
    IvyWalletComponentPreview {
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
    IvyWalletComponentPreview {
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
    IvyWalletComponentPreview {
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
    IvyWalletComponentPreview {
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
    IvyWalletComponentPreview {
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
    IvyWalletComponentPreview {
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
    IvyWalletComponentPreview {
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
    IvyWalletComponentPreview {
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
    IvyWalletComponentPreview {
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
    IvyWalletComponentPreview {
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
    IvyWalletComponentPreview {
        CustomerJourneyCard(
            cardData = CustomerJourneyLogic.ivyWalletIsOpenSource(),
            onCTA = { },
            onDismiss = {}
        )
    }
}




