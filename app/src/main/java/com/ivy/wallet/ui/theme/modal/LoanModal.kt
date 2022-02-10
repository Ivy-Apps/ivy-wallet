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
    val autoFocusKeyboard: Boolean = true,
    val autoOpenAmountModal: Boolean = false,
    val id: UUID = UUID.randomUUID()
)

@Composable
fun BoxWithConstraintsScope.LoanModal(
    accounts: List<Account> = emptyList(),
    selectedAccount: Account? = null,
    onSelectedAccount: (Account) -> Unit = {},
    onCreateAccount: (CreateAccountData) -> Unit = {},
    createLoanTransaction: Boolean = true,
    onLoanTransactionChecked: (Boolean) -> Unit = { _ -> },
    onBaseCurrencyChanged: (String) -> Unit = {},

    baseCurrencyCode: String,
    modal: LoanModalData?,
    onCreateLoan: (CreateLoanData, Account?) -> Unit,
    onEditLoan: (Loan) -> Unit,
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
    var currencyCode by remember(selectedAccount, modal) {
        Log.d("GGGG", "New Acc " + selectedAccount?.currency)
        mutableStateOf(modal?.baseCurrency ?:selectedAccount?.currency ?: "")
    }
    var amountModalVisible by remember { mutableStateOf(false) }
    var currencyModalVisible by remember { mutableStateOf(false) }
    var chooseIconModalVisible by remember(modal) {
        mutableStateOf(false)
    }

    var accountModalData: AccountModalData? by remember { mutableStateOf(null) }
    val accountToPass: State<Account?> =
        produceState(
            initialValue = selectedAccount,
            key1 = createLoanTransaction,
            key2 = selectedAccount
        ) {
            if (createLoanTransaction)
                this.value = selectedAccount
            else
                this.value = null
        }


    IvyModal(
        id = modal?.id,
        visible = modal != null,
        dismiss = dismiss,
        shiftIfKeyboardShown = false,
        PrimaryAction = {
            ModalAddSave(
                item = modal?.loan,
                enabled = nameTextFieldValue.text.isNotNullOrBlank() && amount > 0 && ((createLoanTransaction && selectedAccount != null) || !createLoanTransaction)
            ) {
                save(
                    loan = loan,
                    nameTextFieldValue = nameTextFieldValue,
                    type = type,
                    color = color,
                    icon = icon,
                    amount = amount,
                    selectedAccount = accountToPass.value,

                    onCreateLoan = onCreateLoan,
                    onEditLoan = onEditLoan,
                    dismiss = dismiss
                )
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

        if (createLoanTransaction) {
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
                selectedAccount = selectedAccount,
                onSelectedAccountChanged = onSelectedAccount,
                onAddNewAccount = {
                    accountModalData = AccountModalData(
                        account = null,
                        baseCurrency = selectedAccount?.currency ?: "USD",
                        balance = 0.0
                    )
                },
                childrenTestTag = "amount_modal_account"
            )

            Spacer(Modifier.height(16.dp))
        }

        IvyCheckboxWithText(
            modifier = Modifier
                .padding(start = 16.dp)
                .align(Alignment.Start),
            text = "Create a Main Transaction",
            checked = createLoanTransaction
        ) {
            onLoanTransactionChecked(it)
        }

        Spacer(modifier = Modifier.height(24.dp))

        ModalAmountSection(
            label = "ENTER LOAN AMOUNT",
            currency = baseCurrencyCode,
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
        currency = baseCurrencyCode,
        initialAmount = amount,
        dismiss = { amountModalVisible = false }
    ) { newAmount ->
        amount = newAmount
    }

    CurrencyModal(
        title = "Choose currency",
        initialCurrency = IvyCurrency.fromCode(baseCurrencyCode),
        visible = currencyModalVisible,
        dismiss = { currencyModalVisible = false }
    ) {
        onBaseCurrencyChanged(it)
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

    onCreateLoan: (CreateLoanData, Account?) -> Unit,
    onEditLoan: (Loan) -> Unit,
    dismiss: () -> Unit
) {
    if (loan != null) {
        onEditLoan(
            loan.copy(
                name = nameTextFieldValue.text.trim(),
                type = type,
                amount = amount,
                color = color.toArgb(),
                icon = icon
            )
        )
    } else {
        onCreateLoan(
            CreateLoanData(
                name = nameTextFieldValue.text.trim(),
                type = type,
                amount = amount,
                color = color,
                icon = icon,
            ),
            selectedAccount
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
            baseCurrencyCode = "BGN",
            onCreateLoan = { _, _ -> },
            onEditLoan = { }
        ) {

        }
    }
}