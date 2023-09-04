package com.ivy.wallet.ui.balance

import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.frp.view.navigation.navigation
import com.ivy.wallet.R
import com.ivy.wallet.ui.BalanceScreen
import com.ivy.wallet.ui.IvyWalletPreview
import com.ivy.wallet.ui.main.FAB_BUTTON_SIZE
import com.ivy.wallet.ui.onboarding.model.TimePeriod
import com.ivy.wallet.ui.theme.Gradient
import com.ivy.wallet.ui.theme.Gray
import com.ivy.wallet.ui.theme.Orange
import com.ivy.wallet.ui.theme.White
import com.ivy.wallet.ui.theme.components.BalanceRow
import com.ivy.wallet.ui.theme.components.IvyCircleButton
import com.ivy.wallet.ui.theme.components.IvyDividerLine
import com.ivy.wallet.ui.theme.modal.ChoosePeriodModal
import com.ivy.wallet.ui.theme.modal.ChoosePeriodModalData
import com.ivy.wallet.ui.theme.wallet.PeriodSelector
import com.ivy.wallet.utils.format
import com.ivy.wallet.utils.onScreenStart

@Composable
fun BoxWithConstraintsScope.BalanceScreen(screen: BalanceScreen) {
    val viewModel: BalanceViewModel = viewModel()

    val period by viewModel.period.collectAsState()
    val baseCurrencyCode by viewModel.baseCurrencyCode.collectAsState()
    val currentBalance by viewModel.currentBalance.collectAsState()
    val plannedPaymentsAmount by viewModel.plannedPaymentsAmount.collectAsState()
    val balanceAfterPlannedPayments by viewModel.balanceAfterPlannedPayments.collectAsState()

    onScreenStart {
        viewModel.start()
    }

    UI(
        period = period,
        baseCurrencyCode = baseCurrencyCode,
        currentBalance = currentBalance,
        plannedPaymentsAmount = plannedPaymentsAmount,
        balanceAfterPlannedPayments = balanceAfterPlannedPayments,

        onSetPeriod = viewModel::setPeriod,
        onPreviousMonth = viewModel::previousMonth,
        onNextMonth = viewModel::nextMonth
    )
}

@Composable
private fun BoxWithConstraintsScope.UI(
    period: TimePeriod,

    baseCurrencyCode: String,
    currentBalance: Double,
    plannedPaymentsAmount: Double,
    balanceAfterPlannedPayments: Double,

    onSetPeriod: (TimePeriod) -> Unit = {},
    onPreviousMonth: () -> Unit = {},
    onNextMonth: () -> Unit = {}
) {
    var choosePeriodModal: ChoosePeriodModalData? by remember { mutableStateOf(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Spacer(Modifier.height(20.dp))

        PeriodSelector(
            period = period,
            onPreviousMonth = onPreviousMonth,
            onNextMonth = onNextMonth,
            onShowChoosePeriodModal = {
                choosePeriodModal = ChoosePeriodModalData(
                    period = period
                )
            }
        )

        Spacer(Modifier.height(32.dp))

        CurrentBalance(
            currency = baseCurrencyCode,
            currentBalance = currentBalance
        )

        Spacer(Modifier.height(32.dp))

        IvyDividerLine(
            modifier = Modifier
                .padding(horizontal = 24.dp)
        )

        Spacer(Modifier.height(40.dp))

        BalanceAfterPlannedPayments(
            currency = baseCurrencyCode,
            currentBalance = currentBalance,
            plannedPaymentsAmount = plannedPaymentsAmount,
            balanceAfterPlannedPayments = balanceAfterPlannedPayments
        )

        Spacer(Modifier.weight(1f))

        CloseButton()

        Spacer(Modifier.height(48.dp))
    }

    ChoosePeriodModal(
        modal = choosePeriodModal,
        dismiss = {
            choosePeriodModal = null
        }
    ) {
        onSetPeriod(it)
    }
}

@Composable
private fun ColumnScope.CurrentBalance(
    currency: String,
    currentBalance: Double
) {
    Text(
        modifier = Modifier.align(Alignment.CenterHorizontally),
        text = stringResource(R.string.current_balance),
        style = UI.typo.b2.style(
            color = Gray,
            fontWeight = FontWeight.ExtraBold
        )
    )

    Spacer(Modifier.height(4.dp))

    BalanceRow(
        modifier = Modifier.align(Alignment.CenterHorizontally),
        currency = currency,
        balance = currentBalance
    )
}

@Composable
private fun ColumnScope.BalanceAfterPlannedPayments(
    currency: String,
    currentBalance: Double,
    plannedPaymentsAmount: Double,
    balanceAfterPlannedPayments: Double
) {
    Text(
        modifier = Modifier
            .padding(horizontal = 32.dp),
        text = stringResource(R.string.balance_after_payments),
        style = UI.typo.b2.style(
            color = Orange,
            fontWeight = FontWeight.ExtraBold
        )
    )

    Spacer(Modifier.height(8.dp))

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(32.dp))

        BalanceRow(
            currency = currency,
            balance = balanceAfterPlannedPayments,

            integerFontSize = 30.sp,
            decimalFontSize = 18.sp,
            currencyFontSize = 18.sp,

            currencyUpfront = false
        )

        Spacer(Modifier.weight(1f))

        Column(
            horizontalAlignment = Alignment.End,
        ) {
            Spacer(Modifier.height(4.dp))

            Text(
                text = "${currentBalance.format(2)} $currency",
                style = UI.typo.nC.style(
                    color = UI.colors.pureInverse,
                    fontWeight = FontWeight.Normal
                )
            )

            Spacer(Modifier.height(2.dp))

            val plusSign = if (plannedPaymentsAmount >= 0) "+" else ""
            Text(
                text = "${plusSign}${plannedPaymentsAmount.format(2)} $currency",
                style = UI.typo.nC.style(
                    color = UI.colors.pureInverse,
                    fontWeight = FontWeight.ExtraBold
                )
            )
        }

        Spacer(Modifier.width(32.dp))
    }
}

@Composable
private fun ColumnScope.CloseButton() {
    val nav = navigation()
    IvyCircleButton(
        modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .size(FAB_BUTTON_SIZE)
            .rotate(45f)
            .zIndex(200f),
        backgroundPadding = 8.dp,
        icon = R.drawable.ic_add,
        backgroundGradient = Gradient.solid(Gray),
        hasShadow = false,
        tint = White
    ) {
        nav.back()
    }
}

@Preview
@Composable
private fun Preview() {
    IvyWalletPreview {
        UI(
            period = TimePeriod.currentMonth(
                startDayOfMonth = 1
            ), // preview
            baseCurrencyCode = "BGN",
            currentBalance = 9326.55,
            balanceAfterPlannedPayments = 8426.0,
            plannedPaymentsAmount = -900.55,

            onSetPeriod = {}
        )
    }
}
