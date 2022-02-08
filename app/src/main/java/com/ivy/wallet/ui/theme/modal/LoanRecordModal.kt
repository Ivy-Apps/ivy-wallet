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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.wallet.R
import com.ivy.wallet.base.*
import com.ivy.wallet.logic.model.CreateAccountData
import com.ivy.wallet.logic.model.CreateLoanRecordData
import com.ivy.wallet.model.entity.Account
import com.ivy.wallet.model.entity.LoanRecord
import com.ivy.wallet.ui.IvyAppPreview
import com.ivy.wallet.ui.LocalIvyContext
import com.ivy.wallet.ui.theme.*
import com.ivy.wallet.ui.theme.components.ItemIconSDefaultIcon
import com.ivy.wallet.ui.theme.components.IvyCheckboxWithText
import com.ivy.wallet.ui.theme.components.IvyIcon
import com.ivy.wallet.ui.theme.components.IvyOutlinedButton
import com.ivy.wallet.ui.theme.modal.edit.AccountModal
import com.ivy.wallet.ui.theme.modal.edit.AccountModalData
import com.ivy.wallet.ui.theme.modal.edit.AmountModal
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.*

data class LoanRecordModalData(
    val loanRecord: LoanRecord?,
    val baseCurrency: String,
    val id: UUID = UUID.randomUUID()
)

@Composable
fun BoxWithConstraintsScope.LoanRecordModal(
    modal: LoanRecordModalData?,
    accounts: List<Account> = emptyList(),
    loanRecordInterest: Boolean = false,
    selectedAccount: Account? = null,
    onSelectedAccount: (Account) -> Unit = {},
    onCreateAccount: (CreateAccountData) -> Unit = {},
    createLoanRecordTransaction: Boolean = true,
    onLoanRecordTransactionChecked: (Boolean) -> Unit = { _ -> },
    onLoanRecordInterestChecked: (Boolean) -> Unit = { _ -> },

    onCreate: (CreateLoanRecordData) -> Unit,
    onEdit: (LoanRecord) -> Unit,
    onDelete: (LoanRecord) -> Unit,
    dismiss: () -> Unit
) {
    val initialRecord = modal?.loanRecord
    var noteTextFieldValue by remember(modal) {
        mutableStateOf(selectEndTextFieldValue(initialRecord?.note))
    }
    val currencyCode by remember(modal) {
        mutableStateOf(modal?.baseCurrency ?: "")
    }
    var amount by remember(modal) {
        mutableStateOf(modal?.loanRecord?.amount ?: 0.0)
    }
    var dateTime by remember(modal) {
        mutableStateOf(modal?.loanRecord?.dateTime ?: timeNowUTC())
    }


    var amountModalVisible by remember { mutableStateOf(false) }
    var deleteModalVisible by remember(modal) { mutableStateOf(false) }
    var accountModalData: AccountModalData? by remember { mutableStateOf(null) }

    IvyModal(
        id = modal?.id,
        visible = modal != null,
        dismiss = dismiss,
        shiftIfKeyboardShown = true,
        PrimaryAction = {
            ModalAddSave(
                item = initialRecord,
                enabled = amount > 0
            ) {
                save(
                    loanRecord = initialRecord,
                    noteTextFieldValue = noteTextFieldValue,
                    amount = amount,
                    dateTime = dateTime,
                    loanRecordInterest = loanRecordInterest,

                    onCreate = onCreate,
                    onEdit = onEdit,
                    dismiss = dismiss
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
                text = if (initialRecord != null) "Edit record" else "New record"
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
            hint = "Note",
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

        if (createLoanRecordTransaction) {
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
            checked = createLoanRecordTransaction
        ) {
            onLoanRecordTransactionChecked(it)
        }

        //Spacer(Modifier.height(.dp))

        IvyCheckboxWithText(
            modifier = Modifier
                .padding(start = 16.dp)
                .align(Alignment.Start),
            text = "Mark as Interest",
            checked = loanRecordInterest
        ) {
            onLoanRecordInterestChecked(it)
        }

        Spacer(modifier = Modifier.height(32.dp))

        ModalAmountSection(
            label = "ENTER RECORD AMOUNT",
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
        title = "Confirm deletion",
        description = "Are you sure that you want to delete \"${noteTextFieldValue.text}\" record?",
        dismiss = { deleteModalVisible = false }
    ) {
        if (initialRecord != null) {
            onDelete(initialRecord)
        }
        deleteModalVisible = false
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
}

@Composable
private fun DateTimeRow(
    dateTime: LocalDateTime,
    onSetDateTime: (LocalDateTime) -> Unit
) {
    val ivyContext = LocalIvyContext.current

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

    onCreate: (CreateLoanRecordData) -> Unit,
    onEdit: (LoanRecord) -> Unit,
    dismiss: () -> Unit
) {
    if (loanRecord != null) {
        onEdit(
            loanRecord.copy(
                note = noteTextFieldValue.text.trim(),
                amount = amount,
                dateTime = dateTime,
                interest = loanRecordInterest
            )
        )
    } else {
        onCreate(
            CreateLoanRecordData(
                note = noteTextFieldValue.text.trim(),
                amount = amount,
                dateTime = dateTime,
                interest = loanRecordInterest
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


@Preview
@Composable
private fun Preview() {
    IvyAppPreview {
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