package com.ivy.core.ui.transaction.card

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ivy.core.ui.R
import com.ivy.core.ui.data.transaction.TrnTimeUi
import com.ivy.data.transaction.TransactionType
import com.ivy.design.l0_system.UI
import com.ivy.design.l1_buildingBlocks.SpacerHor
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l2_components.B1
import com.ivy.design.l2_components.CSecond
import com.ivy.design.l3_ivyComponents.button.ButtonFeeling
import com.ivy.design.l3_ivyComponents.button.ButtonSize
import com.ivy.design.l3_ivyComponents.button.ButtonVisibility
import com.ivy.design.l3_ivyComponents.button.IvyButton

/**
 * See TransactionUi.Card() and TrnListItemUi.Transfer.Card().
 */
@Composable
internal fun TransactionCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(UI.shapes.rounded)
            .background(UI.colors.medium, UI.shapes.rounded)
            .clickable(onClick = onClick)
            .padding(all = 20.dp)
            .testTag("transaction_card"),
        content = content
    )
}

// region Due Date ("DUE ON ...")
@Composable
internal fun DueDate(time: TrnTimeUi) {
    if (time is TrnTimeUi.Due) {
        SpacerVer(height = 12.dp)
        CSecond(
            text = time.dueOn,
            color = if (time.upcoming) UI.colors.orange else UI.colors.red,
            fontWeight = FontWeight.Bold
        )
    }
}
//endregion

// region Title & Description
@Composable
internal fun Title(
    title: String?,
    time: TrnTimeUi
) {
    if (title != null) {
        SpacerVer(height = if (time is TrnTimeUi.Due) 8.dp else 8.dp)
        B1(
            text = title,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
internal fun Description(
    description: String?,
    title: String?
) {
    if (description != null) {
        SpacerVer(height = if (title != null) 4.dp else 8.dp)
        CSecond(
            text = description,
            color = UI.colors.neutral,
            fontWeight = FontWeight.Bold,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
    }
}
//endregion

// region Amount Row
@Composable
internal fun TransactionCardAmountRow(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier
            .testTag("type_amount_currency")
            .padding(horizontal = 4.dp), // additional padding to look better?
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}
// endregion

// region Due Payment CTAs
@Composable
internal fun DuePaymentCTAs(
    time: TrnTimeUi,
    type: TransactionType,
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
    type: TransactionType,
    onClick: () -> Unit
) {
    val isIncome = type == TransactionType.Income
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