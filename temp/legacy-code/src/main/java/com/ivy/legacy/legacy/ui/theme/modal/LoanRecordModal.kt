package com.ivy.wallet.ui.theme.modal

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.base.model.LoanRecordType
import com.ivy.data.model.primitive.NotBlankTrimmedString
import com.ivy.design.api.LocalTimeConverter
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.design.utils.thenIf
import com.ivy.frp.test.TestingContext
import com.ivy.legacy.IvyWalletPreview
import com.ivy.legacy.datamodel.Account
import com.ivy.legacy.datamodel.LoanRecord
import com.ivy.legacy.legacy.ui.theme.components.DateTimeRow
import com.ivy.legacy.legacy.ui.theme.modal.ModalNameInput
import com.ivy.legacy.utils.getDefaultFIATCurrency
import com.ivy.legacy.utils.onScreenStart
import com.ivy.legacy.utils.selectEndTextFieldValue
import com.ivy.ui.R
import com.ivy.wallet.domain.deprecated.logic.model.CreateAccountData
import com.ivy.wallet.domain.deprecated.logic.model.CreateLoanRecordData
import com.ivy.wallet.domain.deprecated.logic.model.EditLoanRecordData
import com.ivy.wallet.ui.theme.components.ItemIconSDefaultIcon
import com.ivy.wallet.ui.theme.components.IvyCheckboxWithText
import com.ivy.wallet.ui.theme.components.IvyIcon
import com.ivy.wallet.ui.theme.findContrastTextColor
import com.ivy.wallet.ui.theme.modal.edit.AccountModal
import com.ivy.wallet.ui.theme.modal.edit.AccountModalData
import com.ivy.wallet.ui.theme.modal.edit.AmountModal
import com.ivy.wallet.ui.theme.toComposeColor
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID

@Deprecated("Old design system. Use `:ivy-design` and Material3")
data class LoanRecordModalData(
    val loanRecord: LoanRecord?,
    val baseCurrency: String,
    val loanAccountCurrencyCode: String? = null,
    val selectedAccount: Account? = null,
    val createLoanRecordTransaction: Boolean = false,
    val isLoanInterest: Boolean = false,
    val id: UUID = UUID.randomUUID(),
)

@Suppress("CyclomaticComplexMethod", "LongMethod")
@SuppressLint("ComposeModifierMissing")
@Deprecated("Old design system. Use `:ivy-design` and Material3")
@Composable
fun BoxWithConstraintsScope.LoanRecordModal(
    modal: LoanRecordModalData?,
    dateTime: Instant,
    onSetDate: () -> Unit,
    onSetTime: () -> Unit,
    onCreate: (CreateLoanRecordData) -> Unit,
    onEdit: (EditLoanRecordData) -> Unit,
    onDelete: (LoanRecord) -> Unit,
    dismiss: () -> Unit,
    accounts: List<Account> = emptyList(),
    onCreateAccount: (CreateAccountData) -> Unit = {},
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
    var loanRecordType by remember(modal) {
        mutableStateOf(modal?.loanRecord?.loanRecordType ?: LoanRecordType.DECREASE)
    }

    var dateTime = modal?.loanRecord?.dateTime ?: dateTime
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
                // enabled = amount > 0 && ((createLoanRecordTrans && selectedAcc != null) || !createLoanRecordTrans)
            ) {
                accountChangeConformationModal =
                    initialRecord != null && modal.selectedAccount != null &&
                            modal.baseCurrency != currencyCode && currencyCode != modal.loanAccountCurrencyCode

                if (!accountChangeConformationModal) {
                    save(
                        loanRecord = initialRecord,
                        noteTextFieldValue = noteTextFieldValue,
                        amount = amount,
                        dateTime = dateTime,
                        loanRecordInterest = loanInterest,
                        selectedAccount = selectedAcc,
                        createLoanRecordTransaction = createLoanRecordTrans,
                        reCalculateAmount = reCalculate,
                        loanRecordType = loanRecordType,

                        onCreate = onCreate,
                        onEdit = onEdit,
                        dismiss = dismiss,
                    )
                }
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
                text = if (initialRecord != null) {
                    stringResource(R.string.edit_record)
                } else {
                    stringResource(
                        R.string.new_record
                    )
                }
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

        val timeConverter = LocalTimeConverter.current
        DateTimeRow(
            dateTime = with(timeConverter) { dateTime.toLocalDateTime() },
            onEditDate = onSetDate,
            onEditTime = onSetTime,
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
                // Unchecks the Recalculate Option if Recalculate Checkbox is not visible
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

        Text(
            modifier = Modifier.padding(horizontal = 32.dp),
            text = stringResource(R.string.loan_record_type),
            style = UI.typo.b2.style(
                color = UI.colors.pureInverse,
                fontWeight = FontWeight.ExtraBold
            )
        )

        Spacer(Modifier.height(16.dp))

        LoanRecordTypeRow(selectedRecordType = loanRecordType, onLoanRecordTypeChanged = {
            if (it == LoanRecordType.INCREASE) loanInterest = false
            loanRecordType = it
        })

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

        AnimatedVisibility(visible = loanRecordType == LoanRecordType.DECREASE) {
            IvyCheckboxWithText(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .align(Alignment.Start),
                text = stringResource(R.string.mark_as_interest),
                checked = loanInterest
            ) {
                loanInterest = it
            }
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
            loanRecordType = loanRecordType,

            onCreate = onCreate,
            onEdit = onEdit,
            dismiss = dismiss,
        )

        accountChangeConformationModal = false
    }
}

private fun save(
    loanRecord: LoanRecord?,
    noteTextFieldValue: TextFieldValue,
    amount: Double,
    dateTime: Instant,
    loanRecordInterest: Boolean = false,
    createLoanRecordTransaction: Boolean = false,
    selectedAccount: Account? = null,
    reCalculateAmount: Boolean = false,
    loanRecordType: LoanRecordType,

    onCreate: (CreateLoanRecordData) -> Unit,
    onEdit: (EditLoanRecordData) -> Unit,
    dismiss: () -> Unit
) {
    if (loanRecord != null) {
        val record = loanRecord.copy(
            note = NotBlankTrimmedString.from(noteTextFieldValue.text).getOrNull()?.value,
            amount = amount,
            dateTime = dateTime,
            interest = loanRecordInterest,
            accountId = selectedAccount?.id,
            loanRecordType = loanRecordType
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
                note = NotBlankTrimmedString.from(noteTextFieldValue.text).getOrNull()?.value,
                amount = amount,
                dateTime = dateTime,
                interest = loanRecordInterest,
                account = selectedAccount,
                createLoanRecordTransaction = createLoanRecordTransaction,
                loanRecordType = loanRecordType
            )
        )
    }

    dismiss()
}

@Composable
@Suppress("ParameterNaming")
private fun LoanRecordTypeRow(
    selectedRecordType: LoanRecordType?,
    modifier: Modifier = Modifier,
    onLoanRecordTypeChanged: (LoanRecordType) -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(Modifier.width(24.dp))
        LoanRecordType(
            modifier = Modifier,
            loanRecordType = LoanRecordType.DECREASE,
            selectedRecordType = selectedRecordType
        ) {
            onLoanRecordTypeChanged(it)
        }
        Spacer(modifier = Modifier.width(8.dp))
        LoanRecordType(
            modifier = Modifier,
            loanRecordType = LoanRecordType.INCREASE,
            selectedRecordType = selectedRecordType
        ) {
            onLoanRecordTypeChanged(it)
        }
    }
}

@Composable
private fun LoanRecordType(
    loanRecordType: LoanRecordType,
    selectedRecordType: LoanRecordType?,
    modifier: Modifier = Modifier,
    onClick: (LoanRecordType) -> Unit
) {
    val (text, iconDrawable) =
        if (loanRecordType == LoanRecordType.INCREASE) {
            stringResource(id = R.string.increase_loan) to R.drawable.ic_donate_plus
        } else {
            stringResource(id = R.string.decrease_loan) to R.drawable.ic_donate_minus
        }
    val selected = selectedRecordType == loanRecordType
    val medium = UI.colors.medium
    val rFull = UI.shapes.rFull
    val selectedColor = UI.colors.green1
    Row(
        modifier = modifier
            .clip(UI.shapes.rFull)
            .thenIf(!selected) {
                border(2.dp, medium, rFull)
            }
            .thenIf(selected) {
                background(selectedColor, rFull)
            }
            .clickable(onClick = { onClick(loanRecordType) }),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(12.dp))

        ItemIconSDefaultIcon(
            defaultIcon = iconDrawable,
            iconName = null,
            tint = UI.colors.pureInverse
        )

        Spacer(Modifier.width(4.dp))

        Text(
            modifier = Modifier.padding(vertical = 10.dp),
            text = text,
            style = UI.typo.b2.style(
                color = UI.colors.pureInverse,
                fontWeight = FontWeight.ExtraBold
            )
        )
        Spacer(Modifier.width(24.dp))
    }
}

@Composable
@Suppress("ParameterNaming")
private fun AccountsRow(
    accounts: List<Account>,
    selectedAccount: Account?,
    onSelectedAccountChanged: (Account) -> Unit,
    onAddNewAccount: () -> Unit,
    modifier: Modifier = Modifier,
    childrenTestTag: String? = null,
) {
    val lazyState = rememberLazyListState()

    LaunchedEffect(accounts, selectedAccount) {
        if (selectedAccount != null) {
            val selectedIndex = accounts.indexOf(selectedAccount)
            if (selectedIndex != -1) {
                launch {
                    if (TestingContext.inTest) return@launch // breaks UI tests

                    lazyState.scrollToItem(
                        index = selectedIndex, // +1 because Spacer width 24.dp
                    )
                }
            }
        }
    }

    if (TestingContext.inTest) return // fix broken tests

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

@SuppressLint("ComposeContentEmitterReturningValues", "ComposeMultipleContentEmitters")
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

    val medium = UI.colors.medium
    val rFull = UI.shapes.rFull

    Row(
        modifier = Modifier
            .clip(UI.shapes.rFull)
            .thenIf(!selected) {
                border(2.dp, medium, rFull)
            }
            .thenIf(selected) {
                background(accountColor, rFull)
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

@SuppressLint("ComposeContentEmitterReturningValues", "ComposeMultipleContentEmitters")
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
            onDelete = {},
            dismiss = {},
            onSetDate = {},
            onSetTime = {},
            dateTime = Instant.now()
        )
    }
}
