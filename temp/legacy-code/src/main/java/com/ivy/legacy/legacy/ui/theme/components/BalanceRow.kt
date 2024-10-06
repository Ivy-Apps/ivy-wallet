package com.ivy.wallet.ui.theme.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.legacy.IvyWalletComponentPreview
import com.ivy.legacy.utils.toDecimalFormat
import com.ivy.legacy.utils.toDecimalFormatWithDecimalPlaces
import kotlinx.coroutines.launch

@Deprecated("Old design system. Use `:ivy-design` and Material3")
@Composable
fun BalanceRowMedium(
    currency: String,
    balance: Double,
    modifier: Modifier = Modifier,
    textColor: Color = UI.colors.pureInverse,
    currencyUpfront: Boolean = true,
    hiddenMode: Boolean = false,
) {
    BalanceRow(
        modifier = modifier,

        textColor = textColor,
        currency = currency,
        balance = balance,
        hiddenMode = hiddenMode,
        spacerCurrency = 12.dp,
        currencyFontSize = 24.sp,
        balanceFontSize = 26.sp,
        currencyUpfront = currencyUpfront
    )
}

@Composable
fun BalanceRowMini(
    currency: String,
    balance: Double,
    modifier: Modifier = Modifier,
    textColor: Color = UI.colors.pureInverse,
    currencyUpfront: Boolean = true,
    hiddenMode: Boolean = false,
    doubleRowDisplay: Boolean = false,
) {
    BalanceRow(
        modifier = modifier,

        textColor = textColor,
        currency = currency,
        balance = balance,
        hiddenMode = hiddenMode,
        spacerCurrency = 8.dp,
        currencyFontSize = 20.sp,
        balanceFontSize = 22.sp,
        currencyUpfront = currencyUpfront,
        doubleRowDisplay = doubleRowDisplay
    )
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun BalanceRow(
    currency: String,
    balance: Double,
    modifier: Modifier = Modifier,
    textColor: Color = UI.colors.pureInverse,
    hiddenMode: Boolean = false,
    spacerCurrency: Dp = 12.dp,
    currencyFontSize: TextUnit? = null,
    balanceFontSize: TextUnit? = null,
    currencyUpfront: Boolean = true,
    doubleRowDisplay: Boolean = false,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var formattedBalance by remember {
        mutableStateOf(balance.toDecimalFormatWithDecimalPlaces(context))
    }
    scope.launch {
        formattedBalance = balance.toDecimalFormat(context)
    }
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (doubleRowDisplay) {
                Currency(
                    currency = currency,
                    textColor = textColor,
                    currencyFontSize = currencyFontSize
                )
                Spacer(Modifier.width(spacerCurrency))
            }

            if (!doubleRowDisplay) {
                val balanceText = when {
                    hiddenMode -> "****"
                    else -> formattedBalance
                }

                val balanceCurrencyText = if (currencyUpfront) {
                    "$currency $balanceText"
                } else {
                    "$balanceText $currency"
                }

                Text(
                    text = balanceCurrencyText,
                    style = if (balanceFontSize == null) {
                        UI.typo.nH1.style(
                            fontWeight = FontWeight.ExtraBold,
                            color = textColor
                        )
                    } else {
                        UI.typo.nH1.style(
                            fontWeight = FontWeight.ExtraBold,
                            color = textColor
                        ).copy(fontSize = balanceFontSize)
                    }
                )
            }
        }

        if (doubleRowDisplay) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = when {
                        hiddenMode -> "****"
                        else -> formattedBalance
                    },
                    style = if (balanceFontSize == null) {
                        UI.typo.nH1.style(
                            fontWeight = FontWeight.ExtraBold,
                            color = textColor
                        )
                    } else {
                        UI.typo.nH1.style(
                            fontWeight = FontWeight.ExtraBold,
                            color = textColor
                        ).copy(fontSize = balanceFontSize)
                    }
                )
            }
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
            balance = 3520000.60
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
            balance = 3520.60
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
            balance = 3520.60
        )
    }
}