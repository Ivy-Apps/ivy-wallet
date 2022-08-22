package com.ivy.wallet.ui.theme.modal.edit

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.base.R
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.wallet.ui.theme.Gray
import com.ivy.wallet.ui.theme.Red
import com.ivy.wallet.ui.theme.modal.IvyModal
import com.ivy.wallet.ui.theme.modal.ModalSet
import com.ivy.wallet.ui.theme.modal.ModalTitle
import com.ivy.wallet.utils.*
import com.notkamui.keval.Keval
import java.util.*

@Composable
fun BoxWithConstraintsScope.CalculatorModal(
    id: UUID = UUID.randomUUID(),
    initialAmount: Double?,
    visible: Boolean,
    currency: String,

    dismiss: () -> Unit,
    onCalculation: (Double) -> Unit
) {
    var expression by remember(id, initialAmount) {
        mutableStateOf(initialAmount?.format(currency) ?: "")
    }

    IvyModal(
        id = id,
        visible = visible,
        dismiss = dismiss,
        PrimaryAction = {
            ModalSet(
                modifier = Modifier.testTag("calc_set")
            ) {
                val result = calculate(expression)
                if (result != null) {
                    onCalculation(result)
                    dismiss()
                }
            }
        }
    ) {
        Spacer(Modifier.height(32.dp))

        ModalTitle(text = stringResource(R.string.calculator))

        Spacer(Modifier.height(32.dp))

        val isEmpty = expression.isBlank()
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            text = if (isEmpty) stringResource(R.string.calculator_empty_expression) else expression,
            style = UI.typo.nH2.style(
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = if (isEmpty) Gray else UI.colors.pureInverse
            )
        )

        Spacer(Modifier.height(32.dp))

        AmountKeyboard(
            forCalculator = true,
            ZeroRow = {
                KeypadCircleButton(
                    text = "C",
                    textColor = Red,
                    testTag = "key_C"
                ) {
                    expression = ""
                }

                Spacer(Modifier.width(16.dp))

                KeypadCircleButton(
                    text = "(",
                    testTag = "key_("
                ) {
                    expression += "("
                }

                Spacer(Modifier.width(16.dp))

                KeypadCircleButton(
                    text = ")",
                    testTag = "key_)"
                ) {
                    expression += ")"
                }

                Spacer(Modifier.width(16.dp))

                KeypadCircleButton(
                    text = "/",
                    testTag = "key_/"
                ) {
                    expression += "/"
                }
            },
            FirstRowExtra = {
                KeypadCircleButton(
                    text = "*",
                    testTag = "key_*"
                ) {
                    expression += "*"
                }
            },
            SecondRowExtra = {
                KeypadCircleButton(
                    text = "-",
                    testTag = "key_-"
                ) {
                    expression += "-"
                }
            },
            ThirdRowExtra = {
                KeypadCircleButton(
                    text = "+",
                    testTag = "key_+"
                ) {
                    expression += "+"
                }
            },
            FourthRowExtra = {
                KeypadCircleButton(
                    text = "=",
                    testTag = "key_="
                ) {
                    val result = calculate(expression)
                    if (result != null) {
                        expression = result.format(currency)
                    }
                }
            },

            onNumberPressed = {
                expression = formatExpression(
                    expression = expression + it,
                )
            },
            onDecimalPoint = {
                expression = formatExpression(
                    expression = expression + localDecimalSeparator(),
                )
            },
            onBackspace = {
                if (expression.isNotEmpty()) {
                    expression = expression.dropLast(1)
                }
            }
        )

        Spacer(Modifier.height(24.dp))
    }
}

private fun formatExpression(expression: String): String {
    var formattedExpression = expression

    expression
        .split("(", ")", "/", "*", "-", "+")
        .ifEmpty {
            //handle only number expression formatting
            listOf(expression)
        }
        .forEach { part ->
            val formattedPart = removeExtraDecimals(part)

            val numberPart = formattedPart
                .amountToDoubleOrNull()
            if (numberPart != null) {
                formattedExpression = formattedExpression.replace(part, formattedPart)
            }
        }

    return formattedExpression
}

private fun calculate(expression: String): Double? {
    return try {
        Keval.eval(expression.normalizeExpression())
    } catch (e: Exception) {
        null
    }
}

@Preview
@Composable
private fun Preview() {
    com.ivy.core.ui.temp.Preview {
        CalculatorModal(
            visible = true,
            initialAmount = 50.23,
            currency = "BGN",
            dismiss = { },
            onCalculation = {}
        )
    }
}