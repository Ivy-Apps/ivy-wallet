package com.ivy.wallet.ui.paywall

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.billingclient.api.SkuDetails
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.frp.view.navigation.navigation
import com.ivy.wallet.Constants
import com.ivy.wallet.R
import com.ivy.wallet.android.billing.Plan
import com.ivy.wallet.android.billing.PlanType
import com.ivy.wallet.domain.data.core.Account
import com.ivy.wallet.domain.data.core.Budget
import com.ivy.wallet.domain.data.core.Category
import com.ivy.wallet.domain.data.core.Loan
import com.ivy.wallet.ui.IvyWalletPreview
import com.ivy.wallet.ui.Paywall
import com.ivy.wallet.ui.RootActivity
import com.ivy.wallet.ui.ivyWalletCtx
import com.ivy.wallet.ui.theme.*
import com.ivy.wallet.ui.theme.components.BackButtonType
import com.ivy.wallet.ui.theme.components.IvyIcon
import com.ivy.wallet.ui.theme.components.IvyToolbar
import com.ivy.wallet.utils.onScreenStart
import com.ivy.wallet.utils.toDensityDp

private const val BENEFIT_TAG_ACCOUNTS = "accs"
private const val BENEFIT_TAG_CATEGORIES = "cats"
private const val BENEFIT_TAG_BUDGETS = "budgs"
private const val BENEFIT_TAG_LOANS = "loans"

private val BENEFITS = listOf(
    Benefit(
        text = "Unlimited accounts",
        tag = BENEFIT_TAG_ACCOUNTS
    ),
    Benefit(
        text = "Unlimited categories",
        tag = BENEFIT_TAG_CATEGORIES
    ),
    Benefit(
        text = "Unlimited budgets",
        tag = BENEFIT_TAG_BUDGETS,
    ),
    Benefit(
        text = "Unlimited loans",
        tag = BENEFIT_TAG_LOANS,
    ),
    Benefit(
        text = "Export \"Reports\" to CSV (Google Sheets & Excel)"
    ),
    Benefit(
        text = "Unlock more category & account colors"
    ),
    Benefit(
        text = "Unlimited access to all Ivy Wallet's features"
    ),
)

@ExperimentalFoundationApi
@Composable
fun BoxWithConstraintsScope.PaywallScreen(screen: Paywall, activity: RootActivity) {
    val viewModel: PaywallViewModel = viewModel()

    val plans by viewModel.plans.observeAsState(emptyList())
    val purchasedSkus by viewModel.purchasedSkus.observeAsState(emptyList())
    val paywallReason by viewModel.paywallReason.observeAsState()
    val accounts by viewModel.accounts.observeAsState(emptyList())
    val categories by viewModel.categories.observeAsState(emptyList())
    val budgets by viewModel.budgets.observeAsState(emptyList())
    val loans by viewModel.loans.observeAsState(emptyList())

    onScreenStart {
        viewModel.start(
            screen = screen,
            activity = activity
        )
    }

    UI(
        plans = plans,
        purchasedSkus = purchasedSkus,
        paywallReason = paywallReason,

        accounts = accounts,
        categories = categories,
        budgets = budgets,
        loans = loans,

        onPlanSelected = viewModel::onPlanSelected,
        onBuy = {
            viewModel.buy(activity, it)
        }
    )
}

@ExperimentalFoundationApi
@Composable
private fun BoxWithConstraintsScope.UI(
    plans: List<Plan>,
    purchasedSkus: List<String>,
    paywallReason: PaywallReason?,

    accounts: List<Account>,
    categories: List<Category>,
    budgets: List<Budget>,
    loans: List<Loan>,

    onPlanSelected: (Plan?) -> Unit = {},
    onBuy: (Plan) -> Unit,
) {
    var plansSheetHeight by remember { mutableStateOf(0) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        stickyHeader {
            val nav = navigation()

            IvyToolbar(
                backButtonType = BackButtonType.CLOSE,
                onBack = { nav.onBackPressed() }
            )
        }

        item {
            //16.dp padding from IvyToolbar already => 24.dp - 16.dp = 8.dp
            Spacer(Modifier.height(8.dp))

            Text(
                modifier = Modifier
                    .padding(start = 32.dp),
                text = "Get premium",
                style = UI.typo.h2.style(
                    fontWeight = FontWeight.Black
                )
            )

            Spacer(Modifier.height(32.dp))
        }

        items(items = BENEFITS) { benefit: Benefit ->
            BenefitRow(benefit = benefit) {
                when (benefit.tag) {
                    BENEFIT_TAG_ACCOUNTS -> {
                        UsageText(
                            usedCount = accounts.size,
                            freeCount = Constants.FREE_ACCOUNTS,
                            itemName = "accounts"
                        )
                    }
                    BENEFIT_TAG_CATEGORIES -> {
                        UsageText(
                            usedCount = categories.size,
                            freeCount = Constants.FREE_CATEGORIES,
                            itemName = "categories"
                        )
                    }
                    BENEFIT_TAG_BUDGETS -> {
                        UsageText(
                            usedCount = budgets.size,
                            freeCount = Constants.FREE_BUDGETS,
                            itemName = "budgets"
                        )
                    }
                    BENEFIT_TAG_LOANS -> {
                        UsageText(
                            usedCount = loans.size,
                            freeCount = Constants.FREE_LOANS,
                            itemName = "loans"
                        )
                    }
                    else -> {
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }

        item {
            Spacer(Modifier.height(64.dp)) //last item spacer
            if (plansSheetHeight > 0) {
                Spacer(Modifier.height(plansSheetHeight.toDensityDp()))
            }
        }
    }

    PlansSheet(
        plans = plans,
        purchasedSkus = purchasedSkus,

        onPlansSheetHeightChanged = { height ->
            plansSheetHeight = height
        },

        onPlanSelected = onPlanSelected,
        onBuy = onBuy
    )
}

@Composable
private fun UsageText(
    usedCount: Int,
    freeCount: Int,

    itemName: String
) {
    val isPremium = ivyWalletCtx().isPremium

    if (!isPremium) {
        Spacer(Modifier.height(4.dp))

        val usedPercent = usedCount / freeCount.toFloat()

        Text(
            text = "You have ${usedCount}/$freeCount free $itemName.",
            style = UI.typo.nC.style(
                fontWeight = FontWeight.ExtraBold,
                color = when {
                    usedPercent >= 1f -> Red
                    usedPercent > 0.6f -> Orange
                    else -> Gray
                }
            )
        )
    }
}

@Composable
private fun BenefitRow(
    benefit: Benefit,
    ExtraInfo: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(24.dp))

        val ivyContext = ivyWalletCtx()
        IvyIcon(
            modifier = Modifier
                .background(
                    brush = (if (ivyContext.isPremium) GradientIvy else GradientOrange)
                        .asHorizontalBrush(),
                    shape = CircleShape
                ),
            icon = R.drawable.ic_custom_crown_s,
            tint = White
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(
                    start = 12.dp,
                    end = 24.dp,
                )
                .padding(bottom = 4.dp),
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = benefit.text,
                style = UI.typo.b2.style(
                    fontWeight = FontWeight.Bold
                )
            )

            ExtraInfo()
        }
    }
}

data class Benefit(
    val text: String,
    val tag: String? = null,
)


@ExperimentalFoundationApi
@Preview
@Composable
private fun Preview() {
    IvyWalletPreview {
        UI(
            plans = listOf(
                Plan(
                    sku = "sku1",
                    type = PlanType.MONTHLY,
                    price = "BGN 8.03",
                    skuDetails = SkuDetails("{\"productId\":\"test\", \"type\":\"MONTHLY\"}")
                ),
                Plan(
                    sku = "sku2",
                    type = PlanType.SIX_MONTH,
                    price = "BGN 6.42",
                    skuDetails = SkuDetails("{\"productId\":\"test\", \"type\":\"MONTHLY\"}")
                ),
                Plan(
                    sku = "sku3",
                    type = PlanType.YEARLY,
                    price = "BGN 4.82",
                    skuDetails = SkuDetails("{\"productId\":\"test\", \"type\":\"MONTHLY\"}")
                )
            ),

            paywallReason = PaywallReason.ACCOUNTS,
            purchasedSkus = listOf("sku1"),

            accounts = emptyList(),
            categories = emptyList(),
            budgets = emptyList(),
            loans = emptyList(),

            onBuy = {}
        )
    }
}
