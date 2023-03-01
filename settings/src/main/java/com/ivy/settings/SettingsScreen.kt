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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ivy.core.ui.currency.CurrencyPickerModal
import com.ivy.core.ui.rootScreen
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.color.Green
import com.ivy.design.l0_system.color.Orange
import com.ivy.design.l1_buildingBlocks.B2
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
import com.ivy.settings.data.BackupImportState
import com.ivy.settings.data.Language
import java.time.Instant

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
    val languagePickerModal = rememberIvyModal()

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
                text = stringResource(R.string.settings),
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
                feeling = Feeling.Positive, text = String.format(
                    stringResource(R.string.start_day_of_month),
                    state.startDayOfMonth
                ),
                icon = null
            ) {
                startDayOfMonthModal.show()
            }
            SpacerVer(height = 12.dp)
            SwitchRow(
                enabled = state.hideBalance,
                text = stringResource(R.string.hide_balance),
                onValueChange = {
                    onEvent(SettingsEvent.HideBalance(hideBalance = it))
                })
            SpacerVer(height = 12.dp)
            SwitchRow(
                enabled = state.appLocked,
                text = stringResource(R.string.lock_app),
                onValueChange = {
                    onEvent(SettingsEvent.AppLocked(appLocked = it))
                })
            SpacerVer(height = 12.dp)

            val rootScreen = rootScreen()
            IvyButton(
                modifier = Modifier.padding(horizontal = 16.dp),
                size = ButtonSize.Big, visibility = Visibility.High,
                feeling = when (state.importOldData) {
                    is BackupImportState.Error -> Feeling.Negative
                    BackupImportState.Idle -> Feeling.Positive
                    BackupImportState.Importing -> Feeling.Custom(Orange)
                    is BackupImportState.Success -> Feeling.Custom(Green)
                },
                text = when (state.importOldData) {
                    BackupImportState.Idle -> stringResource(R.string.import_old_data)
                    BackupImportState.Importing -> stringResource(R.string.import_old_data_in_progress)
                    is BackupImportState.Error -> String.format(
                        stringResource(R.string.import_data_error),
                        state.importOldData.message
                    )
                    is BackupImportState.Success -> String.format(
                        stringResource(R.string.import_data_successfully),
                        state.importOldData.message
                    )
                },
                icon = null
            ) {
                onEvent(SettingsEvent.ImportOldData)
            }
            SpacerVer(height = 12.dp)
            IvyButton(
                modifier = Modifier.padding(horizontal = 16.dp),
                size = ButtonSize.Big, visibility = Visibility.Medium,
                feeling = Feeling.Positive, text = stringResource(R.string.languages),
                icon = null
            ) {
                languagePickerModal.show()
            }
            SpacerVer(height = 12.dp)
            IvyButton(
                modifier = Modifier.padding(horizontal = 16.dp),
                size = ButtonSize.Big, visibility = Visibility.Medium,
                feeling = Feeling.Positive, text = stringResource(R.string.exchange_rates),
                icon = null
            ) {
                onEvent(SettingsEvent.ExchangeRates)
            }
        }
        item {
            SpacerVer(height = 24.dp)
            B2(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = stringResource(R.string.data_risk_warning),
                color = UI.colors.red
            )
            SpacerVer(height = 12.dp)
            IvyButton(
                modifier = Modifier.padding(horizontal = 16.dp),
                size = ButtonSize.Big,
                visibility = Visibility.Medium,
                feeling = Feeling.Negative,
                text = if (state.driveMounted)
                    stringResource(R.string.drive_mounted) else stringResource(R.string.mount_drive),
                icon = null
            ) {
                onEvent(SettingsEvent.MountDrive)
            }
        }
        item {
            SpacerVer(height = 24.dp)
            IvyButton(
                modifier = Modifier.padding(horizontal = 16.dp),
                size = ButtonSize.Big,
                visibility = Visibility.Medium,
                feeling = Feeling.Positive,
                text = stringResource(R.string.add_ivy_frame),
                icon = null
            ) {
                onEvent(SettingsEvent.AddFrame)
            }
        }
        item {
            SpacerVer(height = 16.dp)
            val rootScreen = rootScreen()
            IvyButton(
                modifier = Modifier.padding(horizontal = 16.dp),
                size = ButtonSize.Big,
                visibility = Visibility.Medium,
                feeling = Feeling.Positive,
                text = stringResource(R.string.backup_data),
                icon = null
            ) {
                rootScreen.createFile(
                    fileName = "Ivy-Wallet-Backup-${Instant.now().epochSecond}.zip"
                ) {
                    onEvent(SettingsEvent.BackupData(backupLocation = it))
                }
            }
        }
        item {
            SpacerVer(height = 24.dp)
            IvyButton(
                modifier = Modifier.padding(horizontal = 16.dp),
                size = ButtonSize.Big,
                visibility = Visibility.Medium,
                feeling = Feeling.Negative,
                text = stringResource(R.string.nuke_accounts_cache),
                icon = null
            ) {
                onEvent(SettingsEvent.NukeAccCache)
            }
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
    LanguagePickerModal(
        modal = languagePickerModal,
        supportedLanguages = state.supportedLanguages,
        currentLanguageCode = state.currentLanguage,
        onLanguageChange = { languageCode ->
            onEvent(SettingsEvent.LanguageChange(languageCode))
        }
    )

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
        Title(text = stringResource(R.string.set_day_of_month))
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

//TODO:have to highlight the selected language
@Composable
private fun BoxScope.LanguagePickerModal(
    modal: IvyModal,
    onLanguageChange: (String) -> Unit,
    supportedLanguages: List<Language>,
    currentLanguageCode: String
) {
    Modal(
        modal = modal,
        actions = {
        }
    ) {
        Title(text = stringResource(R.string.choose_language))
        SpacerVer(height = 24.dp)
        LazyColumn {
            items(supportedLanguages) { language ->
                SpacerVer(height = 12.dp)
                IvyButton(
                    size = ButtonSize.Big,
                    visibility = if (currentLanguageCode == language.languageCode) Visibility.Focused else Visibility.Medium,
                    feeling = Feeling.Positive,
                    text = language.name,
                    icon = null
                ) {
                    onLanguageChange(language.languageCode)
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
                appLocked = false,
                driveMounted = false,
                importOldData = BackupImportState.Idle,
                supportedLanguages = emptyList(),
                currentLanguage = ""
            ),
            onEvent = {}
        )
    }
}