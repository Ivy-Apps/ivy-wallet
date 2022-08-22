package com.ivy.wallet.ui.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.base.R
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.wallet.ui.theme.*
import com.ivy.wallet.ui.theme.wallet.AmountCurrencyB2Row
import com.ivy.wallet.utils.format
import com.ivy.wallet.utils.thenIf
import kotlin.math.abs

@Composable
fun BudgetBattery(
    modifier: Modifier = Modifier,
    currency: String,
    expenses: Double,
    budget: Double,
    backgroundNotFilled: Color = UI.colors.pure,
    onClick: (() -> Unit)? = null,
) {
    if (budget == 0.0) return
    val percentSpent = expenses / budget

    val textColor = when {
        percentSpent <= 0.30 -> {
            UI.colors.pureInverse
        }
        percentSpent <= 0.50 -> {
            White
        }
        percentSpent <= 0.75 -> {
            White
        }
        else -> White
    }

    val captionTextColor = when {
        percentSpent <= 0.30 -> {
            UI.colors.mediumInverse
        }
        percentSpent <= 0.50 -> {
            White
        }
        percentSpent <= 0.75 -> {
            White
        }
        else -> White
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(UI.shapes.r4)
            .background(backgroundNotFilled)
            .drawBehind {
                drawRect(
                    color = when {
                        percentSpent <= 0.25 -> {
                            Green
                        }
                        percentSpent <= 0.50 -> {
                            Ivy
                        }
                        percentSpent <= 0.75 -> {
                            Orange
                        }
                        else -> Red
                    },
                    size = size.copy(
                        width = (size.width * percentSpent).toFloat()
                    )
                )
            }
            .thenIf(onClick != null) {
                clickable {
                    onClick?.invoke()
                }
            }
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(16.dp))

        IvyIcon(
            icon = if (percentSpent > 1.0) R.drawable.ic_buffer_exceeded else R.drawable.ic_buffer_ok,
            tint = textColor
        )

        Spacer(Modifier.width(16.dp))

        Column {
            Text(
                text = when {
                    percentSpent <= 1 -> {
                        stringResource(R.string.left_to_spend)
                    }
                    else -> stringResource(R.string.budget_exceeded_by)
                },
                style = UI.typo.c.style(
                    color = textColor,
                    fontWeight = FontWeight.ExtraBold
                )
            )

            Spacer(Modifier.height(4.dp))

            AmountCurrencyB2Row(
                amount = abs(budget - expenses),
                currency = currency,
                textColor = textColor
            )

            Spacer(Modifier.height(2.dp))

            Text(
                text = "${expenses.format(currency)}/${budget.format(currency)} $currency",
                style = UI.typo.nC.style(
                    fontWeight = FontWeight.ExtraBold,
                    color = captionTextColor
                )
            )
        }
    }
}

@Preview
@Composable
private fun Preview_budget_0() {
    com.ivy.core.ui.temp.ComponentPreview {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(UI.colors.medium),
            contentAlignment = Alignment.Center
        ) {
            BudgetBattery(
                modifier = Modifier.padding(horizontal = 32.dp),
                budget = 0.0,
                expenses = 100.45,
                currency = "BGN"
            )
        }

    }
}

@Preview
@Composable
private fun Preview_expenses_0() {
    com.ivy.core.ui.temp.ComponentPreview {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(UI.colors.medium),
            contentAlignment = Alignment.Center
        ) {
            BudgetBattery(
                modifier = Modifier.padding(horizontal = 32.dp),
                budget = 1000.0,
                expenses = 0.0,
                currency = "BGN"
            )
        }

    }
}

@Preview
@Composable
private fun Preview_spent_very_low() {
    com.ivy.core.ui.temp.ComponentPreview {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(UI.colors.medium),
            contentAlignment = Alignment.Center
        ) {
            BudgetBattery(
                modifier = Modifier.padding(horizontal = 32.dp),
                expenses = 5000.0,
                budget = 100000.0,
                currency = "BGN"
            )
        }

    }
}

@Preview
@Composable
private fun Preview_buffer_25() {
    com.ivy.core.ui.temp.ComponentPreview {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(UI.colors.medium),
            contentAlignment = Alignment.Center
        ) {
            BudgetBattery(
                modifier = Modifier.padding(horizontal = 32.dp),
                expenses = 5000.0,
                budget = 20000.0,
                currency = "BGN"
            )
        }

    }
}

@Preview
@Composable
private fun Preview_buffer_50() {
    com.ivy.core.ui.temp.ComponentPreview {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(UI.colors.medium),
            contentAlignment = Alignment.Center
        ) {
            BudgetBattery(
                modifier = Modifier.padding(horizontal = 32.dp),
                expenses = 5000.0,
                budget = 10000.0,
                currency = "BGN"
            )
        }

    }
}

@Preview
@Composable
private fun Preview_buffer_75() {
    com.ivy.core.ui.temp.ComponentPreview {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(UI.colors.medium),
            contentAlignment = Alignment.Center
        ) {
            BudgetBattery(
                modifier = Modifier.padding(horizontal = 32.dp),
                expenses = 5000.0,
                budget = 7500.0,
                currency = "BGN"
            )
        }

    }
}

@Preview
@Composable
private fun Preview_buffer_90() {
    com.ivy.core.ui.temp.ComponentPreview {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(UI.colors.medium),
            contentAlignment = Alignment.Center
        ) {
            BudgetBattery(
                modifier = Modifier.padding(horizontal = 32.dp),
                expenses = 5000.0,
                budget = 5500.0,
                currency = "BGN"
            )
        }

    }
}

@Preview
@Composable
private fun Preview_buffer_100() {
    com.ivy.core.ui.temp.ComponentPreview {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(UI.colors.medium),
            contentAlignment = Alignment.Center
        ) {
            BudgetBattery(
                modifier = Modifier.padding(horizontal = 32.dp),
                expenses = 5000.0,
                budget = 5000.0,
                currency = "BGN"
            )
        }

    }
}

@Preview
@Composable
private fun Preview_buffer_125() {
    com.ivy.core.ui.temp.ComponentPreview {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(UI.colors.medium),
            contentAlignment = Alignment.Center
        ) {
            BudgetBattery(
                modifier = Modifier.padding(horizontal = 32.dp),
                expenses = 5000.0,
                budget = 2500.0,
                currency = "BGN"
            )
        }

    }
}

@Preview
@Composable
private fun Preview_expenses_negative() {
    com.ivy.core.ui.temp.ComponentPreview {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(UI.colors.medium),
            contentAlignment = Alignment.Center
        ) {
            BudgetBattery(
                modifier = Modifier.padding(horizontal = 32.dp),
                expenses = -348.54,
                budget = 1000.0,
                currency = "BGN"
            )
        }

    }
}