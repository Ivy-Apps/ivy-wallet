package com.ivy.core.ui.value

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.ivy.data.transaction.Value
import com.ivy.wallet.utils.format
import com.ivy.wallet.utils.shortenAmount
import com.ivy.wallet.utils.shouldShortAmount


@Composable
fun Value.formatAmount(
    shortenBigNumbers: Boolean
) = remember(amount, shortenBigNumbers, currency) {
    val shortAmount = shortenBigNumbers && shouldShortAmount(amount)
    if (shortAmount) shortenAmount(amount) else amount.format(currency)
}