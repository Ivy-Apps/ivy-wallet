package com.ivy.planned.list

import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ivy.core.datamodel.Account
import com.ivy.core.datamodel.Category
import com.ivy.core.datamodel.IntervalType
import com.ivy.core.datamodel.PlannedPaymentRule
import com.ivy.core.db.entity.TransactionType
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.legacy.IvyWalletPreview
import com.ivy.legacy.utils.onScreenStart
import com.ivy.legacy.utils.timeNowUTC
import com.ivy.navigation.EditPlannedScreen
import com.ivy.navigation.PlannedPaymentsScreen
import com.ivy.navigation.navigation
import com.ivy.resources.R
import com.ivy.wallet.ui.theme.Orange

@Composable
fun BoxWithConstraintsScope.PlannedPaymentsScreen(screen: PlannedPaymentsScreen) {
    val viewModel: PlannedPaymentsViewModel = viewModel()

    val currency by viewModel.currency.observeAsState("")
    val categories by viewModel.categories.observeAsState(emptyList())
    val accounts by viewModel.accounts.observeAsState(emptyList())
    val oneTime by viewModel.oneTime.observeAsState(emptyList())
    val oneTimeIncome by viewModel.oneTimeIncome.observeAsState(0.0)
    val oneTimeExpenses by viewModel.oneTimeExpenses.observeAsState(0.0)
    val recurring by viewModel.recurring.observeAsState(emptyList())
    val recurringIncome by viewModel.recurringIncome.observeAsState(0.0)
    val recurringExpenses by viewModel.recurringExpenses.observeAsState(0.0)

    onScreenStart {
        viewModel.start(screen)
    }

    UI(
        currency = currency,
        categories = categories,
        accounts = accounts,
        oneTime = oneTime,
        oneTimeIncome = oneTimeIncome,
        oneTimeExpenses = oneTimeExpenses,
        recurring = recurring,
        recurringIncome = recurringIncome,
        recurringExpenses = recurringExpenses
    )
}

@Composable
private fun BoxWithConstraintsScope.UI(
    currency: String,

    categories: List<Category>,
    accounts: List<Account>,

    oneTime: List<PlannedPaymentRule>,
    oneTimeIncome: Double,
    oneTimeExpenses: Double,

    recurring: List<PlannedPaymentRule>,
    recurringIncome: Double,
    recurringExpenses: Double
) {
    PlannedPaymentsLazyColumn(
        Header = {
            Spacer(Modifier.height(32.dp))

            Text(
                modifier = Modifier.padding(start = 24.dp),
                text = stringResource(R.string.planned_payments_inline),
                style = UI.typo.h2.style(
                    fontWeight = FontWeight.ExtraBold,
                    color = UI.colors.pureInverse
                )
            )

            Spacer(Modifier.height(24.dp))
        },

        currency = currency,
        categories = categories,
        accounts = accounts,
        oneTime = oneTime,
        oneTimeIncome = oneTimeIncome,
        oneTimeExpenses = oneTimeExpenses,
        recurring = recurring,
        recurringIncome = recurringIncome,
        recurringExpenses = recurringExpenses
    )

    val nav = navigation()
    PlannedPaymentsBottomBar(
        onClose = {
            nav.back()
        },
        onAdd = {
            nav.navigateTo(
                EditPlannedScreen(
                    type = TransactionType.EXPENSE,
                    plannedPaymentRuleId = null
                )
            )
        }
    )
}

@Preview
@Composable
private fun Preview() {
    IvyWalletPreview {
        val account = Account(name = "Cash")
        val food = Category(name = "Food")
        val shisha = Category(name = "Shisha", color = Orange.toArgb())

        UI(
            currency = "BGN",
            accounts = listOf(account),
            categories = listOf(food, shisha),

            oneTime = listOf(
                PlannedPaymentRule(
                    accountId = account.id,
                    title = "Lidl pazar",
                    categoryId = food.id,
                    amount = 250.75,
                    startDate = timeNowUTC().plusDays(5),
                    oneTime = true,
                    intervalType = null,
                    intervalN = null,
                    type = TransactionType.EXPENSE
                )
            ),
            oneTimeExpenses = 250.75,
            oneTimeIncome = 0.0,
            recurring = listOf(
                PlannedPaymentRule(
                    accountId = account.id,
                    title = "Tabu",
                    categoryId = shisha.id,
                    amount = 1025.5,
                    startDate = timeNowUTC().plusDays(5),
                    oneTime = false,
                    intervalType = IntervalType.MONTH,
                    intervalN = 1,
                    type = TransactionType.EXPENSE
                )
            ),
            recurringExpenses = 1025.5,
            recurringIncome = 0.0
        )
    }
}
