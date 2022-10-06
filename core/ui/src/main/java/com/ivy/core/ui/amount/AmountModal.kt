package com.ivy.core.ui.amount

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.core.ui.amount.components.Keyboard
import com.ivy.data.CurrencyCode
import com.ivy.data.Value
import com.ivy.design.l1_buildingBlocks.SpacerHor
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.l2_components.modal.Modal
import com.ivy.design.l2_components.modal.components.Positive
import com.ivy.design.l2_components.modal.components.Secondary
import com.ivy.design.l2_components.modal.rememberIvyModal
import com.ivy.design.l3_ivyComponents.button.ButtonFeeling
import com.ivy.design.util.IvyPreview
import com.ivy.design.util.hiltViewmodelPreviewSafe
import com.ivy.resources.R

@Composable
fun BoxScope.AmountModal(
    modal: IvyModal,
    initialAmount: Value,
    contentAbove: (@Composable () -> Unit)? = {
        SpacerVer(height = 24.dp)
    },
    onAmountEnter: (Value) -> Unit,
) {
    val viewModel: AmountModalViewModel? = hiltViewmodelPreviewSafe()
    val state = viewModel?.uiState?.collectAsState()?.value ?: previewState()

    LaunchedEffect(initialAmount) {
        viewModel?.onEvent(AmountModalEvent.Initial(initialAmount))
    }

    var calculatorVisible by remember { mutableStateOf(false) }

    Modal(
        modal = modal,
        actions = {
            Secondary(
                text = null,
                icon = R.drawable.ic_vue_edu_calculator,
                feeling = if (calculatorVisible) ButtonFeeling.Negative else ButtonFeeling.Positive
            ) {
                calculatorVisible = !calculatorVisible
            }
            SpacerHor(width = 8.dp)
            Positive(
                text = stringResource(R.string.enter),
                icon = R.drawable.ic_round_check_24
            ) {
            }
        }
    ) {
        contentAbove?.invoke()
        Amount(
            amountText = state.amountText,
            currency = state.currency
        )
        Keyboard(
            calculatorVisible = calculatorVisible,
            onAmountChange = {},
            onCurrencyChange = {},
        )
        SpacerVer(height = 24.dp)
    }
}

@Composable
private fun Amount(
    amountText: String,
    currency: CurrencyCode,
) {

}



// region Previews
@Preview
@Composable
private fun Preview() {
    IvyPreview {
        val modal = rememberIvyModal()
        modal.show()
        AmountModal(
            modal = modal,
            initialAmount = Value(0.0, "USD"),
            onAmountEnter = {}
        )
    }
}

private fun previewState() = AmountModalState(
    amountText = "",
    currency = "USD",
    amount = null,
    amountBaseCurrency = null
)
// endregion