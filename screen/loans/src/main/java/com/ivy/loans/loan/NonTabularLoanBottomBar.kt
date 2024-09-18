package com.ivy.loans.loan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.legacy.IvyWalletPreview
import com.ivy.ui.R
import com.ivy.wallet.ui.theme.Blue
import com.ivy.wallet.ui.theme.components.BackBottomBar
import com.ivy.wallet.ui.theme.components.IvyButton
import com.ivy.wallet.ui.theme.components.IvyCircleButton

@Composable
internal fun BoxWithConstraintsScope.NonTabularLoanBottomBar(
    isPaidOffLoanVisible: Boolean,
    onClose: () -> Unit,
    onAdd: () -> Unit,
    onTogglePaidOffLoanVisibility: () -> Unit
) {
    BackBottomBar(onBack = onClose) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // TODO: Add icon content description - need to update
            IvyCircleButton(
                icon = when (isPaidOffLoanVisible) {
                    true -> R.drawable.ic_visible
                    else -> R.drawable.ic_hidden
                },
                backgroundPadding = 10.dp
            ) {
                onTogglePaidOffLoanVisibility()
            }

            Spacer(Modifier.width(12.dp))

            IvyButton(
                text = stringResource(R.string.add_loan),
                iconStart = R.drawable.ic_plus
            ) {
                onAdd()
            }
        }
    }
}

@Preview
@Composable
private fun PreviewNonTabularBottomBar() {
    IvyWalletPreview {
        Column(
            Modifier
                .fillMaxSize()
                .background(Blue)
        ) {
        }

        NonTabularLoanBottomBar(
            isPaidOffLoanVisible = false,
            onAdd = {},
            onClose = {},
            onTogglePaidOffLoanVisibility = {}
        )
    }
}
