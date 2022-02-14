package com.ivy.wallet.ui.bankintegrations

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.insets.systemBarsPadding
import com.ivy.wallet.base.OpResult
import com.ivy.wallet.base.onScreenStart
import com.ivy.wallet.ui.ConnectBank
import com.ivy.wallet.ui.IvyActivity
import com.ivy.wallet.ui.IvyAppPreview
import com.ivy.wallet.ui.theme.Orange
import com.ivy.wallet.ui.theme.Typo
import com.ivy.wallet.ui.theme.components.IvyButton
import com.ivy.wallet.ui.theme.components.IvySwitch
import com.ivy.wallet.ui.theme.style

@Composable
fun BoxWithConstraintsScope.ConnectBankScreen(screen: ConnectBank) {
    val viewModel: ConnectBankViewModel = viewModel()

    val opSyncTransactions by viewModel.opSyncTransactions.observeAsState()
    val bankSyncEnabled by viewModel.bankSyncEnabled.observeAsState(false)

    onScreenStart {
        viewModel.start()
    }

    val ivyActivity = LocalContext.current as IvyActivity
    UI(
        opSyncTransactions = opSyncTransactions,
        bankSyncEnabled = bankSyncEnabled,

        onConnect = {
            viewModel.connectBank(
                ivyActivity = ivyActivity
            )
        },
        onFetchTransactions = viewModel::syncTransactions,
        onRemoveCustomer = viewModel::removeCustomer,
        onSetBankSyncEnabled = viewModel::setBankSyncEnabled
    )
}

@Composable
private fun UI(
    opSyncTransactions: OpResult<Unit>?,
    bankSyncEnabled: Boolean,

    onConnect: () -> Unit = {},
    onFetchTransactions: () -> Unit = {},
    onRemoveCustomer: () -> Unit = {},

    onSetBankSyncEnabled: (Boolean) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        IvyButton(text = "Connect") {
            onConnect()
        }

        Spacer(Modifier.height(24.dp))

        IvyButton(text = "Sync transactions") {
            onFetchTransactions()
        }

        if (opSyncTransactions is OpResult.Loading) {
            Spacer(Modifier.height(24.dp))

            Text(
                text = "Syncing transactions...",
                style = Typo.body2.style(
                    color = Orange,
                    fontWeight = FontWeight.ExtraBold
                )
            )
        }

        Spacer(Modifier.height(24.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Bank sync enabled:"
            )

            Spacer(Modifier.width(16.dp))

            IvySwitch(
                enabled = bankSyncEnabled
            ) {
                onSetBankSyncEnabled(it)
            }
        }

        Spacer(Modifier.height(24.dp))

        IvyButton(text = "Remove customer") {
            onRemoveCustomer()
        }
    }
}

@Preview
@Composable
private fun Preview() {
    IvyAppPreview {
        UI(
            opSyncTransactions = null,
            bankSyncEnabled = true,

            onConnect = {}
        )
    }
}