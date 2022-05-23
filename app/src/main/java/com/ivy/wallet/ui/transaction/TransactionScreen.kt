package com.ivy.wallet.ui.transaction

import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.runtime.Composable
import com.ivy.frp.view.navigation.Screen
import com.ivy.wallet.domain.data.TransactionType
import com.ivy.wallet.domain.data.core.Transaction
import com.ivy.wallet.ui.architecture.FRP

sealed class TransactionScreen : Screen {
    data class NewTransaction(
        val type: TransactionType
    ) : TransactionScreen()

    data class EditTransaction(
        val transaction: Transaction
    ) : TransactionScreen()
}

@Composable
fun BoxWithConstraintsScope.TransactionScreen(screen: TransactionScreen) {
    FRP<TrnState, TrnEvent, TransactionViewModel>(
        initialEvent = when (screen) {
            is TransactionScreen.EditTransaction -> TrnEvent.LoadTransaction(screen.transaction)
            is TransactionScreen.NewTransaction -> TrnEvent.NewTransaction(screen.type)
        }
    ) { state, onEvent ->
        UI(state = state, onEvent = onEvent)
    }
}

@Composable
private fun UI(
    state: TrnState,

    onEvent: (TrnEvent) -> Unit
) {

}