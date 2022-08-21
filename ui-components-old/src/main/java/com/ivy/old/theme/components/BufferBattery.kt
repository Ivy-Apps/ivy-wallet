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
import com.ivy.wallet.utils.thenIf
import kotlin.math.abs

@Composable
fun BufferBattery(
    modifier: Modifier = Modifier,
    buffer: Double,
    balance: Double,
    currency: String,
    backgroundNotFilled: Color = UI.colors.pure,
    onClick: (() -> Unit)? = null,
) {
    val bufferExceeded = balance < buffer

    val leftToSpend = balance - buffer
    val bufferExceededPercent = if (balance != 0.0) {
        (balance - leftToSpend) / balance
    } else {
        1.0
    }

    val textColor = when {
        bufferExceededPercent <= 0.25 -> {
            UI.colors.pureInverse
        }
        bufferExceededPercent <= 0.50 -> {
            White
        }
        bufferExceededPercent <= 0.75 -> {
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
                        bufferExceededPercent <= 0.25 -> {
                            Green
                        }
                        bufferExceededPercent <= 0.50 -> {
                            Ivy
                        }
                        bufferExceededPercent <= 0.75 -> {
                            Orange
                        }
                        else -> Red
                    },
                    size = size.copy(
                        width = (size.width * bufferExceededPercent).toFloat()
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
            icon = if (bufferExceeded) R.drawable.ic_buffer_exceeded else R.drawable.ic_buffer_ok,
            tint = textColor
        )

        Spacer(Modifier.width(16.dp))

        Column {
            Text(
                text = if (bufferExceeded) stringResource(R.string.buffer_exceeded_by) else stringResource(
                    R.string.left_to_spend
                ),
                style = UI.typo.c.style(
                    color = textColor,
                    fontWeight = FontWeight.ExtraBold
                )
            )

            Spacer(Modifier.height(4.dp))

            AmountCurrencyB2Row(
                amount = abs(leftToSpend),
                currency = currency,
                textColor = textColor
            )
        }
    }
}

@Preview
@Composable
private fun Preview_buffer_0() {
    com.ivy.core.ui.temp.ComponentPreview {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(UI.colors.medium),
            contentAlignment = Alignment.Center
        ) {
            BufferBattery(
                modifier = Modifier.padding(horizontal = 32.dp),
                buffer = 0.0,
                balance = 100000.0,
                currency = "BGN"
            )
        }

    }
}

@Preview
@Composable
private fun Preview_buffer_balance_0() {
    com.ivy.core.ui.temp.ComponentPreview {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(UI.colors.medium),
            contentAlignment = Alignment.Center
        ) {
            BufferBattery(
                modifier = Modifier.padding(horizontal = 32.dp),
                buffer = 5000.0,
                balance = 0.0,
                currency = "BGN"
            )
        }

    }
}

@Preview
@Composable
private fun Preview_buffer_very_low() {
    com.ivy.core.ui.temp.ComponentPreview {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(UI.colors.medium),
            contentAlignment = Alignment.Center
        ) {
            BufferBattery(
                modifier = Modifier.padding(horizontal = 32.dp),
                buffer = 5000.0,
                balance = 100000.0,
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
            BufferBattery(
                modifier = Modifier.padding(horizontal = 32.dp),
                buffer = 5000.0,
                balance = 20000.0,
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
            BufferBattery(
                modifier = Modifier.padding(horizontal = 32.dp),
                buffer = 5000.0,
                balance = 10000.0,
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
            BufferBattery(
                modifier = Modifier.padding(horizontal = 32.dp),
                buffer = 5000.0,
                balance = 7500.0,
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
            BufferBattery(
                modifier = Modifier.padding(horizontal = 32.dp),
                buffer = 5000.0,
                balance = 5500.0,
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
            BufferBattery(
                modifier = Modifier.padding(horizontal = 32.dp),
                buffer = 5000.0,
                balance = 5000.0,
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
            BufferBattery(
                modifier = Modifier.padding(horizontal = 32.dp),
                buffer = 5000.0,
                balance = 2500.0,
                currency = "BGN"
            )
        }

    }
}