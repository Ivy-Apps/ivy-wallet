package com.ivy.core.ui.currency

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.core.ui.currency.data.CurrencyListItem
import com.ivy.data.CurrencyCode
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.l2_components.modal.Modal
import com.ivy.design.l2_components.modal.components.Title
import com.ivy.design.l2_components.modal.rememberIvyModal
import com.ivy.design.util.IvyPreview
import com.ivy.design.util.hiltViewmodelPreviewSafe
import com.ivy.resources.R

@Composable
fun BoxScope.CurrencyModal(
    modal: IvyModal,
    initialSelectedCurrency: CurrencyCode?,
    onCurrencyPick: (CurrencyCode) -> Unit,
) {
    val viewModel: CurrencyModalViewModel? = hiltViewmodelPreviewSafe()
    val state = viewModel?.uiState?.collectAsState()?.value ?: previewState()

    var selectedCurrency by remember(initialSelectedCurrency) {
        mutableStateOf(initialSelectedCurrency)
    }

    Modal(
        modal = modal,
        actions = {}
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 12.dp)
        ) {
            item {
                Title(text = stringResource(R.string.choose_currency))
                SpacerVer(height = 24.dp)
            }
            currencies(
                items = state.items,
                selectedCurrency = selectedCurrency,
                onCurrencySelect = { selectedCurrency = it }
            )
            item {
                // last item spacer
                SpacerVer(height = 24.dp)
            }
        }
    }
}

// region Currencies list
private fun LazyListScope.currencies(
    items: List<CurrencyListItem>,
    selectedCurrency: CurrencyCode?,
    onCurrencySelect: (CurrencyCode) -> Unit
) {

}
// endregion


// region Previews
@Preview
@Composable
private fun Preview() {
    IvyPreview {
        val modal = rememberIvyModal()
        modal.show()
        CurrencyModal(
            modal = modal,
            initialSelectedCurrency = null,
            onCurrencyPick = {}
        )
    }
}

private fun previewState() = CurrencyModalState(
    items = emptyList(),
)
// endregion