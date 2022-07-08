package com.ivy.journey.domain

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.ivy.base.Constants
import com.ivy.base.IvyWalletComponentPreview
import com.ivy.base.stringRes
import com.ivy.data.transaction.TransactionType
import com.ivy.screens.*
import com.ivy.wallet.io.persistence.SharedPrefs
import com.ivy.wallet.io.persistence.dao.PlannedPaymentRuleDao
import com.ivy.wallet.io.persistence.dao.TransactionDao
import com.ivy.base.R
import com.ivy.design.l0_system.*
import com.ivy.journey.CustomerJourneyCard
import com.ivy.wallet.ui.theme.Ivy
import com.ivy.widgets.AddTransactionWidgetCompact

@Deprecated("Use FP style, look into `domain.fp` package")
class CustomerJourneyLogic(
    private val transactionDao: TransactionDao,
    private val plannedPaymentRuleDao: PlannedPaymentRuleDao,
    private val sharedPrefs: SharedPrefs,
    private val ivyContext: com.ivy.base.IvyWalletCtx
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
            joinIvyTelegramCard(),
            makeReportCard(),
            rateUsCard_2(),
            shareIvyWalletCard_2(),
            ivyWalletIsOpenSource(),
            donateIvyWallet()
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
            background = Gradient.solid(Ivy),
            hasDismiss = false,
            onAction = { _, ivyContext, _ ->
                ivyContext.selectMainTab(com.ivy.base.MainTab.ACCOUNTS)
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
            background = Gradient.solid(Orange),
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
            background = Gradient.solid(GreenLight),
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
            background = Gradient.solid(Green2),
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
            background = Gradient.solid(Red),
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
            background = Gradient.solid(Green),
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
            background = Gradient.solid(Red3),
            hasDismiss = true,
            onAction = { _, _, ivyActivity ->
                ivyActivity.shareIvyWallet()
            }
        )

        fun joinIvyTelegramCard() = CustomerJourneyCardData(
            id = "join_ivy_telegram",
            condition = { trnCount, _, _ ->
                trnCount >= 16
            },
            description = "It looks like that you're enjoying Ivy Wallet! Feel free join our invite-only Ivy Telegram Community and make our app better :)",
            title = "Ivy Community",
            cta = "Join now",
            ctaIcon = R.drawable.ic_telegram_24dp,
            background = Gradient.solid(Blue),
            hasDismiss = true,
            onAction = { _, _, rootActivity ->
                rootActivity.openUrlInBrowser(Constants.URL_IVY_TELEGRAM_INVITE)
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
            background = Gradient.solid(Green2),
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
            background = Gradient.solid(GreenLight),
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
            background = Gradient.solid(Purple2),
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
            background = Gradient.solid(Blue3),
            hasDismiss = true,
            onAction = { _, _, ivyActivity ->
                ivyActivity.openUrlInBrowser(Constants.URL_IVY_WALLET_REPO)
            }
        )

        fun donateIvyWallet() = CustomerJourneyCardData(
            id = "donate_ivy_wallet",
            condition = { trnCount, _, _ ->
                trnCount >= 30
            },
            title = "Support Ivy Wallet",
            description = "It seems like you enjoy free and open-source software. We too! " +
                    "That's why we opened a donations channel to sustain and improve our small project.",
            cta = "Donate",
            ctaIcon = R.drawable.ic_donate_crown,
            background = SunsetNight,
            hasDismiss = true,
            onAction = { nav, _, _ ->
                nav.navigateTo(DonateScreen)
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
private fun PreviewJoinTelegram() {
    IvyWalletComponentPreview {
        CustomerJourneyCard(
            cardData = CustomerJourneyLogic.joinIvyTelegramCard(),
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

@Preview
@Composable
private fun PreviewDonateCard() {
    IvyWalletComponentPreview {
        CustomerJourneyCard(
            cardData = CustomerJourneyLogic.donateIvyWallet(),
            onCTA = { },
            onDismiss = {}
        )
    }
}




