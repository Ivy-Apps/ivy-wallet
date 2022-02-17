package com.ivy.wallet.ui.theme.modal

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.wallet.R
import com.ivy.wallet.base.*
import com.ivy.wallet.logic.model.CreateAccountData
import com.ivy.wallet.logic.model.CreateLoanData
import com.ivy.wallet.model.IvyCurrency
import com.ivy.wallet.model.LoanType
import com.ivy.wallet.model.entity.Account
import com.ivy.wallet.model.entity.Loan
import com.ivy.wallet.ui.IvyAppPreview
import com.ivy.wallet.ui.theme.*
import com.ivy.wallet.ui.theme.components.*
import com.ivy.wallet.ui.theme.modal.edit.AccountModal
import com.ivy.wallet.ui.theme.modal.edit.AccountModalData
import com.ivy.wallet.ui.theme.modal.edit.AmountModal
import com.ivy.wallet.ui.theme.modal.edit.IconNameRow
import kotlinx.coroutines.launch
import java.util.*

data class LoanModalData(
    val loan: Loan?,
    val baseCurrency: String,
    val selectedAccount: Account? = null,
    val autoFocusKeyboard: Boolean = true,
    val autoOpenAmountModal: Boolean = false,
    val createLoanTransaction: Boolean = false,
    val id: UUID = UUID.randomUUID()
)

@Composable
fun BoxWithConstraintsScope.LoanModal(
    accounts: List<Account> = emptyList(),
    onCreateAccount: (CreateAccountData) -> Unit = {},

    modal: LoanModalData?,
    onCreateLoan: (CreateLoanData) -> Unit,
    onEditLoan: (Loan, Boolean) -> Unit,
    onPerformCalculations: () -> Unit = {},
    dismiss: () -> Unit,
) {
    val loan = modal?.loan
    var nameTextFieldValue by remember(modal) {
        mutableStateOf(selectEndTextFieldValue(loan?.name))
    }
    var type by remember(modal) {
        mutableStateOf(modal?.loan?.type ?: LoanType.BORROW)
    }
    var amount by remember(modal) {
        mutableStateOf(modal?.loan?.amount ?: 0.0)
    }
    var color by remember(modal) {
        mutableStateOf(loan?.color?.let { Color(it) } ?: Ivy)
    }
    var icon by remember(modal) {
        mutableStateOf(loan?.icon)
    }
    var currencyCode by remember(modal) {
        mutableStateOf(modal?.baseCurrency ?: "")
    }

    var selectedAcc by remember(modal) {
        mutableStateOf(modal?.selectedAccount)
    }

    var createLoanTrans by remember(modal) {
        mutableStateOf(modal?.createLoanTransaction ?: false)
    }

    var accountChangeModal by remember { mutableStateOf(false) }
    var amountModalVisible by remember { mutableStateOf(false) }
    var currencyModalVisible by remember { mutableStateOf(false) }
    var chooseIconModalVisible by remember(modal) {
        mutableStateOf(false)
    }

    var accountModalData: AccountModalData? by remember { mutableStateOf(null) }


    IvyModal(
        id = modal?.id,
        visible = modal != null,
        dismiss = dismiss,
        shiftIfKeyboardShown = false,
        PrimaryAction = {
            ModalAddSave(
                item = modal?.loan,
                //enabled = nameTextFieldValue.text.isNotNullOrBlank() && amount > 0 && ((createLoanTrans && selectedAcc != null) || !createLoanTrans)
                enabled = nameTextFieldValue.text.isNotNullOrBlank() && amount > 0 && selectedAcc != null
            ) {
                accountChangeModal =
                    modal?.selectedAccount != null && currencyCode != (modal.selectedAccount.currency
                        ?: modal.baseCurrency)

                if (!accountChangeModal) {
                    save(
                        loan = loan,
                        nameTextFieldValue = nameTextFieldValue,
                        type = type,
                        color = color,
                        icon = icon,
                        amount = amount,
                        selectedAccount = selectedAcc,
                        createLoanTransaction = createLoanTrans,

                        onCreateLoan = onCreateLoan,
                        onEditLoan = onEditLoan,
                        dismiss = dismiss
                    )
                }
            }
        }
    ) {
        onScreenStart {
            if (modal?.autoOpenAmountModal == true) {
                amountModalVisible = true
            }
        }

        Spacer(Modifier.height(32.dp))

        ModalTitle(
            text = if (modal?.loan != null) "Edit loan" else "New loan",
        )

        Spacer(Modifier.height(24.dp))

        IconNameRow(
            hint = "Loan name",
            defaultIcon = R.drawable.ic_custom_loan_m,
            color = color,
            icon = icon,

            autoFocusKeyboard = modal?.autoFocusKeyboard ?: true,

            nameTextFieldValue = nameTextFieldValue,
            setNameTextFieldValue = { nameTextFieldValue = it },
            showChooseIconModal = {
                chooseIconModalVisible = true
            }
        )

        Spacer(Modifier.height(24.dp))

        LoanTypePicker(
            type = type,
            onTypeSelected = { type = it }
        )

        Spacer(Modifier.height(24.dp))

        IvyColorPicker(
            selectedColor = color,
            onColorSelected = { color = it }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            modifier = Modifier.padding(horizontal = 32.dp),
            text = "Associated Account",
            style = Typo.body2.style(
                color = IvyTheme.colors.pureInverse,
                fontWeight = FontWeight.ExtraBold
            )
        )

        Spacer(Modifier.height(16.dp))

        AccountsRow(
            accounts = accounts,
            selectedAccount = selectedAcc,
            onSelectedAccountChanged = {
                selectedAcc = it
                currencyCode = it.currency ?: getDefaultFIATCurrency().currencyCode
            },
            onAddNewAccount = {
                accountModalData = AccountModalData(
                    account = null,
                    baseCurrency = selectedAcc?.currency ?: "USD",
                    balance = 0.0
                )
            },
            childrenTestTag = "amount_modal_account"
        )

        Spacer(Modifier.height(16.dp))

        IvyCheckboxWithText(
            modifier = Modifier
                .padding(start = 16.dp)
                .align(Alignment.Start),
            text = "Create a Main Transaction",
            checked = createLoanTrans
        ) {
            createLoanTrans = it
        }

        Spacer(modifier = Modifier.height(24.dp))

        ModalAmountSection(
            label = "ENTER LOAN AMOUNT",
            currency = currencyCode,
            amount = amount,
            amountPaddingTop = 40.dp,
            amountPaddingBottom = 40.dp,
        ) {
            amountModalVisible = true
        }
    }

    val amountModalId = remember(modal, amount) {
        UUID.randomUUID()
    }
    AmountModal(
        id = amountModalId,
        visible = amountModalVisible,
        currency = currencyCode,
        initialAmount = amount,
        dismiss = { amountModalVisible = false }
    ) { newAmount ->
        amount = newAmount
    }

    CurrencyModal(
        title = "Choose currency",
        initialCurrency = IvyCurrency.fromCode(currencyCode),
        visible = currencyModalVisible,
        dismiss = { currencyModalVisible = false }
    ) {
        currencyCode = it
    }

    AccountModal(
        modal = accountModalData,
        onCreateAccount = onCreateAccount,
        onEditAccount = { _, _ -> },
        dismiss = {
            accountModalData = null
        }
    )

    ChooseIconModal(
        visible = chooseIconModalVisible,
        initialIcon = icon ?: "loan",
        color = color,
        dismiss = { chooseIconModalVisible = false }
    ) {
        icon = it
    }

    DeleteModal(
        visible = accountChangeModal,
        title = "Confirm Account Change",
        description = "Note: You are trying to change the account associated with the loan with an account of different currency, " +
                "\nAll the loan records will be re-calculated based on today's exchanges rates ",
        buttonText = "Confirm",
        iconStart = R.drawable.ic_agreed,
        dismiss = {
            selectedAcc = modal?.selectedAccount ?: selectedAcc
            accountChangeModal = false
        }
    ) {
        onPerformCalculations()
        save(
            loan = loan,
            nameTextFieldValue = nameTextFieldValue,
            type = type,
            color = color,
            icon = icon,
            amount = amount,
            selectedAccount = selectedAcc,
            createLoanTransaction = createLoanTrans,

            onCreateLoan = onCreateLoan,
            onEditLoan = onEditLoan,
            dismiss = dismiss
        )
        accountChangeModal = false
    }
}

@Composable
private fun AccountsRow(
    modifier: Modifier = Modifier,
    accounts: List<Account>,
    selectedAccount: Account?,
    childrenTestTag: String? = null,
    onSelectedAccountChanged: (Account) -> Unit,
    onAddNewAccount: () -> Unit
) {
    val lazyState = rememberLazyListState()

    LaunchedEffect(accounts, selectedAccount) {
        if (selectedAccount != null) {
            val selectedIndex = accounts.indexOf(selectedAccount)
            if (selectedIndex != -1) {
                launch {
                    if (TestingContext.inTest) return@launch //breaks UI tests

                    lazyState.scrollToItem(
                        index = selectedIndex, //+1 because Spacer width 24.dp
                    )
                }
            }
        }
    }

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        state = lazyState
    ) {
        item {
            Spacer(Modifier.width(24.dp))
        }

        itemsIndexed(accounts) { _, account ->
            Account(
                account = account,
                selected = selectedAccount == account,
                testTag = childrenTestTag ?: "account"
            ) {
                onSelectedAccountChanged(account)
            }
        }

        item {
            AddAccount {
                onAddNewAccount()
            }
        }

        item {
            Spacer(Modifier.width(24.dp))
        }
    }
}

@Composable
private fun Account(
    account: Account,
    selected: Boolean,
    testTag: String,
    onClick: () -> Unit
) {
    val accountColor = account.color.toComposeColor()
    val textColor =
        if (selected) findContrastTextColor(accountColor) else IvyTheme.colors.pureInverse

    Row(
        modifier = Modifier
            .clip(Shapes.roundedFull)
            .thenIf(!selected) {
                border(2.dp, IvyTheme.colors.medium, Shapes.roundedFull)
            }
            .thenIf(selected) {
                background(accountColor, Shapes.roundedFull)
            }
            .clickable(onClick = onClick)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(12.dp))

        ItemIconSDefaultIcon(
            iconName = account.icon,
            defaultIcon = R.drawable.ic_custom_account_s,
            tint = textColor
        )

        Spacer(Modifier.width(4.dp))

        Text(
            modifier = Modifier.padding(vertical = 10.dp),
            text = account.name,
            style = Typo.body2.style(
                color = textColor,
                fontWeight = FontWeight.ExtraBold
            )
        )

        Spacer(Modifier.width(24.dp))
    }

    Spacer(Modifier.width(8.dp))
}

@Composable
private fun AddAccount(
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(Shapes.roundedFull)
            .border(2.dp, IvyTheme.colors.medium, Shapes.roundedFull)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(12.dp))

        IvyIcon(
            icon = R.drawable.ic_plus,
            tint = IvyTheme.colors.pureInverse
        )

        Spacer(Modifier.width(4.dp))

        Text(
            modifier = Modifier.padding(vertical = 10.dp),
            text = "Add account",
            style = Typo.body2.style(
                color = IvyTheme.colors.pureInverse,
                fontWeight = FontWeight.ExtraBold
            )
        )

        Spacer(Modifier.width(24.dp))
    }

    Spacer(Modifier.width(8.dp))
}

@Composable
private fun ColumnScope.LoanTypePicker(
    type: LoanType,
    onTypeSelected: (LoanType) -> Unit
) {
    Text(
        modifier = Modifier.padding(horizontal = 32.dp),
        text = "Loan type",
        style = Typo.body2.style(
            color = IvyTheme.colors.pureInverse,
            fontWeight = FontWeight.ExtraBold
        )
    )

    Spacer(Modifier.height(16.dp))

    Row(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .fillMaxWidth()
            .background(IvyTheme.colors.medium, Shapes.rounded24),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(8.dp))

        SelectorButton(
            selected = type == LoanType.BORROW,
            label = "Borrow money"
        ) {
            onTypeSelected(LoanType.BORROW)
        }

        Spacer(Modifier.width(8.dp))

        SelectorButton(
            selected = type == LoanType.LEND,
            label = "Lend money"
        ) {
            onTypeSelected(LoanType.LEND)
        }

        Spacer(Modifier.width(8.dp))
    }
}

@Composable
private fun RowScope.SelectorButton(
    selected: Boolean,
    label: String,
    onClick: () -> Unit
) {
    Text(
        modifier = Modifier
            .weight(1f)
            .clip(Shapes.roundedFull)
            .clickable {
                onClick()
            }
            .padding(vertical = 8.dp)
            .thenIf(selected) {
                background(GradientIvy.asHorizontalBrush(), Shapes.roundedFull)
            }
            .padding(vertical = 8.dp),
        text = label,
        style = Typo.body2.style(
            color = if (selected) White else Gray,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )
    )
}

private fun save(
    loan: Loan?,
    nameTextFieldValue: TextFieldValue,
    type: LoanType,
    color: Color,
    icon: String?,
    amount: Double,
    selectedAccount: Account? = null,
    createLoanTransaction: Boolean = false,

    onCreateLoan: (CreateLoanData) -> Unit,
    onEditLoan: (Loan, Boolean) -> Unit,
    dismiss: () -> Unit
) {
    if (loan != null) {
        onEditLoan(
            loan.copy(
                name = nameTextFieldValue.text.trim(),
                type = type,
                amount = amount,
                color = color.toArgb(),
                icon = icon,
                accountId = selectedAccount?.id
            ),
            createLoanTransaction
        )
    } else {
        onCreateLoan(
            CreateLoanData(
                name = nameTextFieldValue.text.trim(),
                type = type,
                amount = amount,
                color = color,
                icon = icon,
                account = selectedAccount,
                createLoanTransaction = createLoanTransaction
            )
        )
    }

    dismiss()
}


@Preview
@Composable
private fun Preview() {
    IvyAppPreview {
        LoanModal(
            modal = LoanModalData(
                loan = null,
                baseCurrency = "BGN",
            ),
            onCreateLoan = { },
            onEditLoan = { _, _ -> }
        ) {

        }
    }
}