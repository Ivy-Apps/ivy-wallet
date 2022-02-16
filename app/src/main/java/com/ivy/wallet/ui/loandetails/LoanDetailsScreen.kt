package com.ivy.wallet.ui.loandetails

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Divider
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.insets.statusBarsHeight
import com.ivy.wallet.R
import com.ivy.wallet.base.*
import com.ivy.wallet.logic.model.CreateAccountData
import com.ivy.wallet.logic.model.CreateLoanRecordData
import com.ivy.wallet.logic.model.EditLoanRecordData
import com.ivy.wallet.model.IvyCurrency
import com.ivy.wallet.model.LoanType
import com.ivy.wallet.model.TransactionType
import com.ivy.wallet.model.entity.Account
import com.ivy.wallet.model.entity.Loan
import com.ivy.wallet.model.entity.LoanRecord
import com.ivy.wallet.ui.IvyAppPreview
import com.ivy.wallet.ui.LocalIvyContext
import com.ivy.wallet.ui.Screen
import com.ivy.wallet.ui.loan.data.DisplayLoanRecord
import com.ivy.wallet.ui.statistic.level2.ItemStatisticToolbar
import com.ivy.wallet.ui.theme.*
import com.ivy.wallet.ui.theme.components.*
import com.ivy.wallet.ui.theme.modal.*
import com.ivy.wallet.ui.theme.transaction.TypeAmountCurrency
import java.util.*

@Composable
fun BoxWithConstraintsScope.LoanDetailsScreen(screen: Screen.LoanDetails) {
    val viewModel: LoanDetailsViewModel = viewModel()

    val baseCurrency by viewModel.baseCurrency.collectAsState()
    val loan by viewModel.loan.collectAsState()
    val displayLoanRecords by viewModel.displayLoanRecords.collectAsState()
    val amountPaid by viewModel.amountPaid.collectAsState()
    val loanAmountPaid by viewModel.loanAmountPaid.collectAsState()
    val accounts by viewModel.accounts.collectAsState()
    val selectedLoanAccount by viewModel.selectedLoanAccount.collectAsState()
    val createLoanTransaction by viewModel.createLoanTransaction.collectAsState()

    onScreenStart {
        viewModel.start(screen = screen)
    }

    UI(
        baseCurrency = baseCurrency,
        loan = loan,
        displayLoanRecords = displayLoanRecords,
        amountPaid = amountPaid,
        loanAmountPaid = loanAmountPaid,
        accounts = accounts,
        selectedLoanAccount = selectedLoanAccount,
        createLoanTransaction = createLoanTransaction,

        onEditLoan = viewModel::editLoan,
        onCreateLoanRecord = viewModel::createLoanRecord,
        onEditLoanRecord = viewModel::editLoanRecord,
        onDeleteLoanRecord = viewModel::deleteLoanRecord,
        onDeleteLoan = viewModel::deleteLoan,
        onCreateAccount = viewModel::createAccount
    )
}

@Composable
private fun BoxWithConstraintsScope.UI(
    baseCurrency: String,
    loan: Loan?,
    loanRecords: List<LoanRecord> = emptyList(),
    displayLoanRecords: List<DisplayLoanRecord> = emptyList(),
    amountPaid: Double,
    loanAmountPaid: Double = 0.0,

    accounts: List<Account> = emptyList(),
    selectedLoanAccount: Account? = null,
    createLoanTransaction: Boolean = false,

    onCreateAccount: (CreateAccountData) -> Unit = {},
    onEditLoan: (Loan, Boolean) -> Unit = { _, _ -> },
    onCreateLoanRecord: (CreateLoanRecordData) -> Unit = {},
    onEditLoanRecord: (EditLoanRecordData) -> Unit = {},
    onDeleteLoanRecord: (LoanRecord) -> Unit = {},
    onDeleteLoan: () -> Unit = {},
) {
    val itemColor = loan?.color?.toComposeColor() ?: Gray

    var deleteModalVisible by remember { mutableStateOf(false) }
    var loanModalData: LoanModalData? by remember { mutableStateOf(null) }
    var loanRecordModalData: LoanRecordModalData? by remember {
        mutableStateOf(null)
    }
    var waitModalVisible by remember(loan) { mutableStateOf(false) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(itemColor)
    ) {
        val listState = rememberLazyListState()

        Spacer(Modifier.statusBarsHeight())

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp)
                .clip(Shapes.rounded32Top)
                .background(IvyTheme.colors.pure),
            state = listState,
        ) {
            item {
                if (loan != null) {
                    Header(
                        loan = loan,
                        baseCurrency = baseCurrency,
                        amountPaid = amountPaid,
                        loanAmountPaid = loanAmountPaid,
                        itemColor = itemColor,
                        onAmountClick = {
                            loanModalData = LoanModalData(
                                loan = loan,
                                baseCurrency = baseCurrency,
                                autoFocusKeyboard = false,
                                autoOpenAmountModal = true,
                                selectedAccount = selectedLoanAccount,
                                createLoanTransaction = createLoanTransaction
                            )
                        },
                        onDeleteLoan = {
                            deleteModalVisible = true
                        },
                        onEditLoan = {
                            loanModalData = LoanModalData(
                                loan = loan,
                                baseCurrency = baseCurrency,
                                autoFocusKeyboard = false,
                                selectedAccount = selectedLoanAccount,
                                createLoanTransaction = createLoanTransaction
                            )
                        },
                        onAddRecord = {
                            loanRecordModalData = LoanRecordModalData(
                                loanRecord = null,
                                baseCurrency = baseCurrency,
                                selectedAccount = selectedLoanAccount
                            )
                        }
                    )
                }
            }

            item {
                //Rounded corners top effect
                Spacer(
                    Modifier
                        .height(32.dp)
                        .fillMaxWidth()
                        .background(itemColor) //itemColor is displayed below the clip
                        .background(IvyTheme.colors.pure, Shapes.rounded32Top)
                )
            }

            if (loan != null) {
                loanRecords(
                    loan = loan,
                    displayLoanRecords = displayLoanRecords,
                    onClick = { displayLoanRecord ->
                        loanRecordModalData = LoanRecordModalData(
                            loanRecord = displayLoanRecord.loanRecord,
                            baseCurrency = displayLoanRecord.loanRecordCurrencyCode,
                            selectedAccount = displayLoanRecord.account,
                            createLoanRecordTransaction = displayLoanRecord.loanRecordTransaction,
                            isLoanInterest = displayLoanRecord.loanRecord.interest,
                            loanAccountCurrencyCode = displayLoanRecord.loanCurrencyCode
                        )
                    }
                )
            }

            if (displayLoanRecords.isEmpty()) {
                item {
                    NoLoanRecordsEmptyState()
                }
            }

            item {
                //scroll hack
                Spacer(Modifier.height(96.dp))
            }
        }
    }

    LoanModal(
        modal = loanModalData,
        onCreateLoan = {
            //do nothing
        },
        onEditLoan = onEditLoan,
        dismiss = {
            loanModalData = null
        },
        onCreateAccount = onCreateAccount,
        accounts = accounts,
        onPerformCalculations = {
            waitModalVisible = true
        }
    )

    LoanRecordModal(
        modal = loanRecordModalData,
        onCreate = onCreateLoanRecord,
        onEdit = onEditLoanRecord,
        onDelete = onDeleteLoanRecord,
        accounts = accounts,
        dismiss = {
            loanRecordModalData = null
        },
        onCreateAccount = onCreateAccount
    )

    DeleteModal(
        visible = deleteModalVisible,
        title = "Confirm deletion",
        description = "Note: Deleting this loan will remove it permanently and delete all associated loan records with it.",
        dismiss = { deleteModalVisible = false }
    ) {
        onDeleteLoan()
    }

    ConfirmationModal(
        title = "Confirm Account Change",
        description = "Please wait, re-calculating all loan records",
        visible = waitModalVisible
    )
}

@Composable
private fun Header(
    loan: Loan,
    baseCurrency: String,
    amountPaid: Double,
    loanAmountPaid: Double = 0.0,
    itemColor: Color,

    onAmountClick: () -> Unit,
    onEditLoan: () -> Unit,
    onDeleteLoan: () -> Unit,
    onAddRecord: () -> Unit
) {
    val contrastColor = findContrastTextColor(itemColor)

    val darkColor = isDarkColor(itemColor)
    setStatusBarDarkTextCompat(darkText = !darkColor)

    Column(
        modifier = Modifier.background(itemColor)
    ) {
        Spacer(Modifier.height(20.dp))

        ItemStatisticToolbar(
            contrastColor = contrastColor,
            onEdit = onEditLoan,
            onDelete = onDeleteLoan
        )

        Spacer(Modifier.height(24.dp))

        LoanItem(
            loan = loan,
            contrastColor = contrastColor,
        ) {
            onEditLoan()
        }

        BalanceRow(
            modifier = Modifier
                .padding(start = 32.dp)
                .testTag("loan_amount")
                .clickableNoIndication {
                    onAmountClick()
                },
            textColor = contrastColor,
            currency = baseCurrency,
            balance = loan.amount,
        )


        Spacer(Modifier.height(20.dp))

        LoanInfoCard(
            loan = loan,
            baseCurrency = baseCurrency,
            amountPaid = amountPaid,
            loanAmountPaid = loanAmountPaid,
            onAddRecord = onAddRecord
        )

        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun LoanItem(
    loan: Loan,
    contrastColor: Color,

    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(start = 22.dp)
            .clickableNoIndication {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        ItemIconMDefaultIcon(
            iconName = loan.icon,
            defaultIcon = R.drawable.ic_custom_loan_m,
            tint = contrastColor
        )

        Spacer(Modifier.width(8.dp))

        Text(
            modifier = Modifier.testTag("loan_name"),
            text = loan.name,
            style = Typo.body1.style(
                color = contrastColor,
                fontWeight = FontWeight.ExtraBold
            )
        )

        Spacer(Modifier.width(8.dp))

        Text(
            modifier = Modifier
                .align(Alignment.Bottom)
                .padding(bottom = 12.dp),
            text = loan.humanReadableType(),
            style = Typo.caption.style(
                color = loan.color.toComposeColor().dynamicContrast()
            )
        )
    }
}

@Composable
private fun LoanInfoCard(
    loan: Loan,
    baseCurrency: String,
    amountPaid: Double,
    loanAmountPaid: Double = 0.0,

    onAddRecord: () -> Unit
) {
    val backgroundColor = if (isDarkColor(loan.color))
        MediumBlack.copy(alpha = 0.9f) else MediumWhite.copy(alpha = 0.9f)

    val lineColor = if (isDarkColor(loan.color))
        MediumWhite.copy(alpha = 0.9f) else MediumBlack.copy(alpha = 0.9f)

    val contrastColor = findContrastTextColor(backgroundColor)

    val percentPaid = amountPaid / loan.amount

    val loanPercentPaid = loanAmountPaid / loan.amount

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .drawColoredShadow(
                color = backgroundColor,
                alpha = 0.1f
            )
            .background(backgroundColor, Shapes.rounded24),
    ) {
        Spacer(Modifier.height(24.dp))

        Text(
            modifier = Modifier.padding(horizontal = 24.dp),
            text = "Paid",
            style = Typo.caption.style(
                color = contrastColor,
                fontWeight = FontWeight.ExtraBold
            )
        )

        Spacer(Modifier.height(12.dp))

        Text(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .testTag("amount_paid"),
            text = "${amountPaid.format(baseCurrency)} / ${loan.amount.format(baseCurrency)}",
            style = Typo.numberBody1.style(
                color = contrastColor,
                fontWeight = FontWeight.ExtraBold
            )
        )
        Text(
            modifier = Modifier.padding(horizontal = 24.dp),
            text = IvyCurrency.fromCode(baseCurrency)?.name ?: "",
            style = Typo.body2.style(
                color = contrastColor,
                fontWeight = FontWeight.Normal
            )
        )

        Spacer(Modifier.height(12.dp))

        val leftToPay = loan.amount - amountPaid
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier
                    .testTag("percent_paid"),
                text = "${percentPaid.times(100).format(2)}%",
                style = Typo.numberBody1.style(
                    color = contrastColor,
                    fontWeight = FontWeight.ExtraBold
                )
            )

            Spacer(Modifier.width(8.dp))

            Text(
                modifier = Modifier
                    .testTag("left_to_pay"),
                text = "${leftToPay.format(baseCurrency)} $baseCurrency left",
                style = Typo.numberBody2.style(
                    color = Gray,
                    fontWeight = FontWeight.ExtraBold
                )
            )
        }

        Spacer(Modifier.height(8.dp))

        ProgressBar(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .padding(horizontal = 24.dp),
            notFilledColor = IvyTheme.colors.pure,
            percent = percentPaid
        )

        if (loanAmountPaid != 0.0) {

            Divider(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .height(1.dp)
                    .fillMaxWidth()
                    .background(lineColor)
            )

            Text(
                modifier = Modifier.padding(horizontal = 24.dp),
                text = "Loan Interest",
                style = Typo.caption.style(
                    color = contrastColor,
                    fontWeight = FontWeight.ExtraBold
                )
            )

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier
                        .testTag("loan_interest_percent_paid"),
                    text = "${loanPercentPaid.times(100).format(2)}%",
                    style = Typo.numberBody1.style(
                        color = contrastColor,
                        fontWeight = FontWeight.ExtraBold
                    )
                )

                Spacer(Modifier.width(8.dp))

                Text(
                    modifier = Modifier
                        .testTag("interest_paid"),
                    text = "${loanAmountPaid.format(baseCurrency)} $baseCurrency paid",
                    style = Typo.numberBody2.style(
                        color = Gray,
                        fontWeight = FontWeight.ExtraBold
                    )
                )
            }

            Spacer(Modifier.height(12.dp))

            ProgressBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
                    .padding(horizontal = 24.dp),
                notFilledColor = IvyTheme.colors.pure,
                percent = loanPercentPaid
            )
        }

        Spacer(Modifier.height(24.dp))

        IvyButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .align(Alignment.CenterHorizontally),
            text = "Add record",
            shadowAlpha = 0.1f,
            backgroundGradient = Gradient.solid(contrastColor),
            textStyle = Typo.body2.style(
                color = findContrastTextColor(contrastColor),
                fontWeight = FontWeight.Bold
            ),
            wrapContentMode = false
        ) {
            onAddRecord()
        }

        Spacer(Modifier.height(12.dp))
    }
}

fun LazyListScope.loanRecords(
    loanRecords: List<LoanRecord> = emptyList(),
    baseCurrency: String = "",
    loan: Loan,
    displayLoanRecords: List<DisplayLoanRecord> = emptyList(),

    onClick: (DisplayLoanRecord) -> Unit
) {
    items(items = displayLoanRecords) { displayLoanRecord ->
        LoanRecordItem(
            loan = loan,
            loanRecord = displayLoanRecord.loanRecord,
            baseCurrency = displayLoanRecord.loanRecordCurrencyCode,
            account = displayLoanRecord.account,
            loanBaseCurrency = displayLoanRecord.loanCurrencyCode
        ) {
            onClick(displayLoanRecord)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun LoanRecordItem(
    loan: Loan,
    loanRecord: LoanRecord,
    baseCurrency: String,
    loanBaseCurrency: String = "",
    account: Account? = null,
    onClick: () -> Unit
) {
    val ivyContext = LocalIvyContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(Shapes.rounded16)
            .clickable {
                onClick()
            }
            .background(IvyTheme.colors.medium, Shapes.rounded16)
            .testTag("loan_record_item")
    ) {

        if (account != null || loanRecord.interest) {
            Row(Modifier.padding(16.dp)) {
                if (account != null) {
                    IvyButton(
                        backgroundGradient = Gradient.solid(IvyTheme.colors.pure),
                        hasGlow = false,
                        iconTint = IvyTheme.colors.pureInverse,
                        text = account.name,
                        iconStart = getCustomIconIdS(
                            iconName = account.icon,
                            defaultIcon = R.drawable.ic_custom_account_s
                        ),
                        textStyle = Typo.caption.style(
                            color = IvyTheme.colors.pureInverse,
                            fontWeight = FontWeight.ExtraBold
                        ),
                        padding = 8.dp,
                        iconEdgePadding = 10.dp
                    ) {
                        ivyContext.navigateTo(
                            Screen.ItemStatistic(
                                accountId = account.id,
                                categoryId = null
                            )
                        )
                    }
                }

                if (loanRecord.interest) {
                    //Spacer(modifier = Modifier.width(8.dp))

                    val textIconColor = if (isDarkColor(loan.color)) MediumWhite else MediumBlack

                    IvyButton(
                        modifier = Modifier.padding(start = 8.dp),
                        backgroundGradient = Gradient.solid(loan.color.toComposeColor()),
                        hasGlow = false,
                        iconTint = textIconColor,
                        text = "Interest",
                        iconStart = getCustomIconIdS(
                            iconName = "currency",
                            defaultIcon = R.drawable.ic_currency
                        ),
                        textStyle = Typo.caption.style(
                            color = textIconColor,
                            fontWeight = FontWeight.ExtraBold
                        ),
                        padding = 8.dp,
                        iconEdgePadding = 10.dp
                    ) {
                        //do Nothing
                    }
                }
            }
        } else {
            Spacer(Modifier.height(20.dp))
        }

        Text(
            modifier = Modifier.padding(horizontal = 24.dp),
            text = loanRecord.dateTime.formatNicelyWithTime(
                noWeekDay = false
            ).uppercase(),
            style = Typo.numberCaption.style(
                color = Gray,
                fontWeight = FontWeight.Bold
            )
        )

        if (loanRecord.note.isNotNullOrBlank()) {
            Text(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                text = loanRecord.note!!,
                style = Typo.body1.style(
                    fontWeight = FontWeight.ExtraBold,
                    color = IvyTheme.colors.pureInverse
                )
            )
        }

        if (loanRecord.note.isNullOrEmpty())
            Spacer(Modifier.height(16.dp))

        TypeAmountCurrency(
            transactionType = if (loan.type == LoanType.LEND) TransactionType.INCOME else TransactionType.EXPENSE,
            dueDate = null,
            currency = baseCurrency,
            amount = loanRecord.amount
        )

        if (loanRecord.convertedAmount != null) {
            Text(
                modifier = Modifier.padding(start = 72.dp),
                text = loanRecord.convertedAmount.format(baseCurrency) + " $loanBaseCurrency",
                style = Typo.numberBody2.style(
                    color = Gray,
                    fontWeight = FontWeight.Normal
                )
            )
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun NoLoanRecordsEmptyState() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(32.dp))

        IvyIcon(
            icon = R.drawable.ic_notransactions,
            tint = Gray
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = "No records",
            style = Typo.body1.style(
                color = Gray,
                fontWeight = FontWeight.ExtraBold
            )
        )

        Spacer(Modifier.height(8.dp))

        Text(
            modifier = Modifier.padding(horizontal = 32.dp),
            text = "You don't have any records for this loan. Tap \"Add record\" to create one.",
            style = Typo.body2.style(
                color = Gray,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        )
    }

    Spacer(Modifier.height(96.dp))
}

@Preview
@Composable
private fun Preview_Empty() {
    IvyAppPreview {
        UI(
            baseCurrency = "BGN",
            loan = Loan(
                name = "Loan 1",
                amount = 4023.54,
                color = Red.toArgb(),
                type = LoanType.LEND
            ),
            loanRecords = emptyList(),
            amountPaid = 0.0
        )
    }
}

@Preview
@Composable
private fun Preview_Records() {
    IvyAppPreview {
        UI(
            baseCurrency = "BGN",
            loan = Loan(
                name = "Loan 1",
                amount = 4023.54,
                color = Red.toArgb(),
                type = LoanType.LEND
            ),
            displayLoanRecords = listOf(
                DisplayLoanRecord(
                    LoanRecord(
                        amount = 123.45,
                        dateTime = timeNowUTC().minusDays(1),
                        note = "Cash",
                        loanId = UUID.randomUUID()
                    )
                ),
                DisplayLoanRecord(
                    LoanRecord(
                        amount = 0.50,
                        dateTime = timeNowUTC().minusYears(1),
                        loanId = UUID.randomUUID()
                    )
                ),
                DisplayLoanRecord(
                    LoanRecord(
                        amount = 1000.00,
                        dateTime = timeNowUTC().minusMonths(1),
                        note = "Revolut",
                        loanId = UUID.randomUUID()
                    )
                ),
            ),
            amountPaid = 3821.00
        )
    }
}