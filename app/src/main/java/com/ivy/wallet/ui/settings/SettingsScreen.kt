package com.ivy.wallet.ui.settings

import androidx.annotation.DrawableRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.design.l1_buildingBlocks.IconScale
import com.ivy.design.l1_buildingBlocks.IvyIconScaled
import com.ivy.frp.view.navigation.navigation
import com.ivy.wallet.BuildConfig
import com.ivy.wallet.Constants
import com.ivy.wallet.Constants.URL_IVY_CONTRIBUTORS
import com.ivy.wallet.R
import com.ivy.wallet.domain.data.AuthProviderType
import com.ivy.wallet.domain.data.IvyCurrency
import com.ivy.wallet.domain.data.core.User
import com.ivy.wallet.ui.*
import com.ivy.wallet.ui.theme.*
import com.ivy.wallet.ui.theme.components.IvyButton
import com.ivy.wallet.ui.theme.components.IvySwitch
import com.ivy.wallet.ui.theme.components.IvyToolbar
import com.ivy.wallet.ui.theme.modal.*
import com.ivy.wallet.utils.*
import java.util.*

@ExperimentalFoundationApi
@Composable
fun BoxWithConstraintsScope.SettingsScreen(screen: Settings) {
    val viewModel: SettingsViewModel = viewModel()

    val user by viewModel.user.observeAsState()
    val opSync by viewModel.opSync.observeAsState()
    val currencyCode by viewModel.currencyCode.observeAsState("")
    val lockApp by viewModel.lockApp.observeAsState(false)
    val showNotifications by viewModel.showNotifications.collectAsState()
    val hideCurrentBalance by viewModel.hideCurrentBalance.collectAsState()
    val treatTransfersAsIncomeExpense by viewModel.treatTransfersAsIncomeExpense.collectAsState()
    val startDateOfMonth by viewModel.startDateOfMonth.observeAsState(1)
    val progressState by viewModel.progressState.collectAsState()

    val nameLocalAccount by viewModel.nameLocalAccount.observeAsState()

    onScreenStart {
        viewModel.start()
    }

    val ivyActivity = LocalContext.current as RootActivity
    val context = LocalContext.current
    UI(
        user = user,
        currencyCode = currencyCode,
        opSync = opSync,
        lockApp = lockApp,
        showNotifications = showNotifications,
        hideCurrentBalance = hideCurrentBalance,
        progressState = progressState,
        treatTransfersAsIncomeExpense = treatTransfersAsIncomeExpense,

        nameLocalAccount = nameLocalAccount,
        startDateOfMonth = startDateOfMonth,


        onSetCurrency = viewModel::setCurrency,
        onSetName = viewModel::setName,

        onSync = viewModel::sync,
        onLogout = viewModel::logout,
        onLogin = viewModel::login,
        onBackupData = {
            viewModel.exportToZip(context)
        },
        onExportToCSV = {
            viewModel.exportToCSV(context)
        },
        onSetLockApp = viewModel::setLockApp,
        onSetShowNotifications = viewModel::setShowNotifications,
        onSetHideCurrentBalance = viewModel::setHideCurrentBalance,
        onSetStartDateOfMonth = viewModel::setStartDateOfMonth,
        onSetTreatTransfersAsIncExp = viewModel::setTransfersAsIncomeExpense,
        onRequestFeature = { title, body ->
            viewModel.requestFeature(
                rootActivity = ivyActivity,
                title = title,
                body = body
            )
        },
        onDeleteAllUserData = viewModel::deleteAllUserData
    )
}

@ExperimentalFoundationApi
@Composable
private fun BoxWithConstraintsScope.UI(
    user: User?,
    currencyCode: String,
    opSync: OpResult<Boolean>?,

    lockApp: Boolean,
    showNotifications: Boolean = true,
    hideCurrentBalance: Boolean = false,
    progressState: Boolean = false,
    treatTransfersAsIncomeExpense: Boolean = false,

    nameLocalAccount: String?,
    startDateOfMonth: Int = 1,

    onSetCurrency: (String) -> Unit,
    onSetName: (String) -> Unit = {},


    onSync: () -> Unit,
    onLogout: () -> Unit,
    onLogin: () -> Unit,
    onBackupData: () -> Unit = {},
    onExportToCSV: () -> Unit = {},
    onSetLockApp: (Boolean) -> Unit = {},
    onSetShowNotifications: (Boolean) -> Unit = {},
    onSetTreatTransfersAsIncExp: (Boolean) -> Unit = {},
    onSetHideCurrentBalance: (Boolean) -> Unit = {},
    onSetStartDateOfMonth: (Int) -> Unit = {},
    onRequestFeature: (String, String) -> Unit = { _, _ -> },
    onDeleteAllUserData: () -> Unit = {}
) {
    var currencyModalVisible by remember { mutableStateOf(false) }
    var nameModalVisible by remember { mutableStateOf(false) }
    var chooseStartDateOfMonthVisible by remember { mutableStateOf(false) }
    var requestFeatureModalVisible by remember { mutableStateOf(false) }
    var deleteAllDataModalVisible by remember { mutableStateOf(false) }
    var deleteAllDataModalFinalVisible by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        stickyHeader {
            val nav = navigation()
            IvyToolbar(
                onBack = { nav.onBackPressed() },
            ) {
                Spacer(Modifier.weight(1f))

                Text(
                    modifier = Modifier.clickableNoIndication {
                        nav.navigateTo(Test)
                    },
                    text = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
                    style = UI.typo.nC.style(
                        color = UI.colors.gray,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(Modifier.width(32.dp))
            }
            //onboarding toolbar include paddingBottom 16.dp
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
                user = user,
                opSync = opSync,
                nameLocalAccount = nameLocalAccount,

                onSync = onSync,
                onLogout = onLogout,
                onLogin = onLogin,
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
                    Import(
                        launchedFromOnboarding = false
                    )
                )
            }
        }

        item {
            SettingsSectionDivider(text = stringResource(R.string.app_settings))

            Spacer(Modifier.height(16.dp))

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

        item {
            SettingsSectionDivider(text = stringResource(R.string.other))

            Spacer(Modifier.height(16.dp))

            val ivyActivity = LocalContext.current as RootActivity
            SettingsPrimaryButton(
                icon = R.drawable.ic_custom_star_m,
                text = stringResource(R.string.rate_us_on_google_play),
                backgroundGradient = GradientIvy
            ) {
                ivyActivity.reviewIvyWallet(dismissReviewCard = false)
            }

            Spacer(Modifier.height(12.dp))

            SettingsPrimaryButton(
                icon = R.drawable.ic_custom_family_m,
                text = stringResource(R.string.share_ivy_wallet),
                backgroundGradient = Gradient.solid(Red3)
            ) {
                ivyActivity.shareIvyWallet()
            }
        }

        item {
            SettingsSectionDivider(text = stringResource(R.string.product))

            Spacer(Modifier.height(12.dp))

            IvyTelegram()

            Spacer(Modifier.height(16.dp))

            HelpCenter()

            Spacer(Modifier.height(12.dp))

            Roadmap()

            Spacer(Modifier.height(12.dp))

            RequestFeature {
                requestFeatureModalVisible = true
            }

            Spacer(Modifier.height(12.dp))

            ContactSupport()

            Spacer(Modifier.height(12.dp))

            ProjectContributors()

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
            Spacer(modifier = Modifier.height(120.dp)) //last item spacer
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

    RequestFeatureModal(
        visible = requestFeatureModalVisible,
        dismiss = {
            requestFeatureModalVisible = false
        },
        onSubmit = onRequestFeature
    )

    DeleteModal(
        title = stringResource(R.string.delete_all_user_data_question),
        description = stringResource(
            R.string.delete_all_user_data_warning, user?.email ?: stringResource(
                R.string.your_account
            )
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
            R.string.confirm_all_userd_data_deletion, user?.email ?: stringResource(
                R.string.all_of_your_data
            )
        ),
        description = stringResource(R.string.final_deletion_warning),
        visible = deleteAllDataModalFinalVisible,
        dismiss = { deleteAllDataModalFinalVisible = false },
        onDelete = {
            onDeleteAllUserData()
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
    val rootActivity = rootActivity()
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
    val nav = navigation()
    SettingsDefaultButton(
        icon = R.drawable.ic_custom_education_m,
        text = stringResource(R.string.help_center),
    ) {
        nav.navigateTo(
            IvyWebView(url = Constants.URL_HELP_CENTER)
        )
    }
}

@Composable
private fun Roadmap() {
    val nav = navigation()
    SettingsDefaultButton(
        icon = R.drawable.ic_custom_rocket_m,
        text = stringResource(R.string.roadmap),
    ) {
        nav.navigateTo(
            IvyWebView(url = Constants.URL_ROADMAP)
        )
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
    val rootActivity = rootActivity()
    SettingsDefaultButton(
        icon = R.drawable.ic_support,
        text = stringResource(R.string.contact_support),
    ) {
        rootActivity.openUrlInBrowser(Constants.URL_IVY_TELEGRAM_INVITE)
    }
}

@Composable
private fun ProjectContributors() {
    val nav = navigation()
    SettingsDefaultButton(
        icon = R.drawable.ic_vue_people_people,
        text = stringResource(R.string.project_contributors),
        iconPadding = 6.dp
    ) {
        nav.navigateTo(
            IvyWebView(url = URL_IVY_CONTRIBUTORS)
        )
    }
}

@Composable
private fun AppSwitch(
    lockApp: Boolean,
    onSetLockApp: (Boolean) -> Unit,
    text: String,
    description: String = "",
    icon: Int,
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

        //Spacer(Modifier.weight(1f))

        IvySwitch(enabled = lockApp) {
            onSetLockApp(it)
        }

        Spacer(Modifier.width(16.dp))
    }
}

@Composable
private fun AccountCard(
    user: User?,
    opSync: OpResult<Boolean>?,
    nameLocalAccount: String?,

    onSync: () -> Unit,
    onLogout: () -> Unit,
    onLogin: () -> Unit,
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

            Spacer(Modifier.weight(1f))

            if (user != null) {
                AccountCardButton(
                    icon = R.drawable.ic_logout,
                    text = stringResource(R.string.logout)
                ) {
                    onLogout()
                }
            } else {
                AccountCardButton(
                    icon = R.drawable.ic_login,
                    text = stringResource(R.string.login)
                ) {
                    onLogin()
                }
            }

            Spacer(Modifier.width(16.dp))
        }

        if (user != null) {
            AccountCardUser(
                localName = nameLocalAccount,
                user = user,
                opSync = opSync,
                onSync = onSync
            )
        } else {
            AccountCardLocalAccount(
                name = nameLocalAccount,
            )
        }

    }
}

@Composable
private fun AccountCardUser(
    localName: String?,
    user: User,
    opSync: OpResult<Boolean>?,

    onSync: () -> Unit,
) {
    Spacer(Modifier.height(4.dp))

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(24.dp))

        if (user.profilePicture != null) {
            AsyncImage(
                modifier = Modifier
                    .clip(CircleShape)
                    .size(32.dp),
                model = user.profilePicture,
                contentScale = ContentScale.FillBounds,
                contentDescription = "profile picture"
            )

            Spacer(Modifier.width(12.dp))
        }

        Text(
            text = localName ?: user.names(),
            style = UI.typo.b2.style(
                fontWeight = FontWeight.ExtraBold,
                color = UI.colors.pureInverse
            )
        )

        Spacer(Modifier.width(24.dp))
    }

    Spacer(Modifier.height(12.dp))

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(20.dp))

        IvyIconScaled(
            icon = R.drawable.ic_email,
            iconScale = IconScale.S,
            padding = 0.dp
        )

        Spacer(Modifier.width(12.dp))

        Text(
            text = user.email,
            style = UI.typo.b2.style(
                fontWeight = FontWeight.ExtraBold,
                color = UI.colors.pureInverse
            )
        )

        Spacer(Modifier.width(24.dp))
    }

    Spacer(Modifier.height(12.dp))

    when (opSync) {
        is OpResult.Loading -> {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(Modifier.width(20.dp))

                IvyIconScaled(
                    icon = R.drawable.ic_data_synced,
                    tint = Orange,
                    iconScale = IconScale.S,
                    padding = 0.dp
                )

                Spacer(Modifier.width(12.dp))

                Text(
                    text = stringResource(R.string.syncing),
                    style = UI.typo.b2.style(
                        fontWeight = FontWeight.ExtraBold,
                        color = Orange
                    )
                )

                Spacer(Modifier.width(24.dp))
            }
        }
        is OpResult.Success -> {
            if (opSync.data) {
                //synced
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(Modifier.width(20.dp))

                    IvyIconScaled(
                        icon = R.drawable.ic_data_synced,
                        tint = Green,
                        iconScale = IconScale.S,
                        padding = 0.dp
                    )

                    Spacer(Modifier.width(12.dp))

                    Text(
                        text = stringResource(R.string.data_synced_to_cloud),
                        style = UI.typo.b2.style(
                            fontWeight = FontWeight.ExtraBold,
                            color = Green
                        )
                    )

                    Spacer(Modifier.width(24.dp))
                }
            } else {
                //not synced
                IvyButton(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    iconStart = R.drawable.ic_sync,
                    text = stringResource(R.string.tap_to_sync),
                    backgroundGradient = GradientRed
                ) {
                    onSync()
                }
            }
        }
        is OpResult.Failure -> {
            IvyButton(
                modifier = Modifier.padding(horizontal = 24.dp),
                iconStart = R.drawable.ic_sync,
                text = stringResource(R.string.sync_failed),
                backgroundGradient = GradientRed
            ) {
                onSync()
            }
        }
    }

    Spacer(Modifier.height(24.dp))
}

@Composable
private fun AccountCardLocalAccount(
    name: String?,
) {
    Spacer(Modifier.height(4.dp))

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(20.dp))

        IvyIconScaled(
            icon = R.drawable.ic_local_account,
            iconScale = IconScale.M
        )

        Spacer(Modifier.width(12.dp))

        Text(
            modifier = Modifier.testTag("local_account_name"),
            text = if (name != null && name.isNotBlank()) name else stringResource(R.string.anonymous),
            style = UI.typo.b2.style(
                fontWeight = FontWeight.Bold
            )
        )
    }

    Spacer(Modifier.height(24.dp))
}

@Composable
private fun Premium() {
    val nav = navigation()
    SettingsPrimaryButton(
        icon = R.drawable.ic_custom_crown_s,
        text = if (ivyWalletCtx().isPremium) "Ivy Premium (owned)" else "Buy premium",
        hasShadow = true,
        backgroundGradient = if (ivyWalletCtx().isPremium) GradientIvy else GradientOrange
    ) {
        nav.navigateTo(
            Paywall(
                paywallReason = null
            )
        )
    }
}

@Composable
private fun ExportCSV(
    onExportToCSV: () -> Unit
) {
    SettingsDefaultButton(
        icon = R.drawable.ic_vue_pc_printer,
        text = stringResource(R.string.export_to_csv),
        iconPadding = 6.dp
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

        Text(
            modifier = Modifier.padding(vertical = 20.dp),
            text = text,
            style = UI.typo.b2.style(
                color = textColor,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
private fun SettingsButtonRow(
    hasShadow: Boolean = false,
    backgroundGradient: Gradient = Gradient.solid(UI.colors.medium),
    onClick: (() -> Unit)?,
    Content: @Composable RowScope.() -> Unit
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
        Content()
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

@Composable
private fun SettingsDefaultButton(
    @DrawableRes icon: Int,
    text: String,
    iconPadding: Dp = 0.dp,
    onClick: () -> Unit
) {
    SettingsPrimaryButton(
        icon = icon,
        text = text,
        backgroundGradient = Gradient.solid(UI.colors.medium),
        textColor = UI.colors.pureInverse,
        iconPadding = iconPadding
    ) {
        onClick()
    }
}

@ExperimentalFoundationApi
@Preview
@Composable
private fun Preview_synced() {
    IvyWalletPreview {
        UI(
            user = User(
                email = "iliyan.germanov971@gmail.com",
                authProviderType = AuthProviderType.GOOGLE,
                firstName = "Iliyan",
                lastName = "Germanov",
                color = 11,
                id = UUID.randomUUID(),
                profilePicture = null
            ),
            nameLocalAccount = null,
            opSync = OpResult.success(true),
            lockApp = false,
            currencyCode = "BGN",
            onSetCurrency = {},
            onLogout = {},
            onLogin = {},
            onSync = {}
        )
    }
}

@ExperimentalFoundationApi
@Preview
@Composable
private fun Preview_notSynced() {
    IvyWalletPreview {
        UI(
            user = User(
                email = "iliyan.germanov971@gmail.com",
                authProviderType = AuthProviderType.GOOGLE,
                firstName = "Iliyan",
                lastName = "Germanov",
                color = 11,
                id = UUID.randomUUID(),
                profilePicture = null
            ),
            lockApp = false,
            nameLocalAccount = null,
            opSync = OpResult.success(false),
            currencyCode = "BGN",
            onSetCurrency = {},
            onLogout = {},
            onLogin = {},
            onSync = {}
        )
    }
}

@ExperimentalFoundationApi
@Preview
@Composable
private fun Preview_loading() {
    IvyWalletPreview {
        UI(
            user = User(
                email = "iliyan.germanov971@gmail.com",
                authProviderType = AuthProviderType.GOOGLE,
                firstName = "Iliyan",
                lastName = null,
                color = 11,
                id = UUID.randomUUID(),
                profilePicture = null
            ),
            lockApp = false,
            nameLocalAccount = null,
            opSync = OpResult.loading(),
            currencyCode = "BGN",
            onSetCurrency = {},
            onLogout = {},
            onLogin = {},
            onSync = {}
        )
    }
}

@ExperimentalFoundationApi
@Preview
@Composable
private fun Preview_localAccount() {
    IvyWalletPreview {
        UI(
            user = null,
            nameLocalAccount = "Iliyan",
            opSync = null,
            currencyCode = "BGN",
            lockApp = false,
            onSetCurrency = {},
            onLogout = {},
            onLogin = {},
            onSync = {}
        )
    }
}