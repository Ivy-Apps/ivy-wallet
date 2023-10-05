package com.ivy.loans.loan

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ivy.data.model.LoanType
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.legacy.IvyWalletPreview
import com.ivy.legacy.datamodel.Loan
import com.ivy.legacy.humanReadableType
import com.ivy.legacy.utils.getDefaultFIATCurrency
import com.ivy.loans.loan.data.DisplayLoan
import com.ivy.navigation.LoanDetailsScreen
import com.ivy.navigation.LoansScreen
import com.ivy.navigation.navigation
import com.ivy.resources.R
import com.ivy.wallet.ui.theme.Blue
import com.ivy.wallet.ui.theme.Gray
import com.ivy.wallet.ui.theme.Orange
import com.ivy.wallet.ui.theme.Red
import com.ivy.wallet.ui.theme.components.BalanceRow
import com.ivy.wallet.ui.theme.components.ItemIconSDefaultIcon
import com.ivy.wallet.ui.theme.components.IvyIcon
import com.ivy.wallet.ui.theme.components.ProgressBar
import com.ivy.wallet.ui.theme.components.ReorderButton
import com.ivy.wallet.ui.theme.components.ReorderModalSingleType
import com.ivy.wallet.ui.theme.dynamicContrast
import com.ivy.wallet.ui.theme.findContrastTextColor
import com.ivy.wallet.ui.theme.modal.LoanModal
import com.ivy.wallet.ui.theme.modal.LoanModalData
import com.ivy.wallet.ui.theme.toComposeColor
import kotlinx.collections.immutable.persistentListOf

@Composable
fun BoxWithConstraintsScope.LoansScreen(screen: LoansScreen) {
    val viewModel: LoanViewModel = viewModel()
    val state = viewModel.uiState()
    UI(
        state = state,
        onEventHandler = viewModel::onEvent
    )
}

@Composable
private fun BoxWithConstraintsScope.UI(
    state: LoanScreenState,
    onEventHandler: (LoanScreenEvent) -> Unit = {},
) {
    val nav = navigation()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .verticalScroll(rememberScrollState()),
    ) {
        Spacer(Modifier.height(32.dp))

        Toolbar(
            setReorderModalVisible = {
                onEventHandler.invoke(LoanScreenEvent.OnReOrderModalShow(show = it))
            },
            state.totalOweAmount,
            state.totalOwedAmount
        )

        Spacer(Modifier.height(8.dp))

        for (item in state.loans) {
            Spacer(Modifier.height(16.dp))

            LoanItem(
                displayLoan = item
            ) {
                nav.navigateTo(
                    screen = LoanDetailsScreen(
                        loanId = item.loan.id
                    )
                )
            }
        }

        if (state.loans.isEmpty()) {
            Spacer(Modifier.weight(1f))

            NoLoansEmptyState(
                emptyStateTitle = stringResource(R.string.no_loans),
                emptyStateText = stringResource(R.string.no_loans_description)
            )

            Spacer(Modifier.weight(1f))
        }

        Spacer(Modifier.height(150.dp)) // scroll hack
    }

    LoanBottomBar(
        onAdd = {
            onEventHandler.invoke(LoanScreenEvent.OnAddLoan)
        },
        onClose = {
            nav.back()
        },
    )

    ReorderModalSingleType(
        visible = state.reorderModalVisible,
        initialItems = state.loans,
        dismiss = {
            onEventHandler.invoke(LoanScreenEvent.OnReOrderModalShow(show = false))
        },
        onReordered = {
            onEventHandler.invoke(LoanScreenEvent.OnReordered(reorderedList = it))
        }
    ) { _, item ->
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 24.dp)
                .padding(vertical = 8.dp),
            text = item.loan.name,
            style = UI.typo.b1.style(
                color = UI.colors.pureInverse,
                fontWeight = FontWeight.Bold
            )
        )
    }

    LoanModal(
        accounts = state.accounts,
        onCreateAccount = {
            onEventHandler.invoke(LoanScreenEvent.OnCreateAccount(accountData = it))
        },
        modal = state.loanModalData,
        onCreateLoan = {
            onEventHandler.invoke(LoanScreenEvent.OnLoanCreate(createLoanData = it))
        },
        onEditLoan = { _, _ -> },
        dismiss = {
            onEventHandler.invoke(LoanScreenEvent.OnLoanModalDismiss)
        },
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun Toolbar(
    setReorderModalVisible: (Boolean) -> Unit,
    totalOweAmount: String,
    totalOwedAmount: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 24.dp, end = 16.dp)
        ) {
            Text(
                text = stringResource(R.string.loans),
                style = UI.typo.h2.style(
                    color = UI.colors.pureInverse,
                    fontWeight = FontWeight.ExtraBold
                )
            )
            if (totalOweAmount.isNotEmpty()) {
                Text(
                    text = "You Owe: $totalOweAmount",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
            }
            if (totalOwedAmount.isNotEmpty()) {
                Text(
                    text = "You're Owed: $totalOwedAmount",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        ReorderButton {
            setReorderModalVisible(true)
        }

        Spacer(Modifier.width(24.dp))
    }
}

@Composable
private fun LoanItem(
    displayLoan: DisplayLoan,
    onClick: () -> Unit
) {
    val loan = displayLoan.loan
    val contrastColor = findContrastTextColor(loan.color.toComposeColor())

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(UI.shapes.r4)
            .border(2.dp, UI.colors.medium, UI.shapes.r4)
            .testTag("loan_item")
            .clickable(
                onClick = onClick
            )
    ) {
        LoanHeader(
            displayLoan = displayLoan,
            contrastColor = contrastColor,
        )

        Spacer(Modifier.height(12.dp))

        LoanInfo(
            displayLoan = displayLoan
        )

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun LoanHeader(
    displayLoan: DisplayLoan,
    contrastColor: Color,
) {
    val loan = displayLoan.loan

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(loan.color.toComposeColor(), UI.shapes.r4Top)
    ) {
        Spacer(Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.width(20.dp))

            ItemIconSDefaultIcon(
                iconName = loan.icon,
                defaultIcon = R.drawable.ic_custom_loan_s,
                tint = contrastColor
            )

            Spacer(Modifier.width(8.dp))

            Text(
                text = loan.name,
                style = UI.typo.b1.style(
                    color = contrastColor,
                    fontWeight = FontWeight.ExtraBold
                )
            )
            Spacer(Modifier.width(8.dp))

            Text(
                modifier = Modifier
                    .align(Alignment.Bottom)
                    .padding(bottom = 4.dp),
                text = loan.humanReadableType(),
                style = UI.typo.c.style(
                    color = loan.color.toComposeColor().dynamicContrast()
                )
            )
        }

        Spacer(Modifier.height(4.dp))

        val leftToPay = loan.amount - displayLoan.amountPaid
        BalanceRow(
            modifier = Modifier
                .align(Alignment.CenterHorizontally),
            decimalPaddingTop = 7.dp,
            spacerDecimal = 6.dp,
            textColor = contrastColor,
            currency = displayLoan.currencyCode ?: getDefaultFIATCurrency().currencyCode,
            balance = leftToPay,

            integerFontSize = 30.sp,
            decimalFontSize = 18.sp,
            currencyFontSize = 30.sp,

            currencyUpfront = false
        )

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun ColumnScope.LoanInfo(
    displayLoan: DisplayLoan
) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        text = displayLoan.formattedDisplayText,
        style = UI.typo.nB2.style(
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    )

    Spacer(Modifier.height(12.dp))

    ProgressBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(24.dp)
            .padding(horizontal = 24.dp),
        notFilledColor = UI.colors.medium,
        percent = displayLoan.percentPaid
    )
}

@Composable
private fun NoLoansEmptyState(
    emptyStateTitle: String,
    emptyStateText: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(32.dp))

        IvyIcon(
            icon = R.drawable.ic_custom_loan_l,
            tint = Gray
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = emptyStateTitle,
            style = UI.typo.b1.style(
                color = Gray,
                fontWeight = FontWeight.ExtraBold
            )
        )

        Spacer(Modifier.height(8.dp))

        Text(
            modifier = Modifier.padding(horizontal = 32.dp),
            text = emptyStateText,
            style = UI.typo.b2.style(
                color = Gray,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        )

        Spacer(Modifier.height(96.dp))
    }
}

@Preview
@Composable
private fun Preview() {
    val state = LoanScreenState(
        baseCurrency = "BGN",
        loans = persistentListOf(
            DisplayLoan(
                loan = Loan(
                    name = "Loan 1",
                    icon = "rocket",
                    color = Red.toArgb(),
                    amount = 5000.0,
                    type = LoanType.BORROW
                ),
                amountPaid = 0.0,
                percentPaid = 0.4
            ),
            DisplayLoan(
                loan = Loan(
                    name = "Loan 2",
                    icon = "atom",
                    color = Orange.toArgb(),
                    amount = 252.36,
                    type = LoanType.BORROW
                ),
                amountPaid = 124.23,
                percentPaid = 0.2
            ),
            DisplayLoan(
                loan = Loan(
                    name = "Loan 3",
                    icon = "bank",
                    color = Blue.toArgb(),
                    amount = 7000.0,
                    type = LoanType.LEND
                ),
                amountPaid = 8000.0,
                percentPaid = 0.8
            ),
        ),
        accounts = persistentListOf(),
        totalOweAmount = "1000.00 INR",
        totalOwedAmount = "1500.0 INR",
        loanModalData = LoanModalData(loan = null, baseCurrency = "INR"),
        reorderModalVisible = false,
        selectedAccount = null
    )
    IvyWalletPreview {
        UI(
            state = state
        ) {}
    }
}
