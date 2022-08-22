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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ivy.base.R
import com.ivy.data.IvyCurrency
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.frp.view.navigation.onScreenStart
import com.ivy.wallet.ui.theme.Red
import com.ivy.wallet.ui.theme.components.IvyIcon
import com.ivy.wallet.ui.theme.modal.IvyModal
import com.ivy.wallet.ui.theme.modal.ModalPositiveButton
import com.ivy.wallet.ui.theme.modal.modalPreviewActionRowHeight
import com.ivy.wallet.utils.*
import java.util.*
import kotlin.math.truncate

@Composable
fun BoxWithConstraintsScope.AmountModal(
    id: UUID,
    visible: Boolean,
    currency: String,
    initialAmount: Double?,
    decimalCountMax: Int = 2,
    Header: (@Composable () -> Unit)? = null,
    amountSpacerTop: Dp = 64.dp,
    dismiss: () -> Unit,
    onAmountChanged: (Double) -> Unit,
) {
    var amount by remember(id) {
        mutableStateOf(
            if (currency.isNotEmpty())
                initialAmount?.takeIf { it != 0.0 }?.format(currency)
                    ?: ""
            else
                initialAmount?.takeIf { it != 0.0 }?.format(decimalCountMax)
                    ?: ""
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
                    .testTag("btn_calculator")
                    .padding(all = 4.dp),
                icon = R.drawable.ic_custom_calculator_m,
                tint = UI.colors.pureInverse
            )

            Spacer(Modifier.width(16.dp))

            ModalPositiveButton(
                text = stringResource(R.string.enter),
                iconStart = R.drawable.ic_check
            ) {
                try {
                    if (amount.isEmpty()) {
                        onAmountChanged(0.0)
                    } else {
                        onAmountChanged(amount.amountToDouble())
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
            decimalCountMax = decimalCountMax,
            amount = amount
        ) {
            amount = it
        }

        Spacer(Modifier.height(24.dp))
    }

    CalculatorModal(
        visible = calculatorModalVisible,
        initialAmount = amount.amountToDoubleOrNull(),
        currency = currency,
        dismiss = {
            calculatorModalVisible = false
        },
        onCalculation = {
            amount = if (currency.isNotEmpty()) it.format(currency) else it.format(decimalCountMax)
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
            text = amount.ifBlank { "0" },
            style = UI.typo.nH1.style(
                fontWeight = FontWeight.Bold,
                color = UI.colors.pureInverse
            )
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = currency,
            style = UI.typo.nH2.style(
                fontWeight = FontWeight.Normal,
                color = UI.colors.pureInverse
            )
        )

        Spacer(Modifier.weight(1f))
    }
}

@Composable
fun AmountInput(
    currency: String,
    amount: String,
    decimalCountMax: Int = 2,
    setAmount: (String) -> Unit,

    ) {
    var firstInput by remember { mutableStateOf(true) }

    AmountKeyboard(
        forCalculator = false,
        onNumberPressed = {
            if (firstInput) {
                setAmount(it)
                firstInput = false
            } else {
                val formattedAmount = formatInputAmount(
                    currency = currency,
                    amount = amount,
                    newSymbol = it,
                    decimalCountMax = decimalCountMax
                )
                if (formattedAmount != null) {
                    setAmount(formattedAmount)
                }
            }
        },
        onDecimalPoint = {
            if (firstInput) {
                setAmount("0${localDecimalSeparator()}")
                firstInput = false
            } else {
                val newlyEnteredString = if (amount.isEmpty())
                    "0${localDecimalSeparator()}" else "$amount${localDecimalSeparator()}"
                if (newlyEnteredString.amountToDoubleOrNull() != null) {
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
                    val formattedNumber = formatNumber(amount.dropLast(1), currency)
                    setAmount(formattedNumber ?: "")
                }
            }
        }
    )
}

private fun formatNumber(number: String, currency: String): String? {
    val decimalPartString = number
        .split(localDecimalSeparator())
        .getOrNull(1)
    val newDecimalCount = decimalPartString?.length ?: 0

    val amountDouble = number.amountToDoubleOrNull()

    if ((newDecimalCount <= 2 || IvyCurrency.fromCode(currency)?.isCrypto == true) &&
        amountDouble != null
    ) {
        val intPart = truncate(amountDouble).toInt()
        val decimalFormatted = if (decimalPartString != null) {
            "${localDecimalSeparator()}${decimalPartString}"
        } else ""

        return formatInt(intPart) + decimalFormatted
    }

    return null
}

@Composable
fun AmountKeyboard(
    forCalculator: Boolean,
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
            forCalculator = forCalculator,
            value = "7",
            onNumberPressed = onNumberPressed
        )

        Spacer(Modifier.width(16.dp))

        CircleNumberButton(
            forCalculator = forCalculator,
            value = "8",
            onNumberPressed = onNumberPressed
        )

        Spacer(Modifier.width(16.dp))

        CircleNumberButton(
            forCalculator = forCalculator,
            value = "9",
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
            forCalculator = forCalculator,
            value = "4",
            onNumberPressed = onNumberPressed
        )

        Spacer(Modifier.width(16.dp))

        CircleNumberButton(
            forCalculator = forCalculator,
            value = "5",
            onNumberPressed = onNumberPressed
        )

        Spacer(Modifier.width(16.dp))

        CircleNumberButton(
            forCalculator = forCalculator,
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
            forCalculator = forCalculator,
            value = "1",
            onNumberPressed = onNumberPressed
        )

        Spacer(Modifier.width(16.dp))

        CircleNumberButton(
            forCalculator = forCalculator,
            value = "2",
            onNumberPressed = onNumberPressed
        )

        Spacer(Modifier.width(16.dp))

        CircleNumberButton(
            forCalculator = forCalculator,
            value = "3",
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
        KeypadCircleButton(
            text = localDecimalSeparator(),
            testTag = if (forCalculator)
                "calc_key_decimal_separator" else "key_decimal_separator"
        ) {
            onDecimalPoint()
        }

        Spacer(Modifier.width(16.dp))

        CircleNumberButton(
            forCalculator = forCalculator,
            value = "0",
            onNumberPressed = onNumberPressed
        )

        Spacer(Modifier.width(16.dp))

        IvyIcon(
            modifier = circleButtonModifier(onClick = onBackspace)
                .padding(all = 16.dp)
                .testTag("key_del"),
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
    forCalculator: Boolean,
    value: String,
    onNumberPressed: (String) -> Unit,
) {
    KeypadCircleButton(
        text = value,
        testTag = if (forCalculator)
            "calc_key_${value}" else "key_${value}",
        onClick = {
            onNumberPressed(value)
        }
    )
}

@Composable
fun KeypadCircleButton(
    text: String,
    textColor: Color = UI.colors.pureInverse,
    testTag: String,
    onClick: () -> Unit
) {
    Text(
        modifier = circleButtonModifier(onClick = onClick)
            .padding(top = 10.dp)
            .testTag(testTag),
        text = text,
        style = UI.typo.nH2.style(
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
//        .drawColoredShadow(
//            color = Black,
//            alpha = if (UI.colors.isLight) 0.05f else 0.5f,
//            borderRadius = 32.dp
//        )
        .clip(CircleShape)
        .clickable(
            onClick = onClick
        )
        .background(UI.colors.pure, UI.shapes.rFull)
        .border(2.dp, UI.colors.medium, UI.shapes.rFull)
}

@Preview
@Composable
private fun Preview() {
    com.ivy.core.ui.temp.Preview {
        BoxWithConstraints(
            modifier = Modifier.padding(bottom = modalPreviewActionRowHeight())
        ) {
            AmountModal(
                id = UUID.randomUUID(),
                visible = true,
                currency = "BGN",
                initialAmount = null,
                dismiss = { }
            ) {

            }
        }
    }
}