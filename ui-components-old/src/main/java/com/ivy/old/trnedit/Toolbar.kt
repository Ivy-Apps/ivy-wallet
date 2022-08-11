package com.ivy.wallet.ui.edit.core

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ivy.base.R
import com.ivy.data.transaction.TrnType
import com.ivy.frp.view.navigation.navigation
import com.ivy.wallet.ui.theme.components.CloseButton
import com.ivy.wallet.ui.theme.components.DeleteButton
import com.ivy.wallet.ui.theme.components.IvyOutlinedButton
import java.util.*

@Composable
fun Toolbar(
    type: TrnType,
    initialTransactionId: UUID?,

    onDeleteTrnModal: () -> Unit,
    onChangeTransactionTypeModal: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(24.dp))

        val nav = navigation()
        CloseButton {
            nav.back()
        }

        Spacer(Modifier.weight(1f))

        when (type) {
            TrnType.INCOME -> {
                IvyOutlinedButton(
                    text = stringResource(R.string.income),
                    iconStart = R.drawable.ic_income
                ) {
                    onChangeTransactionTypeModal()
                }

                Spacer(Modifier.width(12.dp))
            }
            TrnType.EXPENSE -> {
                IvyOutlinedButton(
                    text = stringResource(R.string.expense),
                    iconStart = R.drawable.ic_expense
                ) {
                    onChangeTransactionTypeModal()
                }

                Spacer(Modifier.width(12.dp))
            }
            else -> {
                //show nothing
            }
        }

        if (initialTransactionId != null) {

            DeleteButton(
                hasShadow = false
            ) {
                onDeleteTrnModal()
            }

            Spacer(Modifier.width(24.dp))
        }
    }
}
