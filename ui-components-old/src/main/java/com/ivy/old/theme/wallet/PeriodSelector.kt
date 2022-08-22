package com.ivy.wallet.ui.theme.wallet


import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.base.R
import com.ivy.core.ui.temp.trash.TimePeriod
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.wallet.ui.theme.components.IvyIcon

@Composable
fun PeriodSelector(
    modifier: Modifier = Modifier,
    period: TimePeriod,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onShowChoosePeriodModal: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .border(2.dp, UI.colors.medium, UI.shapes.rFull),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(20.dp))

        if (period.month != null) {
            IvyIcon(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .clickable {
                        onPreviousMonth()
                    }
                    .padding(all = 8.dp)
                    .rotate(-180f),
                icon = R.drawable.ic_arrow_right
            )
        }

        Spacer(Modifier.weight(1f))

        Row(
            modifier = Modifier
                .height(48.dp)
                .defaultMinSize(minWidth = 48.dp)
                .clip(UI.shapes.rFull)
                .clickable {
                    onShowChoosePeriodModal()
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            IvyIcon(
                icon = R.drawable.ic_calendar,
                tint = UI.colors.pureInverse
            )

            Spacer(Modifier.width(4.dp))

            Text(
                text = period.toDisplayShort(com.ivy.core.ui.temp.ivyWalletCtx().startDayOfMonth),
                style = UI.typo.b2.style(
                    color = UI.colors.pureInverse,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Spacer(Modifier.weight(1f))

        if (period.month != null) {
            IvyIcon(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .clickable {
                        onNextMonth()
                    }
                    .padding(all = 8.dp),
                icon = R.drawable.ic_arrow_right
            )
        }

        Spacer(Modifier.width(20.dp))
    }
}

@Preview
@Composable
private fun Preview() {
    com.ivy.core.ui.temp.ComponentPreview {
        PeriodSelector(
            period = TimePeriod.currentMonth(
                startDayOfMonth = 1
            ), //preview
            onPreviousMonth = { },
            onNextMonth = { }
        ) {

        }
    }
}