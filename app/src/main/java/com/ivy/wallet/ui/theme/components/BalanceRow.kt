package com.ivy.wallet.ui.theme.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.wallet.ui.IvyWalletComponentPreview
import com.ivy.wallet.utils.decimalPartFormatted
import com.ivy.wallet.utils.shortenAmount
import com.ivy.wallet.utils.shouldShortAmount
import java.text.DecimalFormat

@Composable
fun BalanceRowMedium(
    modifier: Modifier = Modifier,
    textColor: Color = UI.colors.pureInverse,
    currency: String,
    balance: Double,
    balanceAmountPrefix: String? = null,
    currencyUpfront: Boolean = true,
    shortenBigNumbers: Boolean = false,
    hiddenMode: Boolean = false,
) {
    BalanceRow(
        modifier = modifier,

        decimalPaddingTop = 8.dp,
        textColor = textColor,
        currency = currency,
        balance = balance,
        hiddenMode = hiddenMode,
        spacerCurrency = 12.dp,
        spacerDecimal = 8.dp,
        currencyFontSize = 24.sp,
        integerFontSize = 26.sp,
        decimalFontSize = 11.sp,

        balanceAmountPrefix = balanceAmountPrefix,
        currencyUpfront = currencyUpfront,
        shortenBigNumbers = shortenBigNumbers
    )
}

@Composable
fun BalanceRowMini(
    modifier: Modifier = Modifier,
    textColor: Color = UI.colors.pureInverse,
    currency: String,
    balance: Double,
    balanceAmountPrefix: String? = null,
    currencyUpfront: Boolean = true,
    shortenBigNumbers: Boolean = false,
    hiddenMode: Boolean = false,
) {
    BalanceRow(
        modifier = modifier,

        decimalPaddingTop = 6.dp,
        textColor = textColor,
        currency = currency,
        balance = balance,
        hiddenMode = hiddenMode,
        spacerCurrency = 8.dp,
        spacerDecimal = 4.dp,
        currencyFontSize = 20.sp,
        integerFontSize = 22.sp,
        decimalFontSize = 7.sp,

        balanceAmountPrefix = balanceAmountPrefix,
        currencyUpfront = currencyUpfront,
        shortenBigNumbers = shortenBigNumbers
    )
}

@Composable
fun BalanceRow(
    modifier: Modifier = Modifier,
    currency: String,
    balance: Double,
    hiddenMode: Boolean = false,

    textColor: Color = UI.colors.pureInverse,
    decimalPaddingTop: Dp = 12.dp,
    spacerCurrency: Dp = 12.dp,
    spacerDecimal: Dp = 8.dp,
    currencyFontSize: TextUnit? = null,
    integerFontSize: TextUnit? = null,
    decimalFontSize: TextUnit? = null,

    currencyUpfront: Boolean = true,
    balanceAmountPrefix: String? = null,
    shortenBigNumbers: Boolean = false,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val shortAmount = shortenBigNumbers && shouldShortAmount(balance)

        if (currencyUpfront) {
            Currency(
                currency = currency,
                textColor = textColor,
                currencyFontSize = currencyFontSize
            )

            Spacer(Modifier.width(spacerCurrency))
        }

        val balancePrecise = balance.toBigDecimal()

        val integerPartFormatted = if (shortAmount) {
            shortenAmount(balance)
        } else {
            DecimalFormat("###,###").format(balancePrecise.toInt())
        }
        Text(
            text = when {
                hiddenMode -> "****"
                balanceAmountPrefix != null -> "$balanceAmountPrefix$integerPartFormatted"
                else -> integerPartFormatted
            },
            style = if (integerFontSize == null) {
                UI.typo.nH1.style(
                    fontWeight = FontWeight.ExtraBold,
                    color = textColor
                )
            } else {
                UI.typo.nH1.style(
                    fontWeight = FontWeight.ExtraBold,
                    color = textColor
                ).copy(fontSize = integerFontSize)
            }
        )

        if (!shortAmount) {
            Spacer(Modifier.width(spacerDecimal))

            Text(
                modifier = Modifier
                    .align(Alignment.Top)
                    .padding(top = decimalPaddingTop),
                text = if (hiddenMode) "" else decimalPartFormatted(currency, balance),
                style = if (decimalFontSize == null) {
                    UI.typo.nB1.style(
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                } else {
                    UI.typo.nB1.style(
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    ).copy(fontSize = decimalFontSize)
                }
            )
        }


        if (!currencyUpfront) {
            Spacer(Modifier.width(spacerCurrency))

            Currency(
                currency = currency,
                textColor = textColor,
                currencyFontSize = currencyFontSize
            )
        }
    }
}

@Composable
private fun Currency(
    currency: String,
    currencyFontSize: TextUnit?,
    textColor: Color,
) {
    Text(
        text = currency,
        style = if (currencyFontSize == null) {
            UI.typo.h1.style(
                fontWeight = FontWeight.Light,
                color = textColor
            )
        } else {
            UI.typo.h1.style(
                fontWeight = FontWeight.Light,
                color = textColor
            ).copy(fontSize = currencyFontSize)
        }
    )
}

@Preview
@Composable
private fun Preview_Default() {
    IvyWalletComponentPreview {
        BalanceRow(
            textColor = UI.colors.pureInverse,
            currency = "BGN",
            balance = 3520.60,
            balanceAmountPrefix = null
        )
    }
}

@Preview
@Composable
private fun Preview_Medium() {
    IvyWalletComponentPreview {
        BalanceRowMedium(
            textColor = UI.colors.pureInverse,
            currency = "BGN",
            balance = 3520.60,
            balanceAmountPrefix = null
        )
    }
}

@Preview
@Composable
private fun Preview_Mini() {
    IvyWalletComponentPreview {
        BalanceRowMini(
            textColor = UI.colors.pureInverse,
            currency = "BGN",
            balance = 3520.60,
            balanceAmountPrefix = null
        )
    }
}