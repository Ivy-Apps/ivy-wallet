package com.ivy.wallet.ui.loan

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.insets.systemBarsPadding
import com.ivy.design.api.navigation
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.wallet.R
import com.ivy.wallet.base.format
import com.ivy.wallet.base.onScreenStart
import com.ivy.wallet.logic.model.CreateLoanData
import com.ivy.wallet.model.LoanType
import com.ivy.wallet.model.entity.Loan
import com.ivy.wallet.ui.IvyAppPreview
import com.ivy.wallet.ui.LoanDetails
import com.ivy.wallet.ui.Loans
import com.ivy.wallet.ui.loan.data.DisplayLoan
import com.ivy.wallet.ui.theme.*
import com.ivy.wallet.ui.theme.components.*
import com.ivy.wallet.ui.theme.modal.LoanModal
import com.ivy.wallet.ui.theme.modal.LoanModalData

@Composable
fun BoxWithConstraintsScope.LoansScreen(screen: Loans) {
    val viewModel: LoanViewModel = viewModel()

    val baseCurrency by viewModel.baseCurrencyCode.collectAsState()
    val loans by viewModel.loans.collectAsState()

    onScreenStart {
        viewModel.start()
    }

    UI(
        baseCurrency = baseCurrency,
        loans = loans,

        onCreateLoan = viewModel::createLoan,
        onEditLoan = {
            //do nothing, it shouldn't be done from that screen
        },
        onReorder = viewModel::reorder
    )
}

@Composable
private fun BoxWithConstraintsScope.UI(
    baseCurrency: String,
    loans: List<DisplayLoan>,

    onCreateLoan: (CreateLoanData) -> Unit = {},
    onEditLoan: (Loan) -> Unit = {},
    onReorder: (List<DisplayLoan>) -> Unit = {}
) {
    var reorderModalVisible by remember { mutableStateOf(false) }
    var loanModalData: LoanModalData? by remember { mutableStateOf(null) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .verticalScroll(rememberScrollState()),
    ) {
        Spacer(Modifier.height(32.dp))

        Toolbar(
            setReorderModalVisible = {
                reorderModalVisible = it
            }
        )

        Spacer(Modifier.height(8.dp))

        val nav = navigation()
        for (item in loans) {
            Spacer(Modifier.height(16.dp))

            LoanItem(
                displayLoan = item,
                baseCurrency = baseCurrency
            ) {
                nav.navigateTo(
                    screen = LoanDetails(
                        loanId = item.loan.id
                    )
                )
            }
        }

        if (loans.isEmpty()) {
            Spacer(Modifier.weight(1f))

            NoLoansEmptyState(
                emptyStateTitle = "No loans",
                emptyStateText = "You don't have any loans.\n" +
                        "Tap the \"+ Add loan\" to add one."
            )

            Spacer(Modifier.weight(1f))
        }

        Spacer(Modifier.height(150.dp))  //scroll hack
    }

    val nav = navigation()
    LoanBottomBar(
        onAdd = {
            loanModalData = LoanModalData(
                loan = null,
                baseCurrency = baseCurrency
            )
        },
        onClose = {
            nav.back()
        },
    )

    ReorderModalSingleType(
        visible = reorderModalVisible,
        initialItems = loans,
        dismiss = {
            reorderModalVisible = false
        },
        onReordered = onReorder
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
        modal = loanModalData,
        onCreateLoan = onCreateLoan,
        onEditLoan = onEditLoan,
        dismiss = {
            loanModalData = null
        }
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
                text = "Loans",
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
    baseCurrency: String,
    onClick: () -> Unit
) {
    val loan = displayLoan.loan
    val contrastColor = findContrastTextColor(loan.color.toComposeColor())

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(Shapes.rounded16)
            .border(2.dp, UI.colors.medium, Shapes.rounded16)
            .testTag("loan_item")
            .clickable(
                onClick = onClick
            )
    ) {
        LoanHeader(
            displayLoan = displayLoan,
            baseCurrency = baseCurrency,
            contrastColor = contrastColor,
        )

        Spacer(Modifier.height(12.dp))

        LoanInfo(
            displayLoan = displayLoan,
            baseCurrency = baseCurrency
        )

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun LoanHeader(
    displayLoan: DisplayLoan,
    baseCurrency: String,
    contrastColor: Color,
) {
    val loan = displayLoan.loan

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(loan.color.toComposeColor(), Shapes.rounded16Top)
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
            currency = baseCurrency,
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
    displayLoan: DisplayLoan,
    baseCurrency: String
) {
    val amountPaid = displayLoan.amountPaid
    val loanAmount = displayLoan.loan.amount
    val percentPaid = amountPaid / loanAmount

    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        text = "${amountPaid.format(baseCurrency)} $baseCurrency / ${loanAmount.format(baseCurrency)} $baseCurrency (${
            percentPaid.times(
                100
            ).format(2)
        }%)",
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
        percent = percentPaid
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
    IvyAppPreview {
        UI(
            baseCurrency = "BGN",
            loans = listOf(
                DisplayLoan(
                    loan = Loan(
                        name = "Loan 1",
                        icon = "rocket",
                        color = Red.toArgb(),
                        amount = 5000.0,
                        type = LoanType.BORROW
                    ),
                    amountPaid = 0.0
                ),
                DisplayLoan(
                    loan = Loan(
                        name = "Loan 2",
                        icon = "atom",
                        color = Orange.toArgb(),
                        amount = 252.36,
                        type = LoanType.BORROW
                    ),
                    amountPaid = 124.23
                ),
                DisplayLoan(
                    loan = Loan(
                        name = "Loan 3",
                        icon = "bank",
                        color = Blue.toArgb(),
                        amount = 7000.0,
                        type = LoanType.LEND
                    ),
                    amountPaid = 8000.0
                ),
            ),

            onCreateLoan = {},
            onEditLoan = {},
            onReorder = {}
        )
    }
}