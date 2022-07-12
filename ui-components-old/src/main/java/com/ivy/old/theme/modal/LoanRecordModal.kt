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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.base.IvyWalletPreview
import com.ivy.base.R
import com.ivy.base.ivyWalletCtx
import com.ivy.data.Account
import com.ivy.data.getDefaultFIATCurrency
import com.ivy.data.loan.LoanRecord
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.frp.test.TestingContext
import com.ivy.frp.view.navigation.onScreenStart
import com.ivy.wallet.domain.deprecated.logic.model.CreateAccountData
import com.ivy.wallet.domain.deprecated.logic.model.CreateLoanRecordData
import com.ivy.wallet.domain.deprecated.logic.model.EditLoanRecordData
import com.ivy.wallet.ui.theme.components.ItemIconSDefaultIcon
import com.ivy.wallet.ui.theme.components.IvyCheckboxWithText
import com.ivy.wallet.ui.theme.components.IvyIcon
import com.ivy.wallet.ui.theme.components.IvyOutlinedButton
import com.ivy.wallet.ui.theme.findContrastTextColor
import com.ivy.wallet.ui.theme.modal.edit.AccountModal
import com.ivy.wallet.ui.theme.modal.edit.AccountModalData
import com.ivy.wallet.ui.theme.modal.edit.AmountModal
import com.ivy.wallet.ui.theme.toComposeColor
import com.ivy.wallet.utils.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.*

data class LoanRecordModalData(
    val loanRecord: LoanRecord?,
    val baseCurrency: String,
    val loanAccountCurrencyCode: String? = null,
    val selectedAccount: Account? = null,
    val createLoanRecordTransaction: Boolean = false,
    val isLoanInterest: Boolean = false,
    val id: UUID = UUID.randomUUID()
)

@Composable
fun BoxWithConstraintsScope.LoanRecordModal(
    modal: LoanRecordModalData?,
    accounts: List<Account> = emptyList(),
    onCreateAccount: (CreateAccountData) -> Unit = {},

    onCreate: (CreateLoanRecordData) -> Unit,
    onEdit: (EditLoanRecordData) -> Unit,
    onDelete: (LoanRecord) -> Unit,
    dismiss: () -> Unit
) {
    val initialRecord = modal?.loanRecord
    var noteTextFieldValue by remember(modal) {
        mutableStateOf(selectEndTextFieldValue(initialRecord?.note))
    }
    var currencyCode by remember(modal) {
        mutableStateOf(modal?.baseCurrency ?: "")
    }
    var amount by remember(modal) {
        mutableStateOf(modal?.loanRecord?.amount ?: 0.0)
    }
    var dateTime by remember(modal) {
        mutableStateOf(modal?.loanRecord?.dateTime ?: timeNowUTC())
    }
    var selectedAcc by remember(modal) {
        mutableStateOf(modal?.selectedAccount)
    }
    var createLoanRecordTrans by remember(modal) {
        mutableStateOf(modal?.createLoanRecordTransaction ?: false)
    }
    var loanInterest by remember(modal) {
        mutableStateOf(modal?.isLoanInterest ?: false)
    }
    var reCalculate by remember(modal) {
        mutableStateOf(false)
    }
    var reCalculateVisible by remember(modal) {
        mutableStateOf(modal?.loanAccountCurrencyCode != null && modal.loanAccountCurrencyCode != modal.baseCurrency)
    }

    var amountModalVisible by remember { mutableStateOf(false) }
    var deleteModalVisible by remember(modal) { mutableStateOf(false) }
    var accountModalData: AccountModalData? by remember { mutableStateOf(null) }
    var accountChangeConformationModal by remember { mutableStateOf(false) }

    IvyModal(
        id = modal?.id,
        visible = modal != null,
        dismiss = dismiss,
        shiftIfKeyboardShown = true,
        PrimaryAction = {
            ModalAddSave(
                item = initialRecord,
                enabled = amount > 0 && selectedAcc != null
                //enabled = amount > 0 && ((createLoanRecordTrans && selectedAcc != null) || !createLoanRecordTrans)
            ) {
                accountChangeConformationModal =
                    initialRecord != null && modal.selectedAccount != null
                            && modal.baseCurrency != currencyCode && currencyCode != modal.loanAccountCurrencyCode

                if (!accountChangeConformationModal)
                    save(
                        loanRecord = initialRecord,
                        noteTextFieldValue = noteTextFieldValue,
                        amount = amount,
                        dateTime = dateTime,
                        loanRecordInterest = loanInterest,
                        selectedAccount = selectedAcc,
                        createLoanRecordTransaction = createLoanRecordTrans,
                        reCalculateAmount = reCalculate,

                        onCreate = onCreate,
                        onEdit = onEdit,
                        dismiss = dismiss,
                    )
            }
        }
    ) {
        onScreenStart {
            if (modal?.loanRecord == null) {
                amountModalVisible = true
            }
        }

        Spacer(Modifier.height(32.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            ModalTitle(
                text = if (initialRecord != null) stringResource(R.string.edit_record) else stringResource(
                    R.string.new_record
                )
            )

            if (initialRecord != null) {
                Spacer(Modifier.weight(1f))

                ModalDelete {
                    deleteModalVisible = true
                }

                Spacer(Modifier.width(24.dp))
            }
        }

        Spacer(Modifier.height(24.dp))

        ModalNameInput(
            hint = stringResource(R.string.note),
            autoFocusKeyboard = false,
            textFieldValue = noteTextFieldValue,
            setTextFieldValue = {
                noteTextFieldValue = it
            }
        )

        Spacer(Modifier.height(24.dp))

        DateTimeRow(
            dateTime = dateTime,
            onSetDateTime = {
                dateTime = it
            }
        )

        Spacer(Modifier.height(24.dp))

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
                currencyCode = it.currency ?: getDefaultFIATCurrency().currencyCode

                reCalculateVisible =
                    initialRecord?.convertedAmount != null && selectedAcc != null && currencyCode == modal.baseCurrency
                //Unchecks the Recalculate Option if Recalculate Checkbox is not visible
                reCalculate = !reCalculateVisible

                selectedAcc = it

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
            checked = createLoanRecordTrans
        ) {
            createLoanRecordTrans = it
        }

        IvyCheckboxWithText(
            modifier = Modifier
                .padding(start = 16.dp)
                .align(Alignment.Start),
            text = stringResource(R.string.mark_as_interest),
            checked = loanInterest
        ) {
            loanInterest = it
        }

        if (reCalculateVisible) {
            IvyCheckboxWithText(
                modifier = Modifier
                    .padding(start = 16.dp, end = 8.dp)
                    .align(Alignment.Start),
                text = stringResource(R.string.recalculate_amount_with_today_exchange_rates),
                checked = reCalculate
            ) {
                reCalculate = it
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        ModalAmountSection(
            label = stringResource(R.string.enter_record_amount_uppercase),
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

    DeleteModal(
        visible = deleteModalVisible,
        title = stringResource(R.string.confirm_deletion),
        description = stringResource(R.string.record_deletion_warning, noteTextFieldValue.text),
        dismiss = { deleteModalVisible = false }
    ) {
        if (initialRecord != null) {
            onDelete(initialRecord)
        }
        deleteModalVisible = false
        reCalculate = false
        dismiss()
    }

    AccountModal(
        modal = accountModalData,
        onCreateAccount = onCreateAccount,
        onEditAccount = { _, _ -> },
        dismiss = {
            accountModalData = null
        }
    )

    DeleteModal(
        visible = accountChangeConformationModal,
        title = stringResource(R.string.confirm_account_change),
        description = stringResource(R.string.account_change_warning),
        buttonText = stringResource(R.string.confirm),
        iconStart = R.drawable.ic_agreed,
        dismiss = {
            selectedAcc = modal?.selectedAccount ?: selectedAcc
            accountChangeConformationModal = false
        }
    ) {
        save(
            loanRecord = initialRecord,
            noteTextFieldValue = noteTextFieldValue,
            amount = amount,
            dateTime = dateTime,
            loanRecordInterest = loanInterest,
            selectedAccount = selectedAcc,
            createLoanRecordTransaction = createLoanRecordTrans,
            reCalculateAmount = reCalculate,

            onCreate = onCreate,
            onEdit = onEdit,
            dismiss = dismiss,
        )

        accountChangeConformationModal = false
    }
}

@Composable
private fun DateTimeRow(
    dateTime: LocalDateTime,
    onSetDateTime: (LocalDateTime) -> Unit
) {
    val ivyContext = ivyWalletCtx()

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(24.dp))

        IvyOutlinedButton(
            text = dateTime.formatNicely(),
            iconStart = R.drawable.ic_date
        ) {
            ivyContext.datePicker(
                initialDate = dateTime.convertUTCtoLocal().toLocalDate()
            ) {
                onSetDateTime(getTrueDate(it, dateTime.toLocalTime()))
            }
        }

        Spacer(Modifier.weight(1f))

        IvyOutlinedButton(
            text = dateTime.formatLocalTime(),
            iconStart = R.drawable.ic_date
        ) {
            ivyContext.timePicker {
                onSetDateTime(getTrueDate(dateTime.convertUTCtoLocal().toLocalDate(), it))
            }
        }

        Spacer(Modifier.width(24.dp))
    }
}

private fun save(
    loanRecord: LoanRecord?,
    noteTextFieldValue: TextFieldValue,
    amount: Double,
    dateTime: LocalDateTime,
    loanRecordInterest: Boolean = false,
    createLoanRecordTransaction: Boolean = false,
    selectedAccount: Account? = null,
    reCalculateAmount: Boolean = false,

    onCreate: (CreateLoanRecordData) -> Unit,
    onEdit: (EditLoanRecordData) -> Unit,
    dismiss: () -> Unit
) {
    if (loanRecord != null) {
        val record = loanRecord.copy(
            note = noteTextFieldValue.text.trim(),
            amount = amount,
            dateTime = dateTime,
            interest = loanRecordInterest,
            accountId = selectedAccount?.id
        )
        onEdit(
            EditLoanRecordData(
                newLoanRecord = record,
                originalLoanRecord = loanRecord,
                createLoanRecordTransaction = createLoanRecordTransaction,
                reCalculateLoanAmount = reCalculateAmount,
            )
        )
    } else {
        onCreate(
            CreateLoanRecordData(
                note = noteTextFieldValue.text.trim(),
                amount = amount,
                dateTime = dateTime,
                interest = loanRecordInterest,
                account = selectedAccount,
                createLoanRecordTransaction = createLoanRecordTransaction
            )
        )
    }

    dismiss()
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

    if (TestingContext.inTest) return //fix broken tests

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


@Preview
@Composable
private fun Preview() {
    IvyWalletPreview {
        LoanRecordModal(
            modal = LoanRecordModalData(
                loanRecord = null,
                baseCurrency = "BGN"
            ),
            onCreate = {},
            onEdit = {},
            onDelete = {}
        ) {

        }
    }
}