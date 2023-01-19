package com.ivy.core.ui.transaction.item

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.core.domain.pure.format.ValueUi
import com.ivy.core.ui.R
import com.ivy.core.ui.account.AccountBadge
import com.ivy.core.ui.algorithm.trnhistory.data.TransactionUi
import com.ivy.core.ui.algorithm.trnhistory.data.dummyTransactionUi
import com.ivy.core.ui.category.CategoryBadge
import com.ivy.core.ui.data.CategoryUi
import com.ivy.core.ui.data.account.AccountUi
import com.ivy.core.ui.data.transaction.TrnTimeUi
import com.ivy.core.ui.data.transaction.dummyTrnTimeDueUi
import com.ivy.core.ui.value.AmountCurrency
import com.ivy.data.transaction.TransactionType
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.color.White
import com.ivy.design.l0_system.color.asBrush
import com.ivy.design.l1_buildingBlocks.IconRes
import com.ivy.design.l1_buildingBlocks.SpacerHor
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.util.ComponentPreview

@Composable
fun TransactionCard(
    trn: TransactionUi,
    modifier: Modifier = Modifier,
    onClick: (TransactionUi) -> Unit,
    onAccountClick: (AccountUi) -> Unit,
    onCategoryClick: (CategoryUi) -> Unit,
    onSkip: (TransactionUi) -> Unit,
    onExecute: (TransactionUi) -> Unit,
) {
    BaseTrnCard(
        modifier = modifier,
        onClick = { onClick(trn) }
    ) {
        IncomeExpenseHeader(
            account = trn.account,
            category = trn.category,
            onCategoryClick = onCategoryClick,
            onAccountClick = onAccountClick
        )
        DueDate(time = trn.time)
        Title(title = trn.title, time = trn.time)
        Description(description = trn.description, title = trn.title)
        TrnValue(type = trn.type, value = trn.value, time = trn.time)

        DuePaymentCTAs(
            time = trn.time,
            cta = when (trn.type) {
                TransactionType.Income -> stringResource(R.string.get)
                TransactionType.Expense -> stringResource(R.string.pay)
            },
            onSkip = { onSkip(trn) },
            onExecute = { onExecute(trn) }
        )
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
            CategoryBadge(
                category = it,
                onClick = { onCategoryClick(category) }
            )
            SpacerHor(width = 8.dp)
        }

        AccountBadge(
            account = account,
            background = UI.colors.pure,
            onClick = { onAccountClick(account) }
        )
    }
}
// endregion

// region TrnType & Amount
@Composable
private fun TrnValue(
    value: ValueUi,
    type: TransactionType,
    time: TrnTimeUi,
) {
    SpacerVer(height = 8.dp)
    TransactionCardAmountRow {
        TrnTypeIcon(type = type, time = time)
        SpacerHor(width = 12.dp)
        AmountCurrency(
            value = value,
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
                    is TrnTimeUi.Actual -> UI.colors.red.asBrush()
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
        TransactionCard(
            modifier = Modifier.padding(horizontal = 16.dp),
            trn = dummyTransactionUi(
                type = TransactionType.Expense,
                value = ValueUi(
                    amount = "0.34",
                    currency = "BGN"
                ),
                title = "Order food"
            ),
            onClick = {},
            onAccountClick = {},
            onCategoryClick = {},
            onExecute = {},
            onSkip = {}
        )
    }
}

@Preview
@Composable
private fun Preview_Income() {
    ComponentPreview {
        TransactionCard(
            modifier = Modifier.padding(horizontal = 16.dp),
            trn = dummyTransactionUi(
                type = TransactionType.Income,
                value = ValueUi(
                    amount = "1,005.00",
                    currency = "USD"
                ),
                title = "Income"
            ),
            onClick = {},
            onAccountClick = {},
            onCategoryClick = {},
            onExecute = {},
            onSkip = {}
        )
    }
}

@Preview
@Composable
private fun Preview_UpcomingExpense() {
    ComponentPreview {
        TransactionCard(
            modifier = Modifier.padding(horizontal = 16.dp),
            trn = dummyTransactionUi(
                type = TransactionType.Expense,
                value = ValueUi(
                    amount = "1,005.00",
                    currency = "USD"
                ),
                title = "Upcoming Expense",
                description = "Description",
                time = dummyTrnTimeDueUi(),
            ),
            onClick = {},
            onAccountClick = {},
            onCategoryClick = {},
            onExecute = {},
            onSkip = {},
        )
    }
}
// endregion