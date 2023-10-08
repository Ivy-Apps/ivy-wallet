package com.ivy.home

import androidx.annotation.DrawableRes
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.legacy.data.model.TimePeriod
import com.ivy.legacy.ivyWalletCtx
import com.ivy.legacy.ui.component.transaction.TransactionsDividerLine
import com.ivy.legacy.utils.clickableNoIndication
import com.ivy.legacy.utils.drawColoredShadow
import com.ivy.legacy.utils.format
import com.ivy.legacy.utils.horizontalSwipeListener
import com.ivy.legacy.utils.isNotNullOrBlank
import com.ivy.legacy.utils.springBounce
import com.ivy.legacy.utils.thenIf
import com.ivy.legacy.utils.verticalSwipeListener
import com.ivy.navigation.PieChartStatisticScreen
import com.ivy.navigation.navigation
import com.ivy.base.model.TransactionType
import com.ivy.resources.R
import com.ivy.wallet.ui.theme.Gradient
import com.ivy.wallet.ui.theme.GradientGreen
import com.ivy.wallet.ui.theme.Gray
import com.ivy.wallet.ui.theme.Green
import com.ivy.wallet.ui.theme.White
import com.ivy.wallet.ui.theme.components.BalanceRow
import com.ivy.wallet.ui.theme.components.BalanceRowMini
import com.ivy.wallet.ui.theme.components.IvyIcon
import com.ivy.wallet.ui.theme.components.IvyOutlinedButton
import com.ivy.wallet.ui.theme.wallet.AmountCurrencyB1
import kotlin.math.absoluteValue

private const val OverflowLengthOfBalance = 7
private const val OverflowLengthOfMontthRange = 12

@ExperimentalAnimationApi
@Composable
internal fun HomeHeader(
    expanded: Boolean,
    name: String,
    period: TimePeriod,
    currency: String,
    balance: Double,
    onShowMonthModal: () -> Unit,
    onBalanceClick: () -> Unit,
    onSelectNextMonth: () -> Unit,
    hideBalance: Boolean,
    onHiddenBalanceClick: () -> Unit,
    onSelectPreviousMonth: () -> Unit,
) {
    Column {
        val percentExpanded by animateFloatAsState(
            targetValue = if (expanded) 1f else 0f,
            animationSpec = springBounce(
                stiffness = Spring.StiffnessLow
            ),
            label = "Home Header Expand Collapse"
        )

        Spacer(Modifier.height(20.dp))

        HeaderStickyRow(
            percentExpanded = percentExpanded,
            name = name,
            period = period,
            currency = currency,
            balance = balance,
            hideBalance = hideBalance,

            onShowMonthModal = onShowMonthModal,
            onBalanceClick = onBalanceClick,
            onHiddenBalanceClick = onHiddenBalanceClick,
            onSelectNextMonth = onSelectNextMonth,
            onSelectPreviousMonth = onSelectPreviousMonth
        )

        Spacer(Modifier.height(16.dp))

        if (percentExpanded < 0.5f) {
            TransactionsDividerLine(
                modifier = Modifier.alpha(1f - percentExpanded),
                paddingHorizontal = 0.dp
            )
        }
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
    onSelectNextMonth: () -> Unit,
    hideBalance: Boolean,
    onHiddenBalanceClick: () -> Unit,
    onSelectPreviousMonth: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End,
    ) {
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterStart,
        ) {
            Text(
                modifier = Modifier
                    .alpha(percentExpanded)
                    .testTag("home_greeting_text"),
                text = if (name.isNotNullOrBlank()) {
                    stringResource(
                        R.string.hi_name,
                        name,
                    )
                } else {
                    stringResource(R.string.hi)
                },
                style = UI.typo.b1.style(
                    fontWeight = FontWeight.ExtraBold,
                    color = UI.colors.pureInverse,
                ),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )

            // Balance mini row
            if (percentExpanded < 1f) {
                val lengthOfCurrencyAndBalance = (currency + balance.toString()).length
                var lengthOfMonthRange = period.toDisplayShort(ivyWalletCtx().startDayOfMonth).length
                val overflow by remember(lengthOfCurrencyAndBalance, lengthOfMonthRange) {
                    derivedStateOf {
                        lengthOfCurrencyAndBalance >= OverflowLengthOfBalance &&
                                lengthOfMonthRange >= OverflowLengthOfMontthRange
                    }
                }

                BalanceRowMini(
                    modifier = Modifier
                        .alpha(alpha = 1f - percentExpanded)
                        .clickableNoIndication {
                            if (hideBalance) {
                                onHiddenBalanceClick()
                            } else {
                                onBalanceClick()
                            }
                        },
                    currency = currency,
                    balance = balance,
                    shortenBigNumbers = true,
                    hiddenMode = hideBalance,
                    overflow = overflow

                )
            }
        }

        IvyOutlinedButton(
            modifier = Modifier.horizontalSwipeListener(
                sensitivity = 75,
                onSwipeLeft = {
                    onSelectNextMonth()
                },
                onSwipeRight = {
                    onSelectPreviousMonth()
                },
            ),
            iconStart = R.drawable.ic_calendar,
            text = period.toDisplayShort(ivyWalletCtx().startDayOfMonth),
            minWidth = 130.dp,
        ) {
            onShowMonthModal()
        }

        Spacer(Modifier.width(12.dp))

        Spacer(Modifier.width(40.dp)) // settings menu button spacer
    }
}

@ExperimentalAnimationApi
@Composable
fun CashFlowInfo(
    currency: String,
    balance: Double,
    monthlyIncome: Double,
    monthlyExpenses: Double,
    hideBalance: Boolean,
    onOpenMoreMenu: () -> Unit,
    onBalanceClick: () -> Unit,
    percentExpanded: Float,
    onHiddenBalanceClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalSwipeListener(
                sensitivity = Constants.SWIPE_DOWN_THRESHOLD_OPEN_MORE_MENU,
                onSwipeDown = {
                    onOpenMoreMenu()
                },
            ),
    ) {
        BalanceRow(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .clickableNoIndication {
                    if (hideBalance) {
                        onHiddenBalanceClick()
                    } else {
                        onBalanceClick()
                    }
                }
                .testTag("home_balance"),
            currency = currency,
            balance = balance,
            shortenBigNumbers = true,
            hiddenMode = hideBalance
        )

        Spacer(modifier = Modifier.height(24.dp))

        IncomeExpenses(
            percentExpanded = percentExpanded,
            currency = currency,
            monthlyIncome = monthlyIncome,
            monthlyExpenses = monthlyExpenses,
        )

        val cashflow = monthlyIncome - monthlyExpenses
        if (cashflow != 0.0 && !hideBalance) {
            Spacer(Modifier.height(12.dp))

            Text(
                modifier = Modifier.padding(
                    start = 24.dp,
                ),
                text = stringResource(
                    R.string.cashflow,
                    (if (cashflow > 0) "+" else ""),
                    cashflow.format(currency),
                    currency,
                ),
                style = UI.typo.nB2.style(
                    color = if (cashflow < 0) Gray else Green,
                ),
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
    currency: String,
    monthlyIncome: Double,
    monthlyExpenses: Double,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(Modifier.width(16.dp))

        val nav = navigation()

        HeaderCard(
            percentVisible = percentExpanded,
            icon = R.drawable.ic_income,
            backgroundGradient = GradientGreen,
            textColor = White,
            label = stringResource(R.string.income),
            currency = currency,
            amount = monthlyIncome,
            testTag = "home_card_income",
        ) {
            nav.navigateTo(
                PieChartStatisticScreen(
                    type = TransactionType.INCOME,
                ),
            )
        }

        Spacer(Modifier.width(12.dp))

        HeaderCard(
            percentVisible = percentExpanded,
            icon = R.drawable.ic_expense,
            backgroundGradient = Gradient(UI.colors.pureInverse, UI.colors.gray),
            textColor = UI.colors.pure,
            label = stringResource(R.string.expenses),
            currency = currency,
            amount = monthlyExpenses.absoluteValue,
            testTag = "home_card_expense",
        ) {
            nav.navigateTo(
                PieChartStatisticScreen(
                    type = TransactionType.EXPENSE,
                ),
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
    testTag: String,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .thenIf(percentVisible == 1f) {
                drawColoredShadow(backgroundGradient.startColor)
            }
            .clip(UI.shapes.r4)
            .background(backgroundGradient.asHorizontalBrush())
            .testTag(testTag)
            .clickable(
                onClick = onClick,
            ),
    ) {
        Spacer(Modifier.height(12.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(Modifier.width(16.dp))

            IvyIcon(
                icon = icon,
                tint = textColor,
            )

            Spacer(Modifier.width(4.dp))

            Text(
                text = label,
                style = UI.typo.c.style(
                    color = textColor,
                    fontWeight = FontWeight.ExtraBold,
                ),
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
                shortenBigNumbers = true,
            )

            Spacer(Modifier.width(4.dp))
        }

        Spacer(Modifier.height(20.dp))
    }
}