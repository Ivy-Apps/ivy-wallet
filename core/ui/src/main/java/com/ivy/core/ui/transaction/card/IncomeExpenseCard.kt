package com.ivy.core.ui.transaction.card

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.core.domain.pure.format.FormattedValue
import com.ivy.core.ui.R
import com.ivy.core.ui.account.Badge
import com.ivy.core.ui.category.Badge
import com.ivy.core.ui.data.AccountUi
import com.ivy.core.ui.data.CategoryUi
import com.ivy.core.ui.data.transaction.TransactionUi
import com.ivy.core.ui.data.transaction.TrnTimeUi
import com.ivy.core.ui.data.transaction.dummyTransactionUi
import com.ivy.core.ui.data.transaction.dummyTrnTimeDueUi
import com.ivy.core.ui.value.AmountCurrency
import com.ivy.data.transaction.TransactionType
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.color.Gradient
import com.ivy.design.l0_system.color.White
import com.ivy.design.l0_system.color.asBrush
import com.ivy.design.l1_buildingBlocks.IconRes
import com.ivy.design.l1_buildingBlocks.SpacerHor
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.util.ComponentPreview

@Composable
fun TransactionUi.Card(
    onClick: (TransactionUi) -> Unit,
    onAccountClick: (AccountUi) -> Unit,
    onCategoryClick: (CategoryUi) -> Unit,

    modifier: Modifier = Modifier,
    dueActions: DueActions? = null,
) {
    TransactionCard(
        modifier = modifier,
        onClick = { onClick(this@Card) }
    ) {
        IncomeExpenseHeader(
            account = account,
            category = category,
            onCategoryClick = onCategoryClick,
            onAccountClick = onAccountClick
        )
        DueDate(time = time)
        Title(title = title, time = time)
        Description(description = description, title = title)
        TrnValue(type = type, value = value, time = time)

        if (dueActions != null) {
            DuePaymentCTAs(
                time = time,
                type = type,
                onSkip = {
                    dueActions.onSkip(this@Card)
                },
                onPayGet = {
                    dueActions.onPayGet(this@Card)
                }
            )
        }
    }
}

// region Transaction Header
@Composable
private fun IncomeExpenseHeader(
    account: AccountUi,
    category: CategoryUi?,
    onCategoryClick: (CategoryUi) -> Unit,
    onAccountClick: (AccountUi) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        category?.let {
            category.Badge(
                onClick = { onCategoryClick(category) }
            )
            SpacerHor(width = 12.dp)
        }

        account.Badge(
            background = UI.colors.pure,
            onClick = { onAccountClick(account) }
        )
    }
}
// endregion

// region TrnType & Amount
@Composable
private fun TrnValue(
    value: FormattedValue,
    type: TransactionType,
    time: TrnTimeUi,
) {
    SpacerVer(height = 12.dp)
    TransactionCardAmountRow {
        TrnTypeIcon(type = type, time = time)
        SpacerHor(width = 12.dp)
        value.AmountCurrency(
            color = when (type) {
                TransactionType.Income -> UI.colors.green
                TransactionType.Expense -> when (time) {
                    is TrnTimeUi.Actual -> UI.colorsInverted.pure
                    is TrnTimeUi.Due -> if (time.upcoming)
                        UI.colors.orange else UI.colors.red
                }
            }
        )
    }
}

@Composable
private fun TrnTypeIcon(
    type: TransactionType,
    time: TrnTimeUi
) {
    data class StyledIcon(
        @DrawableRes
        val iconId: Int,
        val bgColor: Brush,
        val tint: Color,
    )

    val style = when (type) {
        TransactionType.Income -> StyledIcon(
            iconId = R.drawable.ic_income,
            bgColor = UI.colors.green.asBrush(),
            tint = White
        )
        TransactionType.Expense -> {
            StyledIcon(
                iconId = R.drawable.ic_expense,
                bgColor = when (time) {
                    is TrnTimeUi.Actual -> Gradient(
                        start = UI.colors.red,
                        end = UI.colors.redP2,
                    ).asHorizontalBrush()
                    is TrnTimeUi.Due -> if (time.upcoming)
                        UI.colors.orange.asBrush() else UI.colors.red.asBrush()
                },
                tint = White
            )
        }
    }

    IconRes(
        modifier = Modifier
            .background(style.bgColor, UI.shapes.circle),
        icon = style.iconId,
        tint = style.tint,
        contentDescription = "transactionType"
    )
}
// endregion

// region Previews
@Preview
@Composable
private fun Preview_Expense() {
    ComponentPreview {
        dummyTransactionUi(
            type = TransactionType.Expense,
            value = FormattedValue(
                amount = "0.34",
                currency = "BGN"
            ),
            title = "Order food"
        ).Card(
            modifier = Modifier.padding(horizontal = 16.dp),
            onClick = {},
            onAccountClick = {},
            onCategoryClick = {}
        )
    }
}

@Preview
@Composable
private fun Preview_Income() {
    ComponentPreview {
        dummyTransactionUi(
            type = TransactionType.Income,
            value = FormattedValue(
                amount = "1,005.00",
                currency = "USD"
            ),
            title = "Income"
        ).Card(
            modifier = Modifier.padding(horizontal = 16.dp),
            onClick = {},
            onAccountClick = {},
            onCategoryClick = {}
        )
    }
}

@Preview
@Composable
private fun Preview_UpcomingExpense() {
    ComponentPreview {
        dummyTransactionUi(
            type = TransactionType.Expense,
            value = FormattedValue(
                amount = "1,005.00",
                currency = "USD"
            ),
            title = "Upcoming Expense",
            description = "Description",
            time = dummyTrnTimeDueUi(),
        ).Card(
            modifier = Modifier.padding(horizontal = 16.dp),
            onClick = {},
            onAccountClick = {},
            onCategoryClick = {},
            dueActions = dummyDueActions()
        )
    }
}
// endregion