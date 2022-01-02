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
import com.ivy.wallet.base.decimalPartFormatted
import com.ivy.wallet.base.shortenAmount
import com.ivy.wallet.base.shouldShortAmount
import com.ivy.wallet.ui.theme.IvyComponentPreview
import com.ivy.wallet.ui.theme.IvyTheme
import com.ivy.wallet.ui.theme.Typo
import com.ivy.wallet.ui.theme.style
import java.text.DecimalFormat
import kotlin.math.truncate

@Composable
fun BalanceRowMedium(
    modifier: Modifier = Modifier,
    textColor: Color = IvyTheme.colors.pureInverse,
    currency: String,
    balance: Double,
    balanceAmountPrefix: String? = null,
    currencyUpfront: Boolean = true,
    shortenBigNumbers: Boolean = false,
) {
    BalanceRow(
        modifier = modifier,

        decimalPaddingTop = 8.dp,
        textColor = textColor,
        currency = currency,
        balance = balance,
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
    textColor: Color = IvyTheme.colors.pureInverse,
    currency: String,
    balance: Double,
    balanceAmountPrefix: String? = null,
    currencyUpfront: Boolean = true,
    shortenBigNumbers: Boolean = false,
) {
    BalanceRow(
        modifier = modifier,

        decimalPaddingTop = 6.dp,
        textColor = textColor,
        currency = currency,
        balance = balance,
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

    textColor: Color = IvyTheme.colors.pureInverse,
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

        val integerPartFormatted = if (shortAmount) {
            shortenAmount(balance)
        } else {
            DecimalFormat("###,###").format(truncate(balance))
        }
        Text(
            text = if (balanceAmountPrefix != null)
                "$balanceAmountPrefix$integerPartFormatted" else integerPartFormatted,
            style = if (integerFontSize == null) {
                Typo.numberH1.style(
                    fontWeight = FontWeight.ExtraBold,
                    color = textColor
                )
            } else {
                Typo.numberH1.style(
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
                text = decimalPartFormatted(currency, balance),
                style = if (decimalFontSize == null) {
                    Typo.numberBody1.style(
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                } else {
                    Typo.numberBody1.style(
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
            Typo.h1.style(
                fontWeight = FontWeight.Light,
                color = textColor
            )
        } else {
            Typo.h1.style(
                fontWeight = FontWeight.Light,
                color = textColor
            ).copy(fontSize = currencyFontSize)
        }
    )
}

@Preview
@Composable
private fun Preview_Default() {
    IvyComponentPreview {
        BalanceRow(
            textColor = IvyTheme.colors.pureInverse,
            currency = "BGN",
            balance = 3520.60,
            balanceAmountPrefix = null
        )
    }
}

@Preview
@Composable
private fun Preview_Medium() {
    IvyComponentPreview {
        BalanceRowMedium(
            textColor = IvyTheme.colors.pureInverse,
            currency = "BGN",
            balance = 3520.60,
            balanceAmountPrefix = null
        )
    }
}

@Preview
@Composable
private fun Preview_Mini() {
    IvyComponentPreview {
        BalanceRowMini(
            textColor = IvyTheme.colors.pureInverse,
            currency = "BGN",
            balance = 3520.60,
            balanceAmountPrefix = null
        )
    }
}