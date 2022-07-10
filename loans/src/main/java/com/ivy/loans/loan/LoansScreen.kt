package com.ivy.wallet.ui.loan

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.ivy.base.IvyWalletPreview
import com.ivy.base.R
import com.ivy.base.humanReadableType
import com.ivy.data.getDefaultFIATCurrency
import com.ivy.data.loan.Loan
import com.ivy.data.loan.LoanType
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.frp.view.navigation.navigation
import com.ivy.frp.view.navigation.onScreenStart
import com.ivy.screens.LoanDetails
import com.ivy.screens.Loans
import com.ivy.wallet.ui.loan.data.DisplayLoan
import com.ivy.wallet.ui.theme.*
import com.ivy.wallet.ui.theme.components.*
import com.ivy.wallet.ui.theme.modal.LoanModal

@Composable
fun BoxWithConstraintsScope.LoansScreen(screen: Loans) {
    val viewModel: LoanViewModel = viewModel()

    val state by viewModel.state.collectAsState()

    onScreenStart {
        viewModel.start()
    }

    UI(
        onEventHandler = viewModel::onEvent,
        state = state
    )
}

@Composable
private fun BoxWithConstraintsScope.UI(
    onEventHandler: (LoanScreenEvent) -> Unit = {},
    state: LoanScreenState = LoanScreenState()
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
            }
        )

        Spacer(Modifier.height(8.dp))

        for (item in state.loans) {
            Spacer(Modifier.height(16.dp))

            LoanItem(
                displayLoan = item
            ) {
                nav.navigateTo(
                    screen = LoanDetails(
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

        Spacer(Modifier.height(150.dp))  //scroll hack
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
private fun Toolbar(
    setReorderModalVisible: (Boolean) -> Unit
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
    modifier: Modifier = Modifier,
    emptyStateTitle: String,
    emptyStateText: String,
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
        loans = listOf(
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
            )
        )
    )
    IvyWalletPreview {
        UI(
            state = state
        )
    }
}