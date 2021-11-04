package com.ivy.wallet.ui.home

import androidx.annotation.DrawableRes
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import com.ivy.wallet.R
import com.ivy.wallet.base.*
import com.ivy.wallet.model.TransactionType
import com.ivy.wallet.ui.LocalIvyContext
import com.ivy.wallet.ui.Screen
import com.ivy.wallet.ui.onboarding.model.TimePeriod
import com.ivy.wallet.ui.theme.*
import com.ivy.wallet.ui.theme.components.BalanceRow
import com.ivy.wallet.ui.theme.components.BalanceRowMini
import com.ivy.wallet.ui.theme.components.IvyIcon
import com.ivy.wallet.ui.theme.components.IvyOutlinedButton
import com.ivy.wallet.ui.theme.transaction.TransactionsDividerLine
import com.ivy.wallet.ui.theme.wallet.AmountCurrencyB1
import kotlin.math.absoluteValue
import kotlin.math.roundToInt


@ExperimentalAnimationApi
@Composable
internal fun HomeHeader(
    expanded: Boolean,
    name: String,
    period: TimePeriod,
    currency: String,
    balance: Double,
    bufferDiff: Double,
    monthlyIncome: Double,
    monthlyExpenses: Double,

    onShowMonthModal: () -> Unit,
    onOpenMoreMenu: () -> Unit,
    onBalanceClick: () -> Unit
) {
    val percentExpanded by animateFloatAsState(
        targetValue = if (expanded) 1f else 0f,
        animationSpec = springBounce(
            stiffness = Spring.StiffnessLow
        )
    )
    val percentExpandedAlpha by animateFloatAsState(
        targetValue = if (expanded) 1f else 0f,
        animationSpec = springBounce(
            stiffness = Spring.StiffnessMedium
        )
    )

    Spacer(Modifier.height(20.dp))

    HeaderStickyRow(
        percentExpanded = percentExpanded,
        name = name,
        period = period,
        currency = currency,
        balance = balance,

        onShowMonthModal = onShowMonthModal,
        onBalanceClick = onBalanceClick
    )

    Spacer(Modifier.height(16.dp))

    CashFlowInfo(
        percentExpanded = percentExpanded,
        percentExpandedAlpha = percentExpandedAlpha,
        period = period,
        currency = currency,
        balance = balance,
        bufferDiff = bufferDiff,
        monthlyIncome = monthlyIncome,
        monthlyExpenses = monthlyExpenses,

        onOpenMoreMenu = onOpenMoreMenu,
        onBalanceClick = onBalanceClick
    )

    if (percentExpanded < 0.5f) {
        TransactionsDividerLine(
            modifier = Modifier.alpha(1f - percentExpanded),
            paddingHorizontal = lerp(
                24.dp, 0.dp, (1f - percentExpanded).coerceIn(0f, 1f)
            )
        )
    }
}

@Composable
private fun HeaderStickyRow(
    percentExpanded: Float,
    name: String,
    period: TimePeriod,

    currency: String,
    balance: Double,

    onShowMonthModal: () -> Unit,
    onBalanceClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(24.dp))

        Box {
            Text(
                modifier = Modifier.alpha(percentExpanded),
                text = if (name.isNotNullOrBlank()) "Hi $name" else "Hi",
                style = Typo.body1.style(
                    fontWeight = FontWeight.ExtraBold,
                    color = IvyTheme.colors.pureInverse
                )
            )

            //Balance mini row
            if (percentExpanded < 1f) {
                BalanceRowMini(
                    modifier = Modifier
                        .alpha(1f - percentExpanded)
                        .clickableNoIndication {
                            onBalanceClick()
                        },
                    currency = currency,
                    balance = balance,
                    shortenBigNumbers = true
                )
            }

        }

        Spacer(Modifier.weight(1f))

        IvyOutlinedButton(
            iconStart = R.drawable.ic_calendar,
            text = period.toDisplayShort(LocalIvyContext.current.startDateOfMonth),
        ) {
            onShowMonthModal()
        }

        Spacer(Modifier.width(12.dp))

        Spacer(Modifier.width(40.dp)) //settings menu button spacer

        Spacer(Modifier.width(24.dp))
    }
}

@ExperimentalAnimationApi
@Composable
private fun CashFlowInfo(
    percentExpanded: Float,
    percentExpandedAlpha: Float,
    period: TimePeriod,
    currency: String,
    balance: Double,
    bufferDiff: Double,
    monthlyIncome: Double,
    monthlyExpenses: Double,

    onOpenMoreMenu: () -> Unit,
    onBalanceClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .layout { measurable, constraints ->
                val placealbe = measurable.measure(constraints)

                val height = placealbe.height * percentExpanded

                layout(placealbe.width, height.roundToInt()) {
                    placealbe.placeRelative(
                        x = 0,
                        y = -(placealbe.height * (1f - percentExpanded)).roundToInt()
                    )
                }
            }
            .alpha(percentExpandedAlpha)
    ) {
        BalanceRow(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .clickableNoIndication {
                    onBalanceClick()
                },
            currency = currency,
            balance = balance,
            shortenBigNumbers = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        IncomeExpenses(
            percentExpanded = percentExpanded,
            period = period,
            currency = currency,
            monthlyIncome = monthlyIncome,
            monthlyExpenses = monthlyExpenses
        )


        val cashflow = monthlyIncome - monthlyExpenses
        if (cashflow != 0.0) {
            Spacer(Modifier.height(12.dp))

            Text(
                modifier = Modifier.padding(
                    start = 24.dp
                ),
                text = "Cashflow: ${if (cashflow > 0) "+" else ""}${cashflow.format(currency)} $currency",
                style = Typo.numberBody2.style(
                    color = if (cashflow < 0) Gray else Green
                )
            )

            Spacer(Modifier.height(4.dp))
        } else {
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun IncomeExpenses(
    percentExpanded: Float,
    period: TimePeriod,
    currency: String,
    monthlyIncome: Double,
    monthlyExpenses: Double
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(16.dp))

        val ivyContext = LocalIvyContext.current

        HeaderCard(
            percentVisible = percentExpanded,
            icon = R.drawable.ic_income,
            backgroundGradient = GradientGreen,
            textColor = White,
            label = "Income",
            currency = currency,
            amount = monthlyIncome
        ) {
            ivyContext.navigateTo(
                Screen.PieChartStatistic(
                    type = TransactionType.INCOME,
                )
            )
        }

        Spacer(Modifier.width(12.dp))

        HeaderCard(
            percentVisible = percentExpanded,
            icon = R.drawable.ic_expense,
            backgroundGradient = Gradient(IvyTheme.colors.pureInverse, IvyTheme.colors.gray),
            textColor = IvyTheme.colors.pure,
            label = "Expenses",
            currency = currency,
            amount = monthlyExpenses.absoluteValue
        ) {
            ivyContext.navigateTo(
                Screen.PieChartStatistic(
                    type = TransactionType.EXPENSE,
                )
            )
        }

        Spacer(Modifier.width(16.dp))
    }
}

@Composable
private fun RowScope.HeaderCard(
    @DrawableRes icon: Int,
    backgroundGradient: Gradient,
    percentVisible: Float,
    textColor: Color,
    label: String,
    currency: String,
    amount: Double,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .thenIf(percentVisible == 1f) {
                drawColoredShadow(backgroundGradient.startColor)
            }
            .clip(Shapes.rounded16)
            .background(backgroundGradient.asHorizontalBrush())
            .clickable(
                onClick = onClick
            )
    ) {
        Spacer(Modifier.height(12.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.width(16.dp))

            IvyIcon(
                icon = icon,
                tint = textColor
            )

            Spacer(Modifier.width(4.dp))

            Text(
                text = label,
                style = Typo.caption.style(
                    color = textColor,
                    fontWeight = FontWeight.ExtraBold
                )
            )
        }

        Spacer(Modifier.height(4.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(Modifier.width(20.dp))

            AmountCurrencyB1(
                amount = amount,
                currency = currency,
                textColor = textColor,
                shortenBigNumbers = true
            )

            Spacer(Modifier.width(4.dp))
        }

        Spacer(Modifier.height(20.dp))
    }
}