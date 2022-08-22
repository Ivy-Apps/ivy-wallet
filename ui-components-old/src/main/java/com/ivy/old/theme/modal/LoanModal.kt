package com.ivy.wallet.ui.theme.modal

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.base.R
import com.ivy.data.AccountOld
import com.ivy.data.IvyCurrency
import com.ivy.data.getDefaultFIATCurrency
import com.ivy.data.loan.Loan
import com.ivy.data.loan.LoanType
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.frp.test.TestingContext
import com.ivy.frp.view.navigation.onScreenStart
import com.ivy.wallet.domain.deprecated.logic.model.CreateAccountData
import com.ivy.wallet.domain.deprecated.logic.model.CreateLoanData
import com.ivy.wallet.ui.theme.*
import com.ivy.wallet.ui.theme.components.ItemIconSDefaultIcon
import com.ivy.wallet.ui.theme.components.IvyCheckboxWithText
import com.ivy.wallet.ui.theme.components.IvyColorPicker
import com.ivy.wallet.ui.theme.components.IvyIcon
import com.ivy.wallet.ui.theme.modal.edit.AccountModal
import com.ivy.wallet.ui.theme.modal.edit.AccountModalData
import com.ivy.wallet.ui.theme.modal.edit.AmountModal
import com.ivy.wallet.ui.theme.modal.edit.IconNameRow
import com.ivy.wallet.utils.isNotNullOrBlank
import com.ivy.wallet.utils.selectEndTextFieldValue
import com.ivy.wallet.utils.thenIf
import kotlinx.coroutines.launch
import java.util.*

data class LoanModalData(
    val loan: Loan?,
    val baseCurrency: String,
    val selectedAccount: AccountOld? = null,
    val autoFocusKeyboard: Boolean = true,
    val autoOpenAmountModal: Boolean = false,
    val createLoanTransaction: Boolean = false,
    val id: UUID = UUID.randomUUID()
)

@Composable
fun BoxWithConstraintsScope.LoanModal(
    accounts: List<AccountOld> = emptyList(),
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
                    loan != null && modal.selectedAccount != null && currencyCode != (modal.selectedAccount.currency
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
            text = if (modal?.loan != null) stringResource(R.string.edit_loan) else stringResource(R.string.new_loan),
        )

        Spacer(Modifier.height(24.dp))

        IconNameRow(
            hint = stringResource(R.string.loan_name),
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
            text = stringResource(R.string.associated_account),
            style = UI.typo.b2.style(
                color = UI.colors.pureInverse,
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
            text = stringResource(R.string.create_main_transaction),
            checked = createLoanTrans
        ) {
            createLoanTrans = it
        }

        Spacer(modifier = Modifier.height(24.dp))

        ModalAmountSection(
            label = stringResource(R.string.enter_loan_amount_uppercase),
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
        title = stringResource(R.string.choose_currency),
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
        title = stringResource(R.string.confirm_account_change),
        description = stringResource(R.string.confirm_account_change_warning),
        buttonText = stringResource(R.string.confirm),
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
    accounts: List<AccountOld>,
    selectedAccount: AccountOld?,
    childrenTestTag: String? = null,
    onSelectedAccountChanged: (AccountOld) -> Unit,
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
    account: AccountOld,
    selected: Boolean,
    testTag: String,
    onClick: () -> Unit
) {
    val accountColor = account.color.toComposeColor()
    val textColor =
        if (selected) findContrastTextColor(accountColor) else UI.colors.pureInverse

    Row(
        modifier = Modifier
            .clip(UI.shapes.rFull)
            .thenIf(!selected) {
                border(2.dp, UI.colors.medium, UI.shapes.rFull)
            }
            .thenIf(selected) {
                background(accountColor, UI.shapes.rFull)
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
            style = UI.typo.b2.style(
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
            .clip(UI.shapes.rFull)
            .border(2.dp, UI.colors.medium, UI.shapes.rFull)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(12.dp))

        IvyIcon(
            icon = R.drawable.ic_plus,
            tint = UI.colors.pureInverse
        )

        Spacer(Modifier.width(4.dp))

        Text(
            modifier = Modifier.padding(vertical = 10.dp),
            text = stringResource(R.string.add_account),
            style = UI.typo.b2.style(
                color = UI.colors.pureInverse,
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
        text = stringResource(R.string.loan_type),
        style = UI.typo.b2.style(
            color = UI.colors.pureInverse,
            fontWeight = FontWeight.ExtraBold
        )
    )

    Spacer(Modifier.height(16.dp))

    Row(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .fillMaxWidth()
            .background(UI.colors.medium, UI.shapes.r2),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(8.dp))

        SelectorButton(
            selected = type == LoanType.BORROW,
            label = stringResource(R.string.borrow_money)
        ) {
            onTypeSelected(LoanType.BORROW)
        }

        Spacer(Modifier.width(8.dp))

        SelectorButton(
            selected = type == LoanType.LEND,
            label = stringResource(R.string.lend_money)
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
            .clip(UI.shapes.rFull)
            .clickable {
                onClick()
            }
            .padding(vertical = 8.dp)
            .thenIf(selected) {
                background(GradientIvy.asHorizontalBrush(), UI.shapes.rFull)
            }
            .padding(vertical = 8.dp),
        text = label,
        style = UI.typo.b2.style(
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
    selectedAccount: AccountOld? = null,
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
    com.ivy.core.ui.temp.Preview {
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