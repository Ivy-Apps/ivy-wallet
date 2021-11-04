package com.ivy.wallet.ui.theme.modal.edit

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ivy.wallet.R
import com.ivy.wallet.base.*
import com.ivy.wallet.model.IvyCurrency
import com.ivy.wallet.ui.IvyAppPreview
import com.ivy.wallet.ui.theme.*
import com.ivy.wallet.ui.theme.components.IvyIcon
import com.ivy.wallet.ui.theme.modal.IvyModal
import com.ivy.wallet.ui.theme.modal.ModalPositiveButton
import com.ivy.wallet.ui.theme.modal.modalPreviewActionRowHeight
import java.util.*
import kotlin.math.truncate

@Composable
fun BoxWithConstraintsScope.AmountModal(
    id: UUID = UUID.randomUUID(),
    visible: Boolean,
    currency: String,
    initialAmount: Double?,
    Header: (@Composable () -> Unit)? = null,
    amountSpacerTop: Dp = 64.dp,
    dismiss: () -> Unit,
    onAmountChanged: (Double) -> Unit,
) {
    var amount by remember(id) {
        mutableStateOf(
            initialAmount?.takeIf { it != 0.0 }?.format(currency) ?: ""
        )
    }

    var calculatorModalVisible by remember(id) {
        mutableStateOf(false)
    }

    IvyModal(
        id = id,
        visible = visible,
        dismiss = dismiss,
        PrimaryAction = {
            IvyIcon(
                modifier = circleButtonModifier(
                    size = 52.dp,
                    onClick = {
                        calculatorModalVisible = true
                    })
                    .padding(all = 4.dp),
                icon = R.drawable.ic_custom_calculator_m,
                tint = IvyTheme.colors.pureInverse
            )

            Spacer(Modifier.width(16.dp))

            ModalPositiveButton(
                text = "Enter",
                iconStart = R.drawable.ic_check
            ) {
                try {
                    if (amount.isEmpty()) {
                        onAmountChanged(0.0)
                    } else {
                        onAmountChanged(amount.replace(",", "").toDouble())
                    }
                    dismiss()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    ) {
        Header?.invoke()

        Spacer(Modifier.height(amountSpacerTop))

        val rootView = LocalView.current
        onScreenStart {
            hideKeyboard(rootView)
        }

        AmountCurrency(
            amount = amount,
            currency = currency
        )

        Spacer(Modifier.height(56.dp))

        AmountInput(
            currency = currency,
            amount = amount
        ) {
            amount = it
        }

        Spacer(Modifier.height(24.dp))
    }

    CalculatorModal(
        visible = calculatorModalVisible,
        currency = currency,
        dismiss = {
            calculatorModalVisible = false
        },
        onCalculation = {
            amount = it.format(currency)
        }
    )
}

@Composable
fun AmountCurrency(
    amount: String,
    currency: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.weight(1f))

        Text(
            text = if (amount.isBlank()) "0" else amount,
            style = Typo.numberH1.style(
                fontWeight = FontWeight.Bold,
                color = IvyTheme.colors.pureInverse
            )
        )

        Text(
            text = " $currency",
            style = Typo.numberH2.style(
                fontWeight = FontWeight.Normal,
                color = IvyTheme.colors.pureInverse
            )
        )

        Spacer(Modifier.weight(1f))
    }
}

@Composable
fun AmountInput(
    currency: String,
    amount: String,
    setAmount: (String) -> Unit
) {
    var firstInput by remember { mutableStateOf(true) }

    AmountKeyboard(
        onNumberPressed = {
            if (firstInput) {
                setAmount(it)
                firstInput = false
            } else {
                val newlyEnteredNumberString = amount.replace(",", "") + it

                val decimalPartString = newlyEnteredNumberString
                    .split(".")
                    .getOrNull(1)
                val decimalCount = decimalPartString?.length ?: 0

                val amountDouble = newlyEnteredNumberString.toDoubleOrNull()

                val decimalCountOkay = IvyCurrency.fromCode(currency)?.isCrypto == true
                    || decimalCount <= 2
                if (amountDouble != null && decimalCountOkay) {
                    val intPart = truncate(amountDouble).toInt()
                    val decimalPartFormatted = if (decimalPartString != null) {
                        ".${decimalPartString}"
                    } else ""

                    val finalAmount = formatInt(intPart) + decimalPartFormatted

                    setAmount(finalAmount)
                }
            }
        },
        onDecimalPoint = {
            if (firstInput) {
                setAmount("0.")
                firstInput = false
            } else {
                val newlyEnteredString = if (amount.isEmpty()) "0." else "$amount."
                if (newlyEnteredString.replace(",", "").toDoubleOrNull() != null) {
                    setAmount(newlyEnteredString)
                }
            }

        },
        onBackspace = {
            if (firstInput) {
                setAmount("")
                firstInput = false
            } else {
                if (amount.isNotEmpty()) {
                    val formattedNumber = formatNumber(amount.dropLast(1))
                    setAmount(formattedNumber ?: "")
                }
            }
        }
    )
}

private fun formatNumber(number: String): String? {
    val newAmountString = number.replace(",", "")

    val decimalPartString = newAmountString
        .split(".")
        .getOrNull(1)
    val newDecimalCount = decimalPartString?.length ?: 0

    val amountDouble = newAmountString.toDoubleOrNull()

    if (newDecimalCount <= 2 && amountDouble != null) {
        val intPart = truncate(amountDouble).toInt()
        val decimalFormatted = if (decimalPartString != null) {
            ".${decimalPartString}"
        } else ""

        return formatInt(intPart) + decimalFormatted
    }

    return null
}

@Composable
fun AmountKeyboard(
    ZeroRow: (@Composable RowScope.() -> Unit)? = null,
    FirstRowExtra: (@Composable RowScope.() -> Unit)? = null,
    SecondRowExtra: (@Composable RowScope.() -> Unit)? = null,
    ThirdRowExtra: (@Composable RowScope.() -> Unit)? = null,
    FourthRowExtra: (@Composable RowScope.() -> Unit)? = null,

    onNumberPressed: (String) -> Unit,
    onDecimalPoint: () -> Unit,
    onBackspace: () -> Unit,
) {
    if (ZeroRow != null) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            ZeroRow.invoke(this)
        }

        Spacer(Modifier.height(16.dp))
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        CircleNumberButton(
            value = "1",
            onNumberPressed = onNumberPressed
        )

        Spacer(Modifier.width(16.dp))

        CircleNumberButton(
            value = "2",
            onNumberPressed = onNumberPressed
        )

        Spacer(Modifier.width(16.dp))

        CircleNumberButton(
            value = "3",
            onNumberPressed = onNumberPressed
        )

        if (FirstRowExtra != null) {
            Spacer(modifier = Modifier.width(16.dp))

            FirstRowExtra.invoke(this)
        }
    }

    Spacer(Modifier.height(16.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        CircleNumberButton(
            value = "4",
            onNumberPressed = onNumberPressed
        )

        Spacer(Modifier.width(16.dp))

        CircleNumberButton(
            value = "5",
            onNumberPressed = onNumberPressed
        )

        Spacer(Modifier.width(16.dp))

        CircleNumberButton(
            value = "6",
            onNumberPressed = onNumberPressed
        )

        if (SecondRowExtra != null) {
            Spacer(modifier = Modifier.width(16.dp))

            SecondRowExtra.invoke(this)
        }
    }

    Spacer(Modifier.height(16.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        CircleNumberButton(
            value = "7",
            onNumberPressed = onNumberPressed
        )

        Spacer(Modifier.width(16.dp))

        CircleNumberButton(
            value = "8",
            onNumberPressed = onNumberPressed
        )

        Spacer(Modifier.width(16.dp))

        CircleNumberButton(
            value = "9",
            onNumberPressed = onNumberPressed
        )

        if (ThirdRowExtra != null) {
            Spacer(modifier = Modifier.width(16.dp))

            ThirdRowExtra.invoke(this)
        }
    }

    Spacer(Modifier.height(16.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        KeypadCircleButton(text = ".") {
            onDecimalPoint()
        }

        Spacer(Modifier.width(16.dp))

        CircleNumberButton(
            value = "0",
            onNumberPressed = onNumberPressed
        )

        Spacer(Modifier.width(16.dp))

        IvyIcon(
            modifier = circleButtonModifier(onClick = onBackspace)
                .padding(all = 16.dp),
            icon = R.drawable.ic_backspace,
            tint = Red
        )

        if (FourthRowExtra != null) {
            Spacer(modifier = Modifier.width(16.dp))

            FourthRowExtra.invoke(this)
        }
    }
}

@Composable
fun CircleNumberButton(
    value: String,
    onNumberPressed: (String) -> Unit,
) {
    KeypadCircleButton(
        text = value,
        onClick = {
            onNumberPressed(value)
        }
    )
}

@Composable
fun KeypadCircleButton(
    text: String,
    textColor: Color = IvyTheme.colors.pureInverse,
    onClick: () -> Unit
) {
    Text(
        modifier = circleButtonModifier(onClick = onClick)
            .padding(top = 10.dp),
        text = text,
        style = Typo.numberH2.style(
            color = textColor,
            fontWeight = FontWeight.Bold
        ).copy(
            textAlign = TextAlign.Center
        )
    )
}

@SuppressLint("ComposableModifierFactory", "ModifierFactoryExtensionFunction")
@Composable
private fun circleButtonModifier(
    size: Dp = 64.dp,
    onClick: () -> Unit
): Modifier {
    return Modifier
        .size(size)
        .drawColoredShadow(
            color = Black,
            alpha = if (IvyTheme.colors.isLight) 0.05f else 0.5f,
            borderRadius = 32.dp
        )
        .clip(CircleShape)
        .clickable(
            onClick = onClick
        )
        .background(IvyTheme.colors.pure, Shapes.roundedFull)
        .border(2.dp, IvyTheme.colors.medium, Shapes.roundedFull)
}

@Preview
@Composable
private fun Preview() {
    IvyAppPreview {
        BoxWithConstraints(
            modifier = Modifier.padding(bottom = modalPreviewActionRowHeight())
        ) {
            AmountModal(
                visible = true,
                currency = "BGN",
                initialAmount = null,
                dismiss = { }
            ) {

            }
        }

    }
}