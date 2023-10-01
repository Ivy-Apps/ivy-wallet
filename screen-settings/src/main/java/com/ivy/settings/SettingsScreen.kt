package com.ivy.settings

import androidx.annotation.DrawableRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ivy.base.legacy.Theme
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.design.l1_buildingBlocks.IconScale
import com.ivy.design.l1_buildingBlocks.IvyIconScaled
import com.ivy.legacy.Constants
import com.ivy.legacy.IvyWalletPreview
import com.ivy.legacy.rootScreen
import com.ivy.legacy.utils.OpResult
import com.ivy.legacy.utils.drawColoredShadow
import com.ivy.legacy.utils.thenIf
import com.ivy.navigation.AttributionsScreen
import com.ivy.navigation.ContributorsScreen
import com.ivy.navigation.ExchangeRatesScreen
import com.ivy.navigation.ImportScreen
import com.ivy.navigation.Navigation
import com.ivy.navigation.ReleasesScreen
import com.ivy.navigation.navigation
import com.ivy.navigation.screenScopedViewModel
import com.ivy.resources.R
import com.ivy.wallet.domain.data.IvyCurrency
import com.ivy.wallet.ui.theme.Blue
import com.ivy.wallet.ui.theme.Gradient
import com.ivy.wallet.ui.theme.GradientGreen
import com.ivy.wallet.ui.theme.GradientIvy
import com.ivy.wallet.ui.theme.Gray
import com.ivy.wallet.ui.theme.Green
import com.ivy.wallet.ui.theme.MediumBlack
import com.ivy.wallet.ui.theme.Orange
import com.ivy.wallet.ui.theme.Red
import com.ivy.wallet.ui.theme.Red3
import com.ivy.wallet.ui.theme.White
import com.ivy.wallet.ui.theme.components.IvySwitch
import com.ivy.wallet.ui.theme.components.IvyToolbar
import com.ivy.wallet.ui.theme.findContrastTextColor
import com.ivy.wallet.ui.theme.modal.ChooseStartDateOfMonthModal
import com.ivy.wallet.ui.theme.modal.CurrencyModal
import com.ivy.wallet.ui.theme.modal.DeleteModal
import com.ivy.wallet.ui.theme.modal.NameModal
import com.ivy.wallet.ui.theme.modal.ProgressModal

@ExperimentalFoundationApi
@Composable
fun BoxWithConstraintsScope.SettingsScreen() {
    val viewModel: SettingsViewModel = screenScopedViewModel()
    val uiState = viewModel.uiState()
    val rootScreen = rootScreen()

    UI(
        currencyCode = uiState.currencyCode,
        theme = uiState.currentTheme,
        onSwitchTheme = {
            viewModel.onEvent(SettingsEvent.SwitchTheme)
        },
        lockApp = uiState.lockApp,
        showNotifications = uiState.showNotifications,
        hideCurrentBalance = uiState.hideCurrentBalance,
        progressState = uiState.progressState,
        treatTransfersAsIncomeExpense = uiState.treatTransfersAsIncomeExpense,
        nameLocalAccount = uiState.name,
        startDateOfMonth = uiState.startDateOfMonth.toInt(),
        onSetCurrency = {
            viewModel.onEvent(SettingsEvent.SetCurrency(it))
        },
        onSetName = {
            viewModel.onEvent(SettingsEvent.SetName(it))
        },
        onBackupData = {
            viewModel.onEvent(SettingsEvent.BackupData(rootScreen))
        },
        onExportToCSV = {
            viewModel.onEvent(SettingsEvent.ExportToCsv(rootScreen))
        },
        onSetLockApp = {
            viewModel.onEvent(SettingsEvent.SetLockApp(it))
        },
        onSetShowNotifications = {
            viewModel.onEvent(SettingsEvent.SetShowNotifications(it))
        },
        onSetHideCurrentBalance = {
            viewModel.onEvent(SettingsEvent.SetHideCurrentBalance(it))
        },
        onSetStartDateOfMonth = {
            viewModel.onEvent(SettingsEvent.SetStartDateOfMonth(it))
        },
        onSetTreatTransfersAsIncExp = {
            viewModel.onEvent(SettingsEvent.SetTransfersAsIncomeExpense(it))
        },
        onDeleteAllUserData = {
            viewModel.onEvent(SettingsEvent.DeleteAllUserData)
        },
        onDeleteCloudUserData = {
            viewModel.onEvent(SettingsEvent.DeleteCloudUserData)
        },
    )
}

@ExperimentalFoundationApi
@Composable
private fun BoxWithConstraintsScope.UI(
    currencyCode: String,
    theme: Theme,
    onSwitchTheme: () -> Unit,
    lockApp: Boolean,
    nameLocalAccount: String?,
    onSetCurrency: (String) -> Unit,
    startDateOfMonth: Int = 1,
    showNotifications: Boolean = true,
    hideCurrentBalance: Boolean = false,
    progressState: Boolean = false,
    treatTransfersAsIncomeExpense: Boolean = false,
    onSetName: (String) -> Unit = {},
    onBackupData: () -> Unit = {},
    onExportToCSV: () -> Unit = {},
    onSetLockApp: (Boolean) -> Unit = {},
    onSetShowNotifications: (Boolean) -> Unit = {},
    onSetTreatTransfersAsIncExp: (Boolean) -> Unit = {},
    onSetHideCurrentBalance: (Boolean) -> Unit = {},
    onSetStartDateOfMonth: (Int) -> Unit = {},
    onDeleteAllUserData: () -> Unit = {},
    onDeleteCloudUserData: () -> Unit = {},

    ) {
    var currencyModalVisible by remember { mutableStateOf(false) }
    var nameModalVisible by remember { mutableStateOf(false) }
    var chooseStartDateOfMonthVisible by remember { mutableStateOf(false) }
    var deleteCloudDataModalVisible by remember { mutableStateOf(false) }
    var deleteAllDataModalVisible by remember { mutableStateOf(false) }
    var deleteAllDataModalFinalVisible by remember { mutableStateOf(false) }
    val nav = navigation()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("settings_lazy_column")
    ) {
        stickyHeader {
            IvyToolbar(
                onBack = { nav.onBackPressed() },
            ) {
                Spacer(Modifier.weight(1f))

                val rootScreen = rootScreen()
                Text(
                    modifier = Modifier.clickable {
                        nav.navigateTo(ReleasesScreen)
                    },
                    text = "${rootScreen.buildVersionName} (${rootScreen.buildVersionCode})",
                    style = UI.typo.nC.style(
                        color = UI.colors.gray,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(Modifier.width(32.dp))
            }
            // onboarding toolbar include paddingBottom 16.dp
        }

        item {
            Spacer(Modifier.height(8.dp))

            Text(
                modifier = Modifier.padding(start = 32.dp),
                text = stringResource(R.string.settings),
                style = UI.typo.h2.style(
                    fontWeight = FontWeight.Black
                )
            )

            Spacer(Modifier.height(24.dp))

            CurrencyButton(currency = currencyCode) {
                currencyModalVisible = true
            }

            Spacer(Modifier.height(12.dp))

            AccountCard(
                nameLocalAccount = nameLocalAccount,
            ) {
                nameModalVisible = true
            }

//            Spacer(Modifier.height(20.dp))
//            Premium()
        }

        item {
            SettingsSectionDivider(text = stringResource(R.string.import_export))

            Spacer(Modifier.height(16.dp))

            val nav = navigation()
            ExportCSV {
                onExportToCSV()
            }

            Spacer(Modifier.height(12.dp))

            SettingsDefaultButton(
                icon = R.drawable.ic_vue_security_shield,
                text = stringResource(R.string.backup_data),
                iconPadding = 6.dp
            ) {
                onBackupData()
            }

            Spacer(Modifier.height(12.dp))

            SettingsPrimaryButton(
                icon = R.drawable.ic_export_csv,
                text = stringResource(R.string.import_data),
                backgroundGradient = GradientGreen
            ) {
                nav.navigateTo(
                    ImportScreen(
                        launchedFromOnboarding = false
                    )
                )
            }
        }

        item {
            SettingsSectionDivider(text = stringResource(R.string.app_settings))

            Spacer(Modifier.height(16.dp))

            AppThemeButton(
                icon = when (theme) {
                    Theme.LIGHT -> R.drawable.home_more_menu_light_mode
                    Theme.DARK -> R.drawable.home_more_menu_dark_mode
                    Theme.AUTO -> R.drawable.home_more_menu_auto_mode
                },
                label = when (theme) {
                    Theme.LIGHT -> stringResource(R.string.light_mode)
                    Theme.DARK -> stringResource(R.string.dark_mode)
                    Theme.AUTO -> stringResource(R.string.auto_mode)
                }
            ) {
                onSwitchTheme()
            }

            Spacer(Modifier.height(12.dp))

            val nav = navigation()
//            SettingsDefaultButton(
//                icon = R.drawable.ic_custom_atom_m,
//                text = "Features"
//            ) {
//                nav.navigateTo(FeaturesScreen)
//            }
//
//            Spacer(Modifier.height(12.dp))

            SettingsDefaultButton(
                icon = R.drawable.ic_currency,
                text = "Exchange rates"
            ) {
                nav.navigateTo(ExchangeRatesScreen)
            }

            Spacer(Modifier.height(12.dp))

            AppSwitch(
                lockApp = lockApp,
                onSetLockApp = onSetLockApp,
                text = stringResource(R.string.lock_app),
                icon = R.drawable.ic_custom_fingerprint_m
            )

            Spacer(Modifier.height(12.dp))

            AppSwitch(
                lockApp = showNotifications,
                onSetLockApp = onSetShowNotifications,
                text = stringResource(R.string.show_notifications),
                icon = R.drawable.ic_notification_m
            )

            Spacer(Modifier.height(12.dp))

            AppSwitch(
                lockApp = hideCurrentBalance,
                onSetLockApp = onSetHideCurrentBalance,
                text = stringResource(R.string.hide_balance),
                description = stringResource(R.string.hide_balance_description),
                icon = R.drawable.ic_hide_m
            )

            Spacer(Modifier.height(12.dp))

            AppSwitch(
                lockApp = treatTransfersAsIncomeExpense,
                onSetLockApp = onSetTreatTransfersAsIncExp,
                text = stringResource(R.string.transfers_as_income_expense),
                description = stringResource(R.string.transfers_as_income_expense_description),
                icon = R.drawable.ic_custom_transfer_m
            )

            Spacer(Modifier.height(12.dp))

            StartDateOfMonth(
                startDateOfMonth = startDateOfMonth
            ) {
                chooseStartDateOfMonthVisible = true
            }
        }

//        item {
//            SettingsSectionDivider(text = stringResource(R.string.experimental))
//
//            Spacer(Modifier.height(16.dp))
//
//            val nav = navigation()
//            SettingsDefaultButton(
//                icon = R.drawable.ic_custom_atom_m,
//                text = stringResource(R.string.experimental_settings)
//            ) {
//                nav.navigateTo(ExperimentalScreen)
//            }
//        }

        item {
            SettingsSectionDivider(text = stringResource(R.string.other))

            Spacer(Modifier.height(16.dp))

            val rootScreen = rootScreen()
            SettingsPrimaryButton(
                icon = R.drawable.ic_custom_star_m,
                text = stringResource(R.string.rate_us_on_google_play),
                backgroundGradient = GradientIvy
            ) {
                rootScreen.reviewIvyWallet(dismissReviewCard = false)
            }

            Spacer(Modifier.height(12.dp))

            SettingsPrimaryButton(
                icon = R.drawable.ic_custom_family_m,
                text = stringResource(R.string.share_ivy_wallet),
                backgroundGradient = Gradient.solid(Red3)
            ) {
                rootScreen.shareIvyWallet()
            }

            Spacer(Modifier.height(12.dp))

            SettingsPrimaryButton(
                icon = R.drawable.github_logo,
                iconPadding = 8.dp,
                text = stringResource(R.string.ivy_wallet_is_opensource),
                backgroundGradient = Gradient.solid(MediumBlack)
            ) {
                rootScreen.openUrlInBrowser(url = Constants.URL_IVY_WALLET_REPO)
            }
        }

        item {
            SettingsSectionDivider(text = stringResource(R.string.product))

            Spacer(Modifier.height(12.dp))

            IvyTelegram()

            Spacer(Modifier.height(16.dp))

            HelpCenter()

            Spacer(Modifier.height(12.dp))

            Releases(nav = nav)

            Spacer(Modifier.height(12.dp))

            Roadmap()

            Spacer(Modifier.height(12.dp))

            val rootActivity = rootScreen()
            RequestFeature {
                rootActivity.openUrlInBrowser(Constants.URL_IVY_TELEGRAM_INVITE)
            }

            Spacer(Modifier.height(12.dp))

            ContactSupport()

            Spacer(Modifier.height(12.dp))

            Contributors(nav = nav)

            Spacer(Modifier.height(12.dp))

            Attributions()

            Spacer(Modifier.height(12.dp))

            TCAndPrivacyPolicy()
        }

        item {
            SettingsSectionDivider(
                text = stringResource(R.string.danger_zone),
                color = Red
            )

            Spacer(Modifier.height(16.dp))

            SettingsPrimaryButton(
                icon = R.drawable.ic_delete,
                text = stringResource(R.string.delete_all_user_data),
                backgroundGradient = Gradient.solid(Red)
            ) {
                deleteAllDataModalVisible = true
            }
        }

        item {
            Spacer(modifier = Modifier.height(120.dp)) // last item spacer
        }
    }

    CurrencyModal(
        title = stringResource(R.string.set_currency),
        initialCurrency = IvyCurrency.fromCode(currencyCode),
        visible = currencyModalVisible,
        dismiss = { currencyModalVisible = false }
    ) {
        onSetCurrency(it)
    }

    NameModal(
        visible = nameModalVisible,
        name = nameLocalAccount ?: "",
        dismiss = { nameModalVisible = false }
    ) {
        onSetName(it)
    }

    ChooseStartDateOfMonthModal(
        visible = chooseStartDateOfMonthVisible,
        selectedStartDateOfMonth = startDateOfMonth,
        dismiss = { chooseStartDateOfMonthVisible = false }
    ) {
        onSetStartDateOfMonth(it)
    }

    DeleteModal(
        title = stringResource(R.string.delete_all_user_data_question),
        description = stringResource(
            R.string.delete_all_user_data_warning,
            stringResource(R.string.your_account)
        ),
        visible = deleteAllDataModalVisible,
        dismiss = { deleteAllDataModalVisible = false },
        onDelete = {
            deleteAllDataModalVisible = false
            deleteAllDataModalFinalVisible = true
        }
    )

    DeleteModal(
        title = stringResource(
            R.string.confirm_all_userd_data_deletion,
            stringResource(R.string.all_of_your_data)
        ),
        description = stringResource(R.string.final_deletion_warning),
        visible = deleteAllDataModalFinalVisible,
        dismiss = { deleteAllDataModalFinalVisible = false },
        onDelete = {
            onDeleteAllUserData()
        }
    )

    DeleteModal(
        title = stringResource(R.string.delete_all_cloud_data_question),
        description = stringResource(
            R.string.delete_all_user_cloud_data_warning,
            stringResource(R.string.your_account)
        ),
        visible = deleteCloudDataModalVisible,
        dismiss = { deleteCloudDataModalVisible = false },
        onDelete = {
            onDeleteCloudUserData()
            deleteCloudDataModalVisible = false
        }
    )

    ProgressModal(
        title = stringResource(R.string.exporting_data),
        description = stringResource(R.string.exporting_data_description),
        visible = progressState
    )
}

@Composable
private fun StartDateOfMonth(
    startDateOfMonth: Int,
    onClick: () -> Unit
) {
    SettingsButtonRow(
        onClick = onClick
    ) {
        Spacer(Modifier.width(12.dp))

        IvyIconScaled(
            icon = R.drawable.ic_custom_calendar_m,
            tint = UI.colors.pureInverse,
            iconScale = IconScale.M,
            padding = 0.dp
        )

        Spacer(Modifier.width(8.dp))

        Text(
            modifier = Modifier.padding(vertical = 20.dp),
            text = stringResource(R.string.start_date_of_month),
            style = UI.typo.b2.style(
                color = UI.colors.pureInverse,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(Modifier.weight(1f))

        Text(
            text = startDateOfMonth.toString(),
            style = UI.typo.nB2.style(
                fontWeight = FontWeight.ExtraBold,
                color = UI.colors.pureInverse
            )
        )

        Spacer(Modifier.width(32.dp))
    }
}

@Composable
private fun IvyTelegram() {
    val rootActivity = rootScreen()
    SettingsPrimaryButton(
        icon = R.drawable.ic_telegram_24dp,
        text = stringResource(R.string.ivy_telegram),
        backgroundGradient = Gradient.solid(Blue),
        iconPadding = 8.dp
    ) {
        rootActivity.openUrlInBrowser(Constants.URL_IVY_TELEGRAM_INVITE)
    }
}

@Composable
private fun HelpCenter() {
    val uriHandler = LocalUriHandler.current
    SettingsDefaultButton(
        icon = R.drawable.ic_custom_education_m,
        text = stringResource(R.string.help_center),
    ) {
        uriHandler.openUri(Constants.URL_HELP_CENTER)
    }
}

@Composable
private fun Roadmap() {
    val uriHandler = LocalUriHandler.current
    SettingsDefaultButton(
        icon = R.drawable.ic_custom_rocket_m,
        text = stringResource(R.string.roadmap),
    ) {
        uriHandler.openUri(Constants.URL_ROADMAP)
    }
}

@Composable
private fun RequestFeature(
    onClick: () -> Unit
) {
    SettingsDefaultButton(
        icon = R.drawable.ic_custom_programming_m,
        text = stringResource(R.string.request_a_feature),
    ) {
        onClick()
    }
}

@Composable
private fun ContactSupport() {
    val rootActivity = rootScreen()
    SettingsDefaultButton(
        icon = R.drawable.ic_support,
        text = stringResource(R.string.contact_support),
    ) {
        rootActivity.openUrlInBrowser(Constants.URL_IVY_TELEGRAM_INVITE)
    }
}

@Composable
private fun Releases(nav: Navigation) {
    SettingsDefaultButton(
        icon = R.drawable.ic_vue_money_tag,
        text = "Releases",
        iconPadding = 10.dp
    ) {
        nav.navigateTo(ReleasesScreen)
    }
}

@Composable
private fun Contributors(nav: Navigation) {
    SettingsDefaultButton(
        icon = R.drawable.ic_vue_people_people,
        text = stringResource(R.string.project_contributors),
        iconPadding = 6.dp
    ) {
        nav.navigateTo(ContributorsScreen)
    }
}

@Composable
private fun Attributions() {
    val nav = navigation()

    SettingsDefaultButton(
        icon = R.drawable.ic_vue_location_global,
        text = "Attributions",
        iconPadding = 6.dp
    ) {
        nav.navigateTo(AttributionsScreen)
    }
}

@Composable
private fun AppThemeButton(
    @DrawableRes icon: Int,
    label: String,
    onClick: () -> Unit
) {
    SettingsPrimaryButton(
        icon = icon,
        text = label,
        backgroundGradient = Gradient.solid(UI.colors.medium),
        textColor = UI.colors.pureInverse,
        iconPadding = 6.dp,
        description = "Tap to switch theme",
        onClick = onClick
    )
}

@Composable
private fun AppSwitch(
    lockApp: Boolean,
    onSetLockApp: (Boolean) -> Unit,
    text: String,
    icon: Int,
    description: String = "",
) {
    SettingsButtonRow(
        onClick = {
            onSetLockApp(!lockApp)
        }
    ) {
        Spacer(Modifier.width(12.dp))

        IvyIconScaled(
            icon = icon,
            tint = UI.colors.pureInverse,
            iconScale = IconScale.M,
            padding = 0.dp
        )

        Spacer(Modifier.width(8.dp))

        Column(
            Modifier
                .weight(1f)
                .padding(top = 20.dp, bottom = 20.dp, end = 8.dp)
        ) {
            Text(
                text = text,
                style = UI.typo.b2.style(
                    color = UI.colors.pureInverse,
                    fontWeight = FontWeight.Bold
                )
            )
            if (description.isNotEmpty()) {
                Text(
                    modifier = Modifier.padding(end = 8.dp),
                    text = description,
                    style = UI.typo.nB2.style(
                        color = Gray,
                        fontWeight = FontWeight.Normal
                    ).copy(fontSize = 14.sp)
                )
            }
        }

        // Spacer(Modifier.weight(1f))

        IvySwitch(enabled = lockApp) {
            onSetLockApp(it)
        }

        Spacer(Modifier.width(16.dp))
    }
}

@Composable
private fun AccountCard(
    nameLocalAccount: String?,
    onCardClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(UI.shapes.r2)
            .background(UI.colors.medium, UI.shapes.r2)
            .clickable {
                onCardClick()
            }
    ) {
        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("settings_profile_card"),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.width(24.dp))

            Text(
                text = stringResource(R.string.account_uppercase),
                style = UI.typo.c.style(
                    fontWeight = FontWeight.Black,
                    color = UI.colors.gray
                )
            )
        }

        Spacer(Modifier.height(4.dp))

        AccountCardLocalAccount(
            name = nameLocalAccount,
        )

        Spacer(Modifier.height(24.dp))
    }
}


@Composable
private fun AccountCardLocalAccount(
    name: String?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(20.dp))
        IvyIconScaled(
            icon = R.drawable.ic_local_account,
            iconScale = IconScale.M
        )

        Spacer(Modifier.width(12.dp))

        Text(
            modifier = Modifier
                .weight(1f)
                .testTag("local_account_name"),
            text = if (!name.isNullOrBlank()) name else stringResource(R.string.anonymous),
            style = UI.typo.b2.style(
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(Modifier.width(12.dp))
    }
}

@Composable
private fun ExportCSV(
    onExportToCSV: () -> Unit
) {
    SettingsDefaultButton(
        icon = R.drawable.ic_vue_pc_printer,
        text = stringResource(R.string.export_to_csv),
        iconPadding = 6.dp,
        description = "⚠\uFE0F Do not use for backup purposes"
    ) {
        onExportToCSV()
    }
}

@Composable
private fun TCAndPrivacyPolicy() {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(16.dp))

        val uriHandler = LocalUriHandler.current

        Text(
            modifier = Modifier
                .weight(1f)
                .clip(UI.shapes.rFull)
                .border(2.dp, UI.colors.medium, UI.shapes.rFull)
                .clickable {
                    uriHandler.openUri(Constants.URL_TC)
                }
                .padding(vertical = 14.dp),
            text = stringResource(R.string.terms_conditions),
            style = UI.typo.c.style(
                fontWeight = FontWeight.ExtraBold,
                color = UI.colors.pureInverse,
                textAlign = TextAlign.Center
            )
        )

        Spacer(Modifier.width(12.dp))

        Text(
            modifier = Modifier
                .weight(1f)
                .clip(UI.shapes.rFull)
                .border(2.dp, UI.colors.medium, UI.shapes.rFull)
                .clickable {
                    uriHandler.openUri(Constants.URL_PRIVACY_POLICY)
                }
                .padding(vertical = 14.dp),
            text = stringResource(R.string.privacy_policy),
            style = UI.typo.c.style(
                fontWeight = FontWeight.ExtraBold,
                color = UI.colors.pureInverse,
                textAlign = TextAlign.Center
            )
        )

        Spacer(Modifier.width(16.dp))
    }
}

@Composable
private fun SettingsPrimaryButton(
    @DrawableRes icon: Int,
    text: String,
    hasShadow: Boolean = false,
    backgroundGradient: Gradient = Gradient.solid(UI.colors.medium),
    textColor: Color = White,
    iconPadding: Dp = 0.dp,
    description: String? = null,
    onClick: () -> Unit
) {
    SettingsButtonRow(
        hasShadow = hasShadow,
        backgroundGradient = backgroundGradient,
        onClick = onClick
    ) {
        Spacer(Modifier.width(12.dp))

        IvyIconScaled(
            icon = icon,
            tint = textColor,
            iconScale = IconScale.M,
            padding = iconPadding
        )

        Spacer(Modifier.width(8.dp))

        Column(
            Modifier
                .weight(1f)
                .padding(top = 20.dp, bottom = 20.dp, end = 8.dp)
        ) {
            Text(
                text = text,
                style = UI.typo.b2.style(
                    color = textColor,
                    fontWeight = FontWeight.Bold,
                )
            )
            if (!description.isNullOrEmpty()) {
                Text(
                    modifier = Modifier.padding(end = 8.dp),
                    text = description,
                    style = UI.typo.nB2.style(
                        color = Gray,
                        fontWeight = FontWeight.Normal
                    ).copy(fontSize = 14.sp)
                )
            }
        }
    }
}

@Composable
private fun SettingsButtonRow(
    onClick: (() -> Unit)?,
    hasShadow: Boolean = false,
    backgroundGradient: Gradient = Gradient.solid(UI.colors.medium),
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .thenIf(hasShadow) {
                drawColoredShadow(color = backgroundGradient.startColor)
            }
            .fillMaxWidth()
            .clip(UI.shapes.r4)
            .background(backgroundGradient.asHorizontalBrush(), UI.shapes.r4)
            .thenIf(onClick != null) {
                clickable {
                    onClick?.invoke()
                }
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        content()
    }
}

@Composable
private fun AccountCardButton(
    @DrawableRes icon: Int,
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(UI.shapes.rFull)
            .background(UI.colors.pure, UI.shapes.rFull)
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(12.dp))

        IvyIconScaled(
            icon = icon,
            iconScale = IconScale.M
        )

        Spacer(Modifier.width(4.dp))

        Text(
            modifier = Modifier
                .padding(vertical = 10.dp),
            text = text,
            style = UI.typo.b2.style(
                fontWeight = FontWeight.Bold,
                color = UI.colors.pureInverse
            )
        )

        Spacer(Modifier.width(24.dp))
    }
}

@Composable
private fun CurrencyButton(
    currency: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(UI.shapes.r4)
            .border(2.dp, UI.colors.medium, UI.shapes.r4)
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(12.dp))

        IvyIconScaled(
            icon = R.drawable.ic_currency,
            iconScale = IconScale.M,
            padding = 0.dp
        )

        Spacer(Modifier.width(8.dp))

        Text(
            modifier = Modifier.padding(vertical = 20.dp),
            text = stringResource(R.string.set_currency),
            style = UI.typo.b2.style(
                color = UI.colors.pureInverse,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(Modifier.weight(1f))

        Text(
            text = currency,
            style = UI.typo.b1.style(
                color = UI.colors.pureInverse,
                fontWeight = FontWeight.ExtraBold
            )
        )

        Spacer(Modifier.height(4.dp))

        IvyIconScaled(
            icon = R.drawable.ic_arrow_right,
            iconScale = IconScale.M
        )

        Spacer(Modifier.width(24.dp))
    }
}

@Composable
private fun SettingsSectionDivider(
    text: String,
    color: Color = Gray
) {
    Column {
        Spacer(Modifier.height(32.dp))

        Text(
            modifier = Modifier.padding(start = 32.dp),
            text = text,
            style = UI.typo.b2.style(
                color = color,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
fun FetchMissingTransactionsButton(
    opFetchTrns: OpResult<Unit>?,
    onFetchMissingTransactions: () -> Unit
) {
    val background = Gradient.solid(
        when (opFetchTrns) {
            is OpResult.Failure -> Red
            OpResult.Loading -> Orange
            is OpResult.Success -> Green
            null -> UI.colors.medium
        }
    )
    SettingsPrimaryButton(
        icon = R.drawable.ic_sync,
        text = when (opFetchTrns) {
            is OpResult.Failure -> "Error: ${opFetchTrns.error()}"
            OpResult.Loading -> "Full sync... wait!"
            is OpResult.Success -> "Success. Check transactions."
            else -> "Fetch missing transactions"
        },
        backgroundGradient = background,
        textColor = findContrastTextColor(background.startColor),
        iconPadding = 0.dp
    ) {
        onFetchMissingTransactions()
    }
}

@Composable
private fun SettingsDefaultButton(
    @DrawableRes icon: Int,
    text: String,
    iconPadding: Dp = 0.dp,
    description: String? = null,
    onClick: () -> Unit,
) {
    SettingsPrimaryButton(
        icon = icon,
        text = text,
        backgroundGradient = Gradient.solid(UI.colors.medium),
        textColor = UI.colors.pureInverse,
        iconPadding = iconPadding,
        description = description
    ) {
        onClick()
    }
}

@ExperimentalFoundationApi
@Preview
@Composable
private fun Preview() {
    IvyWalletPreview {
        UI(
            nameLocalAccount = null,
            theme = Theme.AUTO,
            onSwitchTheme = {},
            lockApp = false,
            currencyCode = "BGN",
            onSetCurrency = {},
        )
    }
}