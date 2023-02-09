package com.ivy.exchangeRates

import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ivy.core.ui.amount.AmountModal
import com.ivy.data.Value
import com.ivy.design.l0_system.UI
import com.ivy.design.l1_buildingBlocks.ColumnRoot
import com.ivy.design.l1_buildingBlocks.DividerW
import com.ivy.design.l1_buildingBlocks.SpacerHor
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l2_components.input.InputFieldType
import com.ivy.design.l2_components.input.IvyInputField
import com.ivy.design.l2_components.modal.rememberIvyModal
import com.ivy.design.l3_ivyComponents.Feeling
import com.ivy.design.l3_ivyComponents.Visibility
import com.ivy.design.l3_ivyComponents.button.ButtonSize
import com.ivy.design.l3_ivyComponents.button.IvyButton
import com.ivy.design.util.IvyPreview
import com.ivy.exchangeRates.component.RateItem
import com.ivy.exchangeRates.data.RateUi
import com.ivy.exchangeRates.modal.AddRateModal


@Composable
fun BoxWithConstraintsScope.ExchangeRatesScreen() {
    val viewModel: ExchangeRatesViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()

    UI(
        state = state,
        onEvent = viewModel::onEvent
    )
}

@Composable
private fun BoxWithConstraintsScope.UI(
    state: RatesState,
    onEvent: (RatesEvent) -> Unit,
) {

    val amountModal = rememberIvyModal()
    val addRateModal = rememberIvyModal()
    var rateToUpdate by remember {
        mutableStateOf<RateUi?>(null)
    }
    val onRateClick = { rate: RateUi ->
        rateToUpdate = rate
        amountModal.show()
    }

    ColumnRoot {
        SpacerVer(height = 16.dp)

        IvyInputField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            iconLeft=R.drawable.ic_search,
            type = InputFieldType.SingleLine,
            initialValue = "",
            placeholder = "Search Currency",
            onValueChange = {
                onEvent(RatesEvent.Search(it))
            }
        )
        SpacerVer(height = 4.dp)
        LazyColumn {
            ratesSection(text = "Manual")
            items(
                items = state.manual,
                key = { "${it.from}-${it.to}" }
            ) { rate ->
                SpacerVer(height = 4.dp)
                RateItem(
                    rate = rate,
                    onDelete = { onEvent(RatesEvent.RemoveOverride(rate)) },
                    onClick = { onRateClick(rate) }
                )
            }
            ratesSection(text = "Automatic")
            items(
                items = state.automatic,
                key = { "${it.from}-${it.to}" }
            ) { rate ->
                SpacerVer(height = 4.dp)
                RateItem(
                    rate = rate,
                    onDelete = null,
                    onClick = { onRateClick(rate) },
                )
            }
            item(key = "last_item_spacer") {
                SpacerVer(height = 480.dp)
            }
        }
    }


    IvyButton(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(bottom = 28.dp),
        size = ButtonSize.Small,
        feeling = Feeling.Positive,
        visibility = Visibility.High,
        text = "Add rate"
    ) {
        addRateModal.show()
    }

    AddRateModal(
        modal = addRateModal,
        baseCurrency = state.baseCurrency,
        dismiss = {
            addRateModal.hide()
        },
        onAdd = { toCurrency, exchangeRate ->
            onEvent(
                RatesEvent.AddRate(
                    RateUi(
                        from = state.baseCurrency,
                        to = toCurrency,
                        rate = exchangeRate
                    )
                )
            )
        }
    )

    AmountModal(
        modal = amountModal,
        initialAmount = Value(rateToUpdate?.rate ?: 0.0, ""),
        onAmountEnter = { newRate ->
            rateToUpdate?.let {
                onEvent(RatesEvent.UpdateRate(rateToUpdate!!, newRate.amount))
            }
        }
    )
}

private fun LazyListScope.ratesSection(
    text: String
) {
    item{
        SpacerVer(height = 24.dp)
        Row(verticalAlignment = Alignment.CenterVertically) {
            DividerW()
            SpacerHor(width = 16.dp)
            Text(
                text = text,
                style = UI.typo.h2
            )
            SpacerHor(width = 16.dp)
            DividerW()
        }
    }
}


@Preview
@Composable
private fun Preview() {
    IvyPreview {
        UI(
            state = RatesState(
                baseCurrency = "BGN",
                manual = listOf(
                    RateUi("BGN", "USD", 1.85),
                    RateUi("BGN", "EUR", 1.96),
                ),
                automatic = listOf(
                    RateUi("XXX", "YYY", 1.23),
                    RateUi("XXX", "YYY", 1.23),
                    RateUi("XXX", "YYY", 1.23),
                    RateUi("XXX", "YYY", 1.23),
                    RateUi("XXX", "YYY", 1.23),
                    RateUi("XXX", "YYY", 1.23),
                    RateUi("XXX", "YYY", 1.23),
                    RateUi("XXX", "YYY", 1.23),
                    RateUi("XXX", "YYY", 1.23),
                    RateUi("XXX", "YYY", 1.23),
                    RateUi("XXX", "YYY", 1.23),
                    RateUi("XXX", "YYY", 1.23),
                )
            ),
            onEvent = {}
        )
    }
}