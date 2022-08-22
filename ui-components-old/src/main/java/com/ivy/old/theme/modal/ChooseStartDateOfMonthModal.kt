package com.ivy.wallet.ui.theme.modal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.base.R
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.wallet.ui.theme.Ivy
import com.ivy.wallet.ui.theme.White
import com.ivy.wallet.utils.thenIf
import java.util.*

@Composable
fun BoxWithConstraintsScope.ChooseStartDateOfMonthModal(
    id: UUID = UUID.randomUUID(),
    visible: Boolean,
    selectedStartDateOfMonth: Int,

    dismiss: () -> Unit,
    onStartDateOfMonthSelected: (Int) -> Unit,
) {
    IvyModal(
        id = id,
        visible = visible,
        dismiss = dismiss,
        PrimaryAction = { }
    ) {
        Spacer(Modifier.height(32.dp))

        ModalTitle(text = stringResource(R.string.choose_start_date_of_month))

        Spacer(Modifier.height(32.dp))

        NumberRow(
            selectedNumber = selectedStartDateOfMonth,
            fromInclusive = 1,
            toInclusive = 5
        ) {
            save(
                number = it,
                onStartDateOfMonthSelected = onStartDateOfMonthSelected,
                dismiss = dismiss
            )
        }

        Spacer(Modifier.height(16.dp))

        NumberRow(
            selectedNumber = selectedStartDateOfMonth,
            fromInclusive = 6,
            toInclusive = 10
        ) {
            save(
                number = it,
                onStartDateOfMonthSelected = onStartDateOfMonthSelected,
                dismiss = dismiss
            )
        }

        Spacer(Modifier.height(16.dp))

        NumberRow(
            selectedNumber = selectedStartDateOfMonth,
            fromInclusive = 11,
            toInclusive = 15
        ) {
            save(
                number = it,
                onStartDateOfMonthSelected = onStartDateOfMonthSelected,
                dismiss = dismiss
            )
        }

        Spacer(Modifier.height(16.dp))

        NumberRow(
            selectedNumber = selectedStartDateOfMonth,
            fromInclusive = 16,
            toInclusive = 20
        ) {
            save(
                number = it,
                onStartDateOfMonthSelected = onStartDateOfMonthSelected,
                dismiss = dismiss
            )
        }

        Spacer(Modifier.height(16.dp))

        NumberRow(
            selectedNumber = selectedStartDateOfMonth,
            fromInclusive = 21,
            toInclusive = 25
        ) {
            save(
                number = it,
                onStartDateOfMonthSelected = onStartDateOfMonthSelected,
                dismiss = dismiss
            )
        }

        Spacer(Modifier.height(16.dp))

        NumberRow(
            selectedNumber = selectedStartDateOfMonth,
            fromInclusive = 26,
            toInclusive = 30
        ) {
            save(
                number = it,
                onStartDateOfMonthSelected = onStartDateOfMonthSelected,
                dismiss = dismiss
            )
        }

        Spacer(Modifier.height(16.dp))

        NumberRow(
            selectedNumber = selectedStartDateOfMonth,
            fromInclusive = 31,
            toInclusive = 31,
        ) {
            save(
                number = it,
                onStartDateOfMonthSelected = onStartDateOfMonthSelected,
                dismiss = dismiss
            )
        }

        Spacer(Modifier.height(8.dp))
    }
}

private fun save(
    number: Int,

    onStartDateOfMonthSelected: (Int) -> Unit,
    dismiss: () -> Unit
) {
    onStartDateOfMonthSelected(number)
    dismiss()
}

@Composable
private fun ColumnScope.NumberRow(
    selectedNumber: Int,
    fromInclusive: Int,
    toInclusive: Int,
    onClick: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .align(Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(24.dp))

        for (number in fromInclusive..toInclusive) {
            NumberView(
                number = number,
                selected = number == selectedNumber
            ) {
                onClick(it)
            }

            Spacer(Modifier.width(20.dp))
        }

        Spacer(Modifier.width(24.dp))
    }
}

@Composable
private fun NumberView(
    number: Int,
    selected: Boolean,
    onClick: (Int) -> Unit
) {
    Box(modifier = Modifier
        .size(48.dp)
        .clip(CircleShape)
        .border(2.dp, if (selected) Ivy else UI.colors.medium, CircleShape)
        .thenIf(selected) {
            background(Ivy, CircleShape)
        }
        .clickable {
            onClick(number)
        },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = number.toString(),
            style = UI.typo.nB2.style(
                fontWeight = FontWeight.ExtraBold,
                color = if (selected) White else UI.colors.pureInverse,
                textAlign = TextAlign.Center
            )
        )
    }
}

@Preview
@Composable
private fun Preview() {
    com.ivy.core.ui.temp.Preview {
        ChooseStartDateOfMonthModal(
            visible = true,
            selectedStartDateOfMonth = 1,
            dismiss = {}
        ) {

        }
    }
}