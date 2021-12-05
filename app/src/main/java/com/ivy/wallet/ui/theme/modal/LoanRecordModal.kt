package com.ivy.wallet.ui.theme.modal

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.wallet.R
import com.ivy.wallet.base.*
import com.ivy.wallet.logic.model.CreateLoanRecordData
import com.ivy.wallet.model.entity.LoanRecord
import com.ivy.wallet.ui.IvyAppPreview
import com.ivy.wallet.ui.LocalIvyContext
import com.ivy.wallet.ui.theme.components.IvyOutlinedButton
import com.ivy.wallet.ui.theme.modal.edit.AmountModal
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

    val forceNonZeroBalance = true

    IvyModal(
        id = modal?.id,
        visible = modal != null,
        dismiss = dismiss,
        shiftIfKeyboardShown = false,
        PrimaryAction = {
            ModalAddSave(
                item = initialRecord,
                enabled = noteTextFieldValue.text.isNotNullOrBlank() && (!forceNonZeroBalance || amount > 0)
            ) {
                save(
                    loanRecord = initialRecord,
                    noteTextFieldValue = noteTextFieldValue,
                    amount = amount,
                    dateTime = dateTime,

                    onCreate = onCreate,
                    onEdit = onEdit,
                    dismiss = dismiss
                )
            }
        }
    ) {
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
                initialDate = dateTime.toLocalDate()
            ) {
                onSetDateTime(it.atTime(dateTime.toLocalTime()))
            }
        }

        Spacer(Modifier.weight(1f))

        IvyOutlinedButton(
            text = dateTime.formatLocalTime(),
            iconStart = R.drawable.ic_date
        ) {
            ivyContext.timePicker {
                onSetDateTime(dateTime.toLocalDate().atTime(it))
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
            )
        )
    } else {
        onCreate(
            CreateLoanRecordData(
                note = noteTextFieldValue.text.trim(),
                amount = amount,
                dateTime = dateTime,
            )
        )
    }

    dismiss()
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