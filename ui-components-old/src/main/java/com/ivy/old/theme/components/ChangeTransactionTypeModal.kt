package com.ivy.wallet.ui.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.base.IvyWalletPreview
import com.ivy.data.transaction.TransactionType
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.base.R
import com.ivy.wallet.ui.theme.Gradient
import com.ivy.wallet.ui.theme.GradientGreen
import com.ivy.wallet.ui.theme.GradientIvy
import com.ivy.wallet.ui.theme.White
import com.ivy.wallet.ui.theme.modal.IvyModal
import com.ivy.wallet.ui.theme.modal.ModalSet
import com.ivy.wallet.ui.theme.modal.ModalTitle
import java.util.*

@Composable
fun BoxWithConstraintsScope.ChangeTransactionTypeModal(
    title: String = stringResource(R.string.set_transaction_type),
    visible: Boolean,
    includeTransferType: Boolean,
    initialType: TransactionType,
    id: UUID = UUID.randomUUID(),
    dismiss: () -> Unit,
    onTransactionTypeChanged: (TransactionType) -> Unit
) {
    var transactionType by remember(id) {
        mutableStateOf(initialType)
    }

    IvyModal(
        id = id,
        visible = visible,
        dismiss = dismiss,
        PrimaryAction = {
            ModalSet {
                save(
                    transactionType = transactionType,
                    onTransactionTypeChanged = onTransactionTypeChanged,
                    dismiss = dismiss,
                )
            }
        }
    ) {
        Spacer(Modifier.height(32.dp))

        ModalTitle(text = title)

        Spacer(Modifier.height(32.dp))

        TransactionTypeButton(
            transactionType = TransactionType.INCOME,
            selected = transactionType == TransactionType.INCOME,
            selectedGradient = GradientGreen,
            textSelectedColor = White
        ) {
            transactionType = TransactionType.INCOME
            save(
                transactionType = transactionType,
                onTransactionTypeChanged = onTransactionTypeChanged,
                dismiss = dismiss,
            )
        }

        Spacer(Modifier.height(12.dp))

        TransactionTypeButton(
            transactionType = TransactionType.EXPENSE,
            selected = transactionType == TransactionType.EXPENSE,
            selectedGradient = Gradient(UI.colors.pureInverse, UI.colors.gray),
            textSelectedColor = UI.colors.pure
        ) {
            transactionType = TransactionType.EXPENSE
            save(
                transactionType = transactionType,
                onTransactionTypeChanged = onTransactionTypeChanged,
                dismiss = dismiss,
            )
        }

        if (includeTransferType) {
            Spacer(Modifier.height(12.dp))

            TransactionTypeButton(
                transactionType = TransactionType.TRANSFER,
                selected = transactionType == TransactionType.TRANSFER,
                selectedGradient = GradientIvy,
                textSelectedColor = White
            ) {
                transactionType = TransactionType.TRANSFER
                save(
                    transactionType = transactionType,
                    onTransactionTypeChanged = onTransactionTypeChanged,
                    dismiss = dismiss,
                )
            }
        }

        Spacer(Modifier.height(48.dp))
    }
}

private fun save(
    transactionType: TransactionType,
    onTransactionTypeChanged: (TransactionType) -> Unit,
    dismiss: () -> Unit
) {
    onTransactionTypeChanged(transactionType)
    dismiss()
}

@Composable
private fun TransactionTypeButton(
    transactionType: TransactionType,
    selected: Boolean,
    selectedGradient: Gradient,
    textSelectedColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(UI.shapes.r4)
            .background(
                brush = if (selected) selectedGradient.asHorizontalBrush() else SolidColor(UI.colors.medium),
                shape = UI.shapes.r4
            )
            .clickable {
                onClick()
            }
            .padding(vertical = 16.dp)
            .testTag("modal_type_${transactionType.name}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(16.dp))

        val textColor = if (selected) textSelectedColor else UI.colors.pureInverse

        IvyIcon(
            icon = when (transactionType) {
                TransactionType.INCOME -> R.drawable.ic_income
                TransactionType.EXPENSE -> R.drawable.ic_expense
                TransactionType.TRANSFER -> R.drawable.ic_transfer
            },
            tint = textColor
        )

        Spacer(Modifier.width(12.dp))

        Text(
            text = when (transactionType) {
                TransactionType.INCOME -> stringResource(R.string.income)
                TransactionType.EXPENSE -> stringResource(R.string.expense)
                TransactionType.TRANSFER -> stringResource(R.string.transfer)
            },
            style = UI.typo.b1.style(
                color = textColor
            )
        )

        if (selected) {
            Spacer(Modifier.weight(1f))

            IvyIcon(
                icon = R.drawable.ic_check,
                tint = textSelectedColor
            )

            Text(
                text = stringResource(R.string.selected),
                style = UI.typo.b2.style(
                    fontWeight = FontWeight.SemiBold,
                    color = textSelectedColor
                )
            )

            Spacer(Modifier.width(24.dp))
        }
    }
}

@Preview
@Composable
private fun Preview() {
    IvyWalletPreview {
        ChangeTransactionTypeModal(
            includeTransferType = true,
            visible = true,
            initialType = TransactionType.INCOME,
            dismiss = {}
        ) {

        }
    }
}