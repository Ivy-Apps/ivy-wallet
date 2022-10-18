package com.ivy.debug

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ivy.core.ui.amount.AmountModal
import com.ivy.core.ui.color.ColorPickerButton
import com.ivy.core.ui.color.picker.ColorPickerModal
import com.ivy.core.ui.currency.CurrencyPickerModal
import com.ivy.core.ui.icon.picker.IconPickerModal
import com.ivy.data.ItemIconId
import com.ivy.data.Value
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.color.Purple
import com.ivy.design.l1_buildingBlocks.ColumnRoot
import com.ivy.design.l1_buildingBlocks.H2
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l1_buildingBlocks.SpacerWeight
import com.ivy.design.l2_components.modal.rememberIvyModal
import com.ivy.design.l3_ivyComponents.Feeling
import com.ivy.design.l3_ivyComponents.Visibility
import com.ivy.design.l3_ivyComponents.button.ButtonSize
import com.ivy.design.l3_ivyComponents.button.IvyButton

@Composable
fun BoxScope.TestScreen() {
    val viewModel: TestViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()

    val iconPickerModal = rememberIvyModal()
    val colorPickerModal = rememberIvyModal()
    val amountModal = rememberIvyModal()
    val currencyPickerModal = rememberIvyModal()

    var selectedIconId by remember { mutableStateOf<ItemIconId?>(null) }
    var selectedColor by remember { mutableStateOf(Purple) }

    ColumnRoot(
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SpacerWeight(weight = 1f)
        IvyButton(
            size = ButtonSize.Big,
            visibility = Visibility.Focused,
            feeling = Feeling.Positive,
            text = "Pick an icon",
            icon = null
        ) {
            iconPickerModal.show()
        }
        SpacerVer(height = 48.dp)
        selectedIconId?.let { H2(text = it) }
        SpacerVer(height = 48.dp)
        ColorPickerButton(
            colorPickerModal = colorPickerModal,
            selectedColor = selectedColor
        )
        SpacerVer(height = 48.dp)
        IvyButton(
            size = ButtonSize.Big,
            visibility = Visibility.Focused,
            feeling = Feeling.Positive,
            text = "Amount modal",
            icon = null
        ) {
            amountModal.show()
        }
        SpacerVer(height = 48.dp)
        IvyButton(
            size = ButtonSize.Big,
            visibility = Visibility.Medium,
            feeling = Feeling.Positive,
            text = "Base currency: ${state.baseCurrency}",
            icon = null
        ) {
            currencyPickerModal.show()
        }
        SpacerWeight(weight = 1f)
    }

    IconPickerModal(
        modal = iconPickerModal,
        initialIcon = null,
        color = UI.colors.primary,
        onIconPick = { selectedIconId = it }
    )
    ColorPickerModal(
        modal = colorPickerModal,
        initialColor = selectedColor,
        onColorPicked = { selectedColor = it }
    )
    AmountModal(
        modal = amountModal,
        initialAmount = Value(0.0, "USD"),
        onAmountEnter = {}
    )
    CurrencyPickerModal(
        modal = currencyPickerModal,
        initialCurrency = state.baseCurrency,
        onCurrencyPick = {
            viewModel.onEvent(TestEvent.BaseCurrencyChange(it))
        }
    )
}