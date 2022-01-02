package com.ivy.wallet.ui.planned.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.wallet.R
import com.ivy.wallet.base.*
import com.ivy.wallet.model.IntervalType
import com.ivy.wallet.model.TransactionType
import com.ivy.wallet.model.entity.Account
import com.ivy.wallet.model.entity.Category
import com.ivy.wallet.model.entity.PlannedPaymentRule
import com.ivy.wallet.ui.IvyAppPreview
import com.ivy.wallet.ui.LocalIvyContext
import com.ivy.wallet.ui.Screen
import com.ivy.wallet.ui.theme.*
import com.ivy.wallet.ui.theme.components.IvyButton
import com.ivy.wallet.ui.theme.components.IvyIcon
import com.ivy.wallet.ui.theme.components.getCustomIconIdS
import com.ivy.wallet.ui.theme.transaction.TypeAmountCurrency
import java.time.LocalDateTime

@Composable
fun LazyItemScope.PlannedPaymentCard(
    baseCurrency: String,
    categories: List<Category>,
    accounts: List<Account>,
    plannedPayment: PlannedPaymentRule,
    onClick: (PlannedPaymentRule) -> Unit,
) {
    Spacer(Modifier.height(12.dp))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(Shapes.rounded16)
            .clickable {
                if (accounts.find { it.id == plannedPayment.accountId } != null) {
                    onClick(plannedPayment)
                }
            }
            .background(IvyTheme.colors.medium, Shapes.rounded16)
            .testTag("planned_payment_card")
    ) {
        val currency = accounts.find { it.id == plannedPayment.accountId }?.currency ?: baseCurrency

        Spacer(Modifier.height(20.dp))

        PlannedPaymentHeaderRow(
            plannedPayment = plannedPayment,
            categories = categories,
            accounts = accounts
        )

        Spacer(Modifier.height(16.dp))

        RuleTextRow(
            oneTime = plannedPayment.oneTime,
            startDate = plannedPayment.startDate,
            intervalN = plannedPayment.intervalN,
            intervalType = plannedPayment.intervalType
        )

        if (plannedPayment.title.isNotNullOrBlank()) {
            Spacer(Modifier.height(8.dp))

            Text(
                modifier = Modifier.padding(horizontal = 24.dp),
                text = plannedPayment.title!!,
                style = Typo.body1.style(
                    fontWeight = FontWeight.ExtraBold,
                    color = IvyTheme.colors.pureInverse
                )
            )
        }

        Spacer(Modifier.height(20.dp))

        TypeAmountCurrency(
            transactionType = plannedPayment.type,
            dueDate = null,
            currency = currency,
            amount = plannedPayment.amount
        )

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun PlannedPaymentHeaderRow(
    plannedPayment: PlannedPaymentRule,
    categories: List<Category>,
    accounts: List<Account>
) {
    val ivyContext = LocalIvyContext.current

    if (plannedPayment.type != TransactionType.TRANSFER) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.width(20.dp))

            IvyIcon(
                modifier = Modifier
                    .background(IvyTheme.colors.pure, CircleShape),
                icon = R.drawable.ic_planned_payments,
                tint = IvyTheme.colors.pureInverse
            )

            Spacer(Modifier.width(12.dp))

            val category =
                plannedPayment.categoryId?.let { targetId -> categories.find { it.id == targetId } }
            if (category != null) {
                IvyButton(
                    iconTint = findContrastTextColor(category.color.toComposeColor()),
                    iconStart = getCustomIconIdS(category.icon, R.drawable.ic_custom_category_s),
                    text = category.name,
                    backgroundGradient = Gradient.solid(category.color.toComposeColor()),
                    textStyle = Typo.caption.style(
                        color = findContrastTextColor(category.color.toComposeColor()),
                        fontWeight = FontWeight.ExtraBold
                    ),
                    padding = 8.dp,
                    iconEdgePadding = 10.dp
                ) {
                    ivyContext.navigateTo(
                        Screen.ItemStatistic(
                            accountId = null,
                            categoryId = category.id
                        )
                    )
                }

                Spacer(Modifier.width(12.dp))
            }

            val account = accounts.find { it.id == plannedPayment.accountId }
            IvyButton(
                backgroundGradient = Gradient.solid(IvyTheme.colors.pure),
                text = account?.name ?: "deleted",
                iconTint = IvyTheme.colors.pureInverse,
                iconStart = getCustomIconIdS(account?.icon, R.drawable.ic_custom_account_s),
                textStyle = Typo.caption.style(
                    color = IvyTheme.colors.pureInverse,
                    fontWeight = FontWeight.ExtraBold
                ),
                padding = 8.dp,
                iconEdgePadding = 10.dp
            ) {
                account?.let {
                    ivyContext.navigateTo(
                        Screen.ItemStatistic(
                            accountId = account.id,
                            categoryId = null
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun RuleTextRow(
    oneTime: Boolean,
    startDate: LocalDateTime?,
    intervalN: Int?,
    intervalType: IntervalType?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(24.dp))

        if (oneTime) {
            Text(
                text = "PLANNED FOR ",
                style = Typo.numberCaption.style(
                    color = Orange,
                    fontWeight = FontWeight.SemiBold
                )
            )
            Text(
                modifier = Modifier.padding(bottom = 1.dp),
                text = startDate?.toLocalDate()?.formatDateOnlyWithYear()?.uppercaseLocal()
                    ?: "null",
                style = Typo.numberCaption.style(
                    color = Orange,
                    fontWeight = FontWeight.ExtraBold
                )
            )
        } else {
            val startDateFormatted = startDate?.toLocalDate()?.formatDateOnly()?.uppercaseLocal()
            Text(
                text = "STARTS $startDateFormatted ",
                style = Typo.numberCaption.style(
                    color = Orange,
                    fontWeight = FontWeight.SemiBold
                )
            )
            val intervalTypeFormatted = intervalType?.forDisplay(intervalN ?: 0)?.uppercaseLocal()
            Text(
                modifier = Modifier.padding(bottom = 1.dp),
                text = "REPEATS EVERY $intervalN $intervalTypeFormatted",
                style = Typo.numberCaption.style(
                    color = Orange,
                    fontWeight = FontWeight.ExtraBold
                )
            )
        }

        Spacer(Modifier.width(24.dp))

    }
}

@Preview
@Composable
private fun Preview_oneTime() {
    IvyAppPreview {
        LazyColumn(Modifier.fillMaxSize()) {
            val cash = Account(name = "Cash")
            val food = Category(name = "Food")

            item {
                Spacer(Modifier.height(68.dp))

                PlannedPaymentCard(
                    baseCurrency = "BGN",
                    categories = listOf(food),
                    accounts = listOf(cash),
                    plannedPayment = PlannedPaymentRule(
                        accountId = cash.id,
                        title = "Lidl pazar",
                        categoryId = food.id,
                        amount = 250.75,
                        startDate = timeNowUTC().plusDays(5),
                        oneTime = true,
                        intervalType = null,
                        intervalN = null,
                        type = TransactionType.EXPENSE
                    )
                ) {

                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview_recurring() {
    IvyAppPreview {
        LazyColumn(Modifier.fillMaxSize()) {
            val account = Account(name = "Revolut")
            val shisha = Category(name = "Shisha", color = Orange.toArgb())

            item {
                Spacer(Modifier.height(68.dp))

                PlannedPaymentCard(
                    baseCurrency = "BGN",
                    categories = listOf(shisha),
                    accounts = listOf(account),
                    plannedPayment = PlannedPaymentRule(
                        accountId = account.id,
                        title = "Tabu",
                        categoryId = shisha.id,
                        amount = 250.75,
                        startDate = timeNowUTC().plusDays(5),
                        oneTime = false,
                        intervalType = IntervalType.MONTH,
                        intervalN = 1,
                        type = TransactionType.EXPENSE
                    )
                ) {

                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview_recurringError() {
    IvyAppPreview {
        LazyColumn(Modifier.fillMaxSize()) {
            val account = Account(name = "Revolut")
            val shisha = Category(name = "Shisha", color = Orange.toArgb())

            item {
                Spacer(Modifier.height(68.dp))

                PlannedPaymentCard(
                    baseCurrency = "BGN",
                    categories = listOf(shisha),
                    accounts = listOf(account),
                    plannedPayment = PlannedPaymentRule(
                        accountId = account.id,
                        title = "Tabu",
                        categoryId = shisha.id,
                        amount = 250.75,
                        startDate = timeNowUTC().plusDays(5),
                        oneTime = false,
                        intervalType = null,
                        intervalN = null,
                        type = TransactionType.EXPENSE
                    )
                ) {

                }
            }
        }
    }
}
