package com.ivy.core.ui.transaction.item

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
import com.ivy.design.l0_system.UI
import com.ivy.design.l1_buildingBlocks.B2
import com.ivy.design.l1_buildingBlocks.CaptionSecond
import com.ivy.design.l1_buildingBlocks.SpacerHor
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l3_ivyComponents.Feeling
import com.ivy.design.l3_ivyComponents.Visibility
import com.ivy.design.l3_ivyComponents.button.ButtonSize
import com.ivy.design.l3_ivyComponents.button.IvyButton

/**
 * See TransactionUi.Card() and TrnListItemUi.Transfer.Card().
 */
@Composable
internal fun BaseTrnCard(
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
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("transaction_card"),
        content = content
    )
}

// region Due Date ("DUE ON ...")
@Composable
internal fun DueDate(time: TrnTimeUi) {
    if (time is TrnTimeUi.Due) {
        SpacerVer(height = 8.dp)
        CaptionSecond(
            text = time.dueOnDate,
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
        SpacerVer(height = if (time is TrnTimeUi.Due) 4.dp else 8.dp)
        B2(
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
        SpacerVer(height = if (title != null) 0.dp else 4.dp)
        CaptionSecond(
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
            .testTag("type_amount_currency"),
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}
// endregion

// region Due Payment CTAs
@Composable
internal fun DuePaymentCTAs(
    time: TrnTimeUi,
    cta: String,
    onSkip: () -> Unit,
    onExecute: () -> Unit,
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
            ExecutePaymentButton(cta = cta, onClick = onExecute)
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
        visibility = Visibility.Medium,
        feeling = Feeling.Negative,
        text = stringResource(R.string.skip),
        icon = null,
        onClick = onClick,
    )
}

@Composable
private fun RowScope.ExecutePaymentButton(
    cta: String,
    onClick: () -> Unit
) {
    IvyButton(
        modifier = Modifier.weight(1f),
        size = ButtonSize.Big,
        visibility = Visibility.High,
        feeling = Feeling.Positive,
        text = cta,
        icon = null,
        onClick = onClick,
    )
}
// endregion