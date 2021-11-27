package com.ivy.wallet.ui.loan

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.insets.systemBarsPadding
import com.ivy.wallet.R
import com.ivy.wallet.base.onScreenStart
import com.ivy.wallet.logic.model.CreateLoanData
import com.ivy.wallet.model.LoanType
import com.ivy.wallet.model.entity.Loan
import com.ivy.wallet.ui.IvyAppPreview
import com.ivy.wallet.ui.LocalIvyContext
import com.ivy.wallet.ui.Screen
import com.ivy.wallet.ui.theme.*
import com.ivy.wallet.ui.theme.components.IvyIcon
import com.ivy.wallet.ui.theme.components.ReorderButton
import com.ivy.wallet.ui.theme.components.ReorderModalSingleType
import com.ivy.wallet.ui.theme.modal.LoanModal
import com.ivy.wallet.ui.theme.modal.LoanModalData

@Composable
fun BoxWithConstraintsScope.LoanScreen(screen: Screen.Loan) {
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
    loans: List<Loan>,

    onCreateLoan: (CreateLoanData) -> Unit = {},
    onEditLoan: (Loan) -> Unit = {},
    onReorder: (List<Loan>) -> Unit = {}
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

        for (item in loans) {
            Spacer(Modifier.height(24.dp))

            LoanItem(
                loan = item,
                baseCurrency = baseCurrency
            ) {
                TODO("Handle loan item click")
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

    val ivyContext = LocalIvyContext.current
    LoanBottomBar(
        onAdd = {
            loanModalData = LoanModalData(
                loan = null,
                baseCurrency = baseCurrency
            )
        },
        onClose = {
            ivyContext.back()
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
            text = item.name,
            style = Typo.body1.style(
                color = IvyTheme.colors.pureInverse,
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
                style = Typo.h2.style(
                    color = IvyTheme.colors.pureInverse,
                    fontWeight = FontWeight.ExtraBold
                )
            )

            //TODO: How much do you owe in total & how much do you have to get in total
//            if (categoryBudgetsTotal > 0 || appBudgetMax > 0) {
//                Spacer(Modifier.height(4.dp))
//
//                val categoryBudgetText = if (categoryBudgetsTotal > 0) {
//                    "${categoryBudgetsTotal.format(baseCurrency)} $baseCurrency for categories"
//                } else ""
//
//                val appBudgetMaxText = if (appBudgetMax > 0) {
//                    "${appBudgetMax.format(baseCurrency)} $baseCurrency app budget"
//                } else ""
//
//                val hasBothBudgetTypes =
//                    categoryBudgetText.isNotBlank() && appBudgetMaxText.isNotBlank()
//                Text(
//                    modifier = Modifier.testTag("budgets_info_text"),
//                    text = if (hasBothBudgetTypes)
//                        "Budget info: $categoryBudgetText / $appBudgetMaxText" else "Budget info: $categoryBudgetText$appBudgetMaxText",
//                    style = Typo.numberCaption.style(
//                        color = Gray,
//                        fontWeight = FontWeight.ExtraBold
//                    )
//                )
//            }

        }

        ReorderButton {
            setReorderModalVisible(true)
        }

        Spacer(Modifier.width(24.dp))
    }
}

@Composable
private fun LoanItem(
    loan: Loan,
    baseCurrency: String,
    onClick: () -> Unit
) {
    //TODO: Display
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
            style = Typo.body1.style(
                color = Gray,
                fontWeight = FontWeight.ExtraBold
            )
        )

        Spacer(Modifier.height(8.dp))

        Text(
            modifier = Modifier.padding(horizontal = 32.dp),
            text = emptyStateText,
            style = Typo.body2.style(
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
                Loan(
                    name = "Loan 1",
                    icon = "rocket",
                    color = Red.toArgb(),
                    amount = 5000.0,
                    type = LoanType.BORROW
                ),
                Loan(
                    name = "Loan 2",
                    icon = "atom",
                    color = Orange.toArgb(),
                    amount = 252.36,
                    type = LoanType.BORROW
                ),
                Loan(
                    name = "Loan 3",
                    icon = "bank",
                    color = Blue.toArgb(),
                    amount = 7000.0,
                    type = LoanType.LEND
                ),
            ),

            onCreateLoan = {},
            onEditLoan = {},
            onReorder = {}
        )
    }
}