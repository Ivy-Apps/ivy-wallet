package com.ivy.core.ui.transaction

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
import com.ivy.data.transaction.TrnType
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.color.Gradient
import com.ivy.design.l0_system.color.White
import com.ivy.design.l0_system.color.asBrush
import com.ivy.design.l1_buildingBlocks.Icon
import com.ivy.design.l1_buildingBlocks.SpacerHor
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l2_components.B1
import com.ivy.design.l2_components.CSecond
import com.ivy.design.l3_ivyComponents.button.ButtonFeeling
import com.ivy.design.l3_ivyComponents.button.ButtonSize
import com.ivy.design.l3_ivyComponents.button.ButtonVisibility
import com.ivy.design.l3_ivyComponents.button.IvyButton
import com.ivy.design.util.ComponentPreview

@Immutable
data class DueActions(
    val onSkip: (TransactionUi) -> Unit,
    val onPayGet: (TransactionUi) -> Unit,
)

@Composable
fun TransactionUi.Card(
    onClick: (TransactionUi) -> Unit,
    onAccountClick: (AccountUi) -> Unit,
    onCategoryClick: (CategoryUi) -> Unit,

    modifier: Modifier = Modifier,
    dueActions: DueActions? = null,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(UI.shapes.rounded)
            .background(UI.colors.medium, UI.shapes.rounded)
            .clickable(onClick = {
                onClick(this@Card)
            })
            .padding(all = 20.dp)
            .testTag("transaction_card")
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

// region Due Date ("DUE ON ...")
@Composable
private fun DueDate(time: TrnTimeUi) {
    if (time is TrnTimeUi.Due) {
        SpacerVer(height = 12.dp)
        time.dueOn.CSecond(
            color = if (time.upcoming) UI.colors.orange else UI.colors.red,
            fontWeight = FontWeight.Bold
        )
    }
}
//endregion

// region Title & Description
@Composable
private fun Title(
    title: String?,
    time: TrnTimeUi
) {
    if (title != null) {
        SpacerVer(height = if (time is TrnTimeUi.Due) 8.dp else 8.dp)
        title.B1(
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun Description(
    description: String?,
    title: String?
) {
    if (description != null) {
        SpacerVer(height = if (title != null) 4.dp else 8.dp)
        description.CSecond(
            color = UI.colors.neutral,
            fontWeight = FontWeight.Bold,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
    }
}
//endregion

// region TrnType & Amount
@Composable
private fun TrnValue(
    value: FormattedValue,
    type: TrnType,
    time: TrnTimeUi,
) {
    SpacerVer(height = if (time is TrnTimeUi.Due) 12.dp else 12.dp)
    Row(
        modifier = Modifier
            .testTag("type_amount_currency")
            .padding(horizontal = 4.dp), // additional padding to look better?
        verticalAlignment = Alignment.CenterVertically
    ) {
        TrnTypeIcon(type = type, time = time)
        SpacerHor(width = 12.dp)
        value.AmountCurrency(
            color = when (type) {
                TrnType.Income -> UI.colors.green
                TrnType.Expense -> when (time) {
                    is TrnTimeUi.Actual -> UI.colorsInverted.pure
                    is TrnTimeUi.Due -> if (time.upcoming) UI.colors.orange else UI.colors.red
                }
            }
        )
    }
}

@Composable
private fun TrnTypeIcon(
    type: TrnType,
    time: TrnTimeUi
) {
    data class StyledIcon(
        @DrawableRes
        val iconId: Int,
        val bgColor: Brush,
        val tint: Color,
    )

    val style = when (type) {
        TrnType.Income -> StyledIcon(
            iconId = R.drawable.ic_income,
            bgColor = UI.colors.green.asBrush(),
            tint = White
        )
        TrnType.Expense -> {
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

    style.iconId.Icon(
        modifier = Modifier
            .background(style.bgColor, UI.shapes.circle),
        tint = style.tint,
        contentDescription = "transactionType"
    )
}
// endregion

// region Due Payment CTAs
@Composable
private fun DuePaymentCTAs(
    time: TrnTimeUi,
    type: TrnType,
    onSkip: () -> Unit,
    onPayGet: () -> Unit,
) {
    if (time is TrnTimeUi.Due) {
        SpacerVer(height = 12.dp)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp), // additional padding to look better
            verticalAlignment = Alignment.CenterVertically
        ) {
            SkipButton(onClick = onSkip)
            SpacerHor(width = 12.dp)
            PayGetButton(type = type, onClick = onPayGet)
        }
    }
}

@Composable
private fun RowScope.SkipButton(
    onClick: () -> Unit
) {
    IvyButton(
        modifier = Modifier.weight(1f),
        size = ButtonSize.Big,
        visibility = ButtonVisibility.Medium,
        feeling = ButtonFeeling.Negative,
        text = stringResource(R.string.skip),
        icon = null,
        onClick = onClick,
    )
}

@Composable
private fun RowScope.PayGetButton(
    type: TrnType,
    onClick: () -> Unit
) {
    val isIncome = type == TrnType.Income
    IvyButton(
        modifier = Modifier.weight(1f),
        size = ButtonSize.Big,
        visibility = ButtonVisibility.High,
        feeling = ButtonFeeling.Positive,
        text = stringResource(if (isIncome) R.string.get else R.string.pay),
        icon = null,
        onClick = onClick,
    )
}
// endregion


// region Previews
@Preview
@Composable
private fun Preview_Expense() {
    ComponentPreview {
        dummyTransactionUi(
            type = TrnType.Expense,
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
            type = TrnType.Income,
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
            type = TrnType.Expense,
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
            dueActions = DueActions({}, {})
        )
    }
}
// endregion