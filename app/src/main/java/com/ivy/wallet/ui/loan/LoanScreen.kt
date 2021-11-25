package com.ivy.wallet.ui.loan

import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.insets.systemBarsPadding
import com.ivy.wallet.base.onScreenStart
import com.ivy.wallet.ui.IvyAppPreview
import com.ivy.wallet.ui.LocalIvyContext
import com.ivy.wallet.ui.Screen
import com.ivy.wallet.ui.theme.Typo
import com.ivy.wallet.ui.theme.style

@Composable
fun BoxWithConstraintsScope.LoanScreen(screen: Screen.Loan) {
    val viewModel: LoanViewModel = viewModel()

    onScreenStart {
        viewModel.start()
    }

    UI()
}

@Composable
private fun BoxWithConstraintsScope.UI() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .verticalScroll(rememberScrollState()),
    ) {
        //TODO: Implement UI

        Spacer(Modifier.weight(1f))

        Text(
            text = "Loans",
            style = Typo.h1.style()
        )

        Spacer(Modifier.weight(1f))
    }

    val ivyContext = LocalIvyContext.current
    LoanBottomBar(
        onAdd = {
//            budgetModalData = BudgetModalData(
//                budget = null,
//                baseCurrency = baseCurrency,
//                categories = categories,
//                accounts = accounts
//            )
        },
        onClose = {
            ivyContext.back()
        },
    )
}

@Preview
@Composable
private fun Preview() {
    IvyAppPreview {
        UI()
    }
}