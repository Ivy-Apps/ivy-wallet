package com.ivy.wallet.ui.theme.modal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.wallet.R
import com.ivy.wallet.base.isNotNullOrBlank
import com.ivy.wallet.base.onScreenStart
import com.ivy.wallet.base.selectEndTextFieldValue
import com.ivy.wallet.base.thenIf
import com.ivy.wallet.logic.model.CreateLoanData
import com.ivy.wallet.model.IvyCurrency
import com.ivy.wallet.model.LoanType
import com.ivy.wallet.model.entity.Loan
import com.ivy.wallet.ui.IvyAppPreview
import com.ivy.wallet.ui.theme.GradientIvy
import com.ivy.wallet.ui.theme.Gray
import com.ivy.wallet.ui.theme.Ivy
import com.ivy.wallet.ui.theme.White
import com.ivy.wallet.ui.theme.components.IvyColorPicker
import com.ivy.wallet.ui.theme.modal.edit.AmountModal
import com.ivy.wallet.ui.theme.modal.edit.IconNameRow
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
    modal: LoanModalData?,
    onCreateLoan: (CreateLoanData) -> Unit,
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
    var currencyCode by remember(modal) {
        mutableStateOf(modal?.baseCurrency ?: "")
    }


    var amountModalVisible by remember { mutableStateOf(false) }
    var currencyModalVisible by remember { mutableStateOf(false) }
    var chooseIconModalVisible by remember(modal) {
        mutableStateOf(false)
    }


    IvyModal(
        id = modal?.id,
        visible = modal != null,
        dismiss = dismiss,
        shiftIfKeyboardShown = false,
        PrimaryAction = {
            ModalAddSave(
                item = modal?.loan,
                enabled = nameTextFieldValue.text.isNotNullOrBlank() && amount > 0
            ) {
                save(
                    loan = loan,
                    nameTextFieldValue = nameTextFieldValue,
                    type = type,
                    color = color,
                    icon = icon,
                    amount = amount,

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

        Spacer(modifier = Modifier.height(40.dp))

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
private fun ColumnScope.LoanTypePicker(
    type: LoanType,
    onTypeSelected: (LoanType) -> Unit
) {
    Text(
        modifier = Modifier.padding(horizontal = 32.dp),
        text = "Loan type",
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

    onCreateLoan: (CreateLoanData) -> Unit,
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
            onEditLoan = { }
        ) {

        }
    }
}