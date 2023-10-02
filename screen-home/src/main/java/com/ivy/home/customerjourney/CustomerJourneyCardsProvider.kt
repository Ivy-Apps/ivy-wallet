package com.ivy.home.customerjourney

import com.ivy.base.legacy.stringRes
import com.ivy.design.l0_system.Blue
import com.ivy.design.l0_system.Blue3
import com.ivy.design.l0_system.Gradient
import com.ivy.design.l0_system.Green
import com.ivy.design.l0_system.GreenLight
import com.ivy.design.l0_system.Ivy
import com.ivy.design.l0_system.Orange
import com.ivy.design.l0_system.Red
import com.ivy.design.l0_system.Red3
import com.ivy.legacy.Constants
import com.ivy.legacy.IvyWalletCtx
import com.ivy.legacy.data.SharedPrefs
import com.ivy.legacy.data.model.MainTab
import com.ivy.navigation.EditPlannedScreen
import com.ivy.navigation.PieChartStatisticScreen
import com.ivy.data.db.dao.read.PlannedPaymentRuleDao
import com.ivy.data.db.dao.read.TransactionDao
import com.ivy.base.model.TransactionType
import com.ivy.resources.R
import com.ivy.widget.transaction.AddTransactionWidgetCompact
import javax.inject.Inject

@Deprecated("Legacy code")
class CustomerJourneyCardsProvider @Inject constructor(
    private val transactionDao: TransactionDao,
    private val plannedPaymentRuleDao: PlannedPaymentRuleDao,
    private val sharedPrefs: SharedPrefs,
    private val ivyContext: IvyWalletCtx
) {

    suspend fun loadCards(): List<CustomerJourneyCardModel> {
        val trnCount = transactionDao.countHappenedTransactions()
        val plannedPaymentsCount = plannedPaymentRuleDao.countPlannedPayments()

        return ACTIVE_CARDS
            .filter {
                it.condition(trnCount, plannedPaymentsCount, ivyContext) && !isCardDismissed(it)
            }
    }

    private fun isCardDismissed(cardData: CustomerJourneyCardModel): Boolean {
        return sharedPrefs.getBoolean(sharedPrefsKey(cardData), false)
    }

    fun dismissCard(cardData: CustomerJourneyCardModel) {
        sharedPrefs.putBoolean(sharedPrefsKey(cardData), true)
    }

    private fun sharedPrefsKey(cardData: CustomerJourneyCardModel): String {
        return "${cardData.id}${SharedPrefs._CARD_DISMISSED}"
    }

    companion object {
        val ACTIVE_CARDS = listOf(
            adjustBalanceCard(),
            addPlannedPaymentCard(),
            didYouKnow_pinAddTransactionWidgetCard(),
            didYouKnow_expensesPieChart(),
            rateUsCard(),
            shareIvyWalletCard(),
            joinIvyTelegramCard(),
            rateUsCard_2(),
            joinTelegram2(),
            ivyWalletIsOpenSource(),
        )

        fun adjustBalanceCard() = CustomerJourneyCardModel(
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
                ivyContext.selectMainTab(MainTab.ACCOUNTS)
            }
        )

        fun addPlannedPaymentCard() = CustomerJourneyCardModel(
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
                    EditPlannedScreen(
                        type = TransactionType.EXPENSE,
                        plannedPaymentRuleId = null
                    )
                )
            }
        )

        fun didYouKnow_pinAddTransactionWidgetCard() = CustomerJourneyCardModel(
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

        fun didYouKnow_expensesPieChart() = CustomerJourneyCardModel(
            id = "expenses_pie_chart",
            condition = { trnCount, _, _ ->
                trnCount >= 7
            },
            title = stringRes(R.string.did_you_know),
            description = "You can see a PieChart for your expenses!" +
                    " Click the Expense card on the top of the dashboard.",
            cta = stringRes(R.string.expenses_piechart),
            ctaIcon = R.drawable.ic_custom_bills_s,
            background = Gradient.solid(Red),
            hasDismiss = true,
            onAction = { navigation, _, _ ->
                navigation.navigateTo(PieChartStatisticScreen(type = TransactionType.EXPENSE))
            }
        )

        fun rateUsCard() = CustomerJourneyCardModel(
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

        fun shareIvyWalletCard() = CustomerJourneyCardModel(
            id = "share_ivy_wallet",
            condition = { trnCount, _, _ ->
                trnCount >= 11
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

        fun joinIvyTelegramCard() = CustomerJourneyCardModel(
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

        fun ivyWalletIsOpenSource() = CustomerJourneyCardModel(
            id = "open_source",
            condition = { trnCount, _, _ ->
                trnCount >= 20
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

        fun rateUsCard_2() = CustomerJourneyCardModel(
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

        fun joinTelegram2() = CustomerJourneyCardModel(
            id = "ivy_telegram_2",
            condition = { trnCount, _, _ ->
                trnCount >= 28
            },
            description = "It looks like that you're enjoying Ivy Wallet! " +
                    "If you haven't yet, feel free join our invite-only Ivy Telegram Community and make our app better :)",
            title = "Ivy Community",
            cta = "Join now",
            ctaIcon = R.drawable.ic_telegram_24dp,
            background = Gradient.solid(Blue),
            hasDismiss = true,
            onAction = { _, _, ivyActivity ->
                ivyActivity.openUrlInBrowser(Constants.URL_IVY_TELEGRAM_INVITE)
            }
        )
    }
}