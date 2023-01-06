package com.ivy.settings

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ivy.core.ui.currency.CurrencyPickerModal
import com.ivy.design.l1_buildingBlocks.H1
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l2_components.SwitchRow
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.l2_components.modal.Modal
import com.ivy.design.l2_components.modal.components.Title
import com.ivy.design.l2_components.modal.rememberIvyModal
import com.ivy.design.l3_ivyComponents.BackButton
import com.ivy.design.l3_ivyComponents.Feeling
import com.ivy.design.l3_ivyComponents.Visibility
import com.ivy.design.l3_ivyComponents.button.ButtonSize
import com.ivy.design.l3_ivyComponents.button.IvyButton
import com.ivy.design.util.IvyPreview

/*
- Dark Mode
- Hidden transactions (leads to new screen which you'll create later)
- Create Transaction steps (leads to new screen)
- Export backup
- Export CSV
- Import backup
- Exchange Rates (new screen)
- T&C + Privacy Policy
- Rate us
- Share Ivy Wallet
- Donate
- Ivy Telegram
- Delete all data
- GitHub repo
 */

// H1, B1, Caption {H1Second, B1Second, ...}
// Focused, Medium
// IvyButton
// UI.color, UI.typo(fonts)
// SwitchRow (on/off)

@Composable
fun BoxScope.SettingsScreen() {
    val viewModel: SettingsViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()

    UI(state = state, onEvent = viewModel::onEvent)
}

@Composable
private fun BoxScope.UI(
    state: SettingsState,
    onEvent: (SettingsEvent) -> Unit
) {
    val currencyModal = rememberIvyModal()
    val startDayOfMonthModal = rememberIvyModal()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        item(key = "content") {
            BackButton(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                onEvent(SettingsEvent.Back)
            }

            H1(
                text = "Settings",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            SpacerVer(height = 12.dp)
            IvyButton(
                modifier = Modifier.padding(horizontal = 16.dp),
                size = ButtonSize.Big, visibility = Visibility.Medium,
                feeling = Feeling.Positive, text = state.baseCurrency, icon = null
            ) {
                currencyModal.show()
            }
            SpacerVer(height = 12.dp)
            IvyButton(
                modifier = Modifier.padding(horizontal = 16.dp),
                size = ButtonSize.Big, visibility = Visibility.Focused,
                feeling = Feeling.Positive, text = "Start day of month ${state.startDayOfMonth}",
                icon = null
            ) {
                startDayOfMonthModal.show()
            }
            SpacerVer(height = 12.dp)
            SwitchRow(enabled = state.hideBalance, text = "Hide balance", onValueChange = {
                onEvent(SettingsEvent.HideBalance(hideBalance = it))
            })
            SpacerVer(height = 12.dp)
            SwitchRow(enabled = state.appLocked, text = "Lock app", onValueChange = {
                onEvent(SettingsEvent.AppLocked(appLocked = it))
            })
        }
    }

    CurrencyPickerModal(
        modal = currencyModal,
        initialCurrency = state.baseCurrency,
        onCurrencyPick = { newCurrency ->
            onEvent(SettingsEvent.BaseCurrencyChange(newCurrency = newCurrency))
        }
    )

    StartDayOfMonthModal(
        modal = startDayOfMonthModal,
        onStartDayOfMonthChange = { startDayOfMonth ->
            onEvent(SettingsEvent.StartDayOfMonth(startDayOfMonth = startDayOfMonth))
        })
}

@Composable
private fun BoxScope.StartDayOfMonthModal(
    modal: IvyModal,
    onStartDayOfMonthChange: (Int) -> Unit,
) {
    Modal(
        modal = modal,
        actions = {
        }
    ) {
        Title(text = "Set start day of month")
        SpacerVer(height = 24.dp)
        LazyColumn {
            items(items = (1..31).toList()) { number ->
                SpacerVer(height = 12.dp)
                IvyButton(
                    size = ButtonSize.Big,
                    visibility = Visibility.Medium,
                    feeling = Feeling.Positive,
                    text = number.toString(),
                    icon = null
                ) {
                    onStartDayOfMonthChange(number)
                    modal.hide()
                }
            }
        }
        SpacerVer(height = 24.dp)
    }
}


@Preview
@Composable
private fun Preview() {
    IvyPreview {
        UI(
            state = SettingsState(
                baseCurrency = "BGN",
                startDayOfMonth = 1,
                hideBalance = false,
                appLocked = false
            ),
            onEvent = {}
        )
    }
}