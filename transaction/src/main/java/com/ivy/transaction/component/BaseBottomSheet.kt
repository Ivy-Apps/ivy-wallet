package com.ivy.transaction.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.design.l0_system.UI
import com.ivy.design.l1_buildingBlocks.H1
import com.ivy.design.l1_buildingBlocks.SpacerHor
import com.ivy.design.l1_buildingBlocks.SpacerWeight
import com.ivy.design.l3_ivyComponents.Feeling
import com.ivy.design.l3_ivyComponents.Visibility
import com.ivy.design.l3_ivyComponents.button.ButtonSize
import com.ivy.design.l3_ivyComponents.button.DeleteButton
import com.ivy.design.l3_ivyComponents.button.IvyButton
import com.ivy.design.util.IvyPreview
import com.ivy.design.util.keyboardShiftAnimated
import com.ivy.resources.R

@Composable
fun BoxScope.BaseBottomSheet(
    ctaText: String,
    @DrawableRes
    ctaIcon: Int,
    modifier: Modifier = Modifier,
    secondaryActions: (@Composable RowScope.() -> Unit)? = null,
    onCtaClick: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    val keyboardShiftDp by keyboardShiftAnimated()
    Column(
        modifier = modifier
            .align(Alignment.BottomCenter)
            .border(1.dp, UI.colors.neutral, UI.shapes.roundedTop)
            .background(UI.colors.pure, UI.shapes.roundedTop)
            .padding(bottom = 8.dp, top = 12.dp)
            .padding(bottom = keyboardShiftDp)
    ) {
        content()
        BottomBar(
            ctaText = ctaText,
            ctaIcon = ctaIcon,
            secondaryActions = secondaryActions,
            onCtaClick = onCtaClick,
        )
    }
}

@Composable
private fun BottomBar(
    modifier: Modifier = Modifier,
    ctaText: String,
    @DrawableRes
    ctaIcon: Int,
    secondaryActions: (@Composable RowScope.() -> Unit)?,
    onCtaClick: () -> Unit
) {
    val lineColor = UI.colors.medium
    Row(
        modifier = modifier
            .fillMaxWidth()
            .drawBehind {
                val height = this.size.height
                val width = this.size.width

                drawLine(
                    color = lineColor,
                    strokeWidth = 2.dp.toPx(),
                    start = Offset(x = 0f, y = height / 2),
                    end = Offset(x = width, y = height / 2)
                )
            }
            .padding(horizontal = 16.dp)
    ) {
        SpacerWeight(weight = 1f)
        secondaryActions?.invoke(this)
        IvyButton(
            size = ButtonSize.Small,
            visibility = Visibility.Focused,
            feeling = Feeling.Positive,
            text = ctaText,
            icon = ctaIcon,
            onClick = onCtaClick
        )
    }
}


@Preview
@Composable
private fun Preview() {
    IvyPreview {
        BaseBottomSheet(
            ctaText = "CTA",
            ctaIcon = R.drawable.ic_round_add_24,
            secondaryActions = {
                DeleteButton {

                }
                SpacerHor(width = 12.dp)
            },
            onCtaClick = {},
        ) {
            H1(text = "Content")
        }
    }
}