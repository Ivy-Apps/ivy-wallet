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
import com.ivy.wallet.R
import com.ivy.wallet.ui.ivyWalletCtx
import com.ivy.wallet.ui.onboarding.model.TimePeriod
import com.ivy.wallet.ui.theme.*
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
            .border(2.dp, IvyTheme.colors.medium, Shapes.roundedFull),
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
                .clip(Shapes.roundedFull)
                .clickable {
                    onShowChoosePeriodModal()
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            IvyIcon(
                icon = R.drawable.ic_calendar,
                tint = IvyTheme.colors.pureInverse
            )

            Spacer(Modifier.width(4.dp))

            Text(
                text = period.toDisplayShort(ivyWalletCtx().startDayOfMonth),
                style = Typo.body2.style(
                    color = IvyTheme.colors.pureInverse,
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
    IvyComponentPreview {
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