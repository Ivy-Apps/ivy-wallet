package com.ivy.wallet.ui.theme.wallet

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.wallet.utils.format
import com.ivy.wallet.utils.shortenAmount
import com.ivy.wallet.utils.shouldShortAmount


@Composable
fun AmountCurrencyB2Row(
    amount: Double,
    currency: String,
    amountFontWeight: FontWeight = FontWeight.ExtraBold,
    textColor: Color = UI.colors.pureInverse
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = amount.format(currency),
            style = UI.typo.nB2.style(
                fontWeight = amountFontWeight,
                color = textColor
            )
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = currency,
            style = UI.typo.nB2.style(
                fontWeight = FontWeight.Normal,
                color = textColor
            )
        )
    }
}

@Composable
fun AmountCurrencyB1Row(
    amount: Double,
    currency: String,
    amountFontWeight: FontWeight = FontWeight.Bold,
    textColor: Color = UI.colors.pureInverse
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        AmountCurrencyB1(
            amount = amount,
            currency = currency,
            amountFontWeight = amountFontWeight,
            textColor = textColor
        )
    }
}


@Composable
fun AmountCurrencyB1(
    amount: Double,
    currency: String,
    amountFontWeight: FontWeight = FontWeight.Bold,
    textColor: Color = UI.colors.pureInverse,
    shortenBigNumbers: Boolean = false
) {
    val shortAmount = shortenBigNumbers && shouldShortAmount(amount)

    Text(
        modifier = Modifier.testTag("amount_currency_b1"),
        text = if (shortAmount) shortenAmount(amount) else amount.format(currency),
        style = UI.typo.nB1.style(
            fontWeight = amountFontWeight,
            color = textColor
        )
    )
    Spacer(modifier = Modifier.width(4.dp))
    Text(
        text = currency,
        style = UI.typo.nB1.style(
            fontWeight = FontWeight.Normal,
            color = textColor
        )
    )
}

@Composable
fun AmountCurrencyH1(
    amount: Double,
    currency: String,
    textColor: Color = UI.colors.pureInverse
) {
    Text(
        text = amount.format(currency),
        style = UI.typo.nH1.style(
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    )
    Spacer(modifier = Modifier.width(4.dp))
    Text(
        text = currency,
        style = UI.typo.nH2.style(
            fontWeight = FontWeight.Normal,
            color = textColor
        )
    )
}

@Composable
fun AmountCurrencyH2Row(
    amount: Double,
    currency: String,
    textColor: Color = UI.colors.pureInverse
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = amount.format(currency),
            style = UI.typo.nH2.style(
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = currency,
            style = UI.typo.b1.style(
                fontWeight = FontWeight.Normal,
                color = textColor
            )
        )
    }
}

@Composable
fun AmountCurrencyCaption(
    amount: Double,
    currency: String,
    amountFontWeight: FontWeight = FontWeight.ExtraBold,
    textColor: Color = UI.colors.pureInverse
) {
    Text(
        text = amount.format(currency),
        style = UI.typo.nC.style(
            fontWeight = amountFontWeight,
            color = textColor
        )
    )
    Spacer(modifier = Modifier.width(4.dp))
    Text(
        text = currency,
        style = UI.typo.nC.style(
            fontWeight = FontWeight.Normal,
            color = textColor
        )
    )
}