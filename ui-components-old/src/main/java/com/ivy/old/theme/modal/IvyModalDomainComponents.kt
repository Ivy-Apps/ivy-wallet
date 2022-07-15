package com.ivy.wallet.ui.theme.modal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.wallet.ui.theme.components.BalanceRow
import com.ivy.wallet.ui.theme.components.IvyDividerLine
import com.ivy.wallet.utils.clickableNoIndication

@Composable
fun ModalAmountSection(
    label: String,
    currency: String,
    amount: Double,
    Header: (@Composable () -> Unit)? = null,
    amountPaddingTop: Dp = 48.dp,
    amountPaddingBottom: Dp = 48.dp,
    showAmountModal: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IvyDividerLine()

        Header?.invoke()

        Spacer(Modifier.height(amountPaddingTop))

        Text(
            text = label,
            style = UI.typo.c.style(
                color = UI.colors.gray,
                fontWeight = FontWeight.ExtraBold
            )
        )

        Spacer(Modifier.height(4.dp))

        BalanceRow(
            modifier = Modifier
                .clickableNoIndication {
                    showAmountModal()
                }
                .testTag("amount_balance"),
            currency = currency,
            balance = amount,

            decimalPaddingTop = 8.dp,
            spacerDecimal = 4.dp,
            spacerCurrency = 8.dp,


            integerFontSize = 40.sp,
            decimalFontSize = 18.sp,
            currencyFontSize = 30.sp,

            currencyUpfront = false
        )

        Spacer(Modifier.height(amountPaddingBottom))
    }
}
