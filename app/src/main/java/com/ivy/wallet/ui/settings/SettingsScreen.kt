package com.ivy.wallet.ui.settings

import androidx.annotation.DrawableRes
import androidx.compose.foundation.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import com.ivy.design.api.navigation
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.wallet.BuildConfig
import com.ivy.wallet.Constants
import com.ivy.wallet.Constants.URL_IVY_CONTRIBUTORS
import com.ivy.wallet.R
import com.ivy.wallet.base.*
import com.ivy.wallet.model.AuthProviderType
import com.ivy.wallet.model.IvyCurrency
import com.ivy.wallet.model.entity.User
import com.ivy.wallet.ui.*
import com.ivy.wallet.ui.theme.*
import com.ivy.wallet.ui.theme.components.IvyButton
import com.ivy.wallet.ui.theme.components.IvyIcon
import com.ivy.wallet.ui.theme.components.IvySwitch
import com.ivy.wallet.ui.theme.components.IvyToolbar
import com.ivy.wallet.ui.theme.modal.*
import java.util.*

@ExperimentalFoundationApi
@Composable
fun BoxWithConstraintsScope.SettingsScreen(screen: Settings) {
    val viewModel: SettingsViewModel = viewModel()

    val user by viewModel.user.observeAsState()
    val opSync by viewModel.opSync.observeAsState()
    val currencyCode by viewModel.currencyCode.observeAsState("")
    val lockApp by viewModel.lockApp.observeAsState(false)
    val startDateOfMonth by viewModel.startDateOfMonth.observeAsState(1)

    val nameLocalAccount by viewModel.nameLocalAccount.observeAsState()

    onScreenStart {
        viewModel.start()
    }

    val ivyActivity = LocalContext.current as IvyActivity
    val context = LocalContext.current
    UI(
        user = user,
        currencyCode = currencyCode,
        opSync = opSync,
        lockApp = lockApp,

        nameLocalAccount = nameLocalAccount,
        startDateOfMonth = startDateOfMonth,


        onSetCurrency = viewModel::setCurrency,
        onSetName = viewModel::setName,

        onSync = viewModel::sync,
        onLogout = viewModel::logout,
        onLogin = viewModel::login,
        onExportToCSV = {
            viewModel.exportToCSV(context)
        },
        onSetLockApp = viewModel::setLockApp,
        onSetStartDateOfMonth = viewModel::setStartDateOfMonth,
        onRequestFeature = { title, body ->
            viewModel.requestFeature(
                ivyActivity = ivyActivity,
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

    nameLocalAccount: String?,
    startDateOfMonth: Int = 1,

    onSetCurrency: (String) -> Unit,
    onSetName: (String) -> Unit = {},


    onSync: () -> Unit,
    onLogout: () -> Unit,
    onLogin: () -> Unit,
    onExportToCSV: () -> Unit = {},
    onSetLockApp: (Boolean) -> Unit = {},
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
                text = "Settings",
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

            Spacer(Modifier.height(20.dp))

            Premium()
        }

        item {
            SettingsSectionDivider(text = "Import & Export")

            Spacer(Modifier.height(16.dp))

            val nav = navigation()
            ExportCSV {
                onExportToCSV()
            }

            Spacer(Modifier.height(12.dp))

            SettingsPrimaryButton(
                icon = R.drawable.ic_export_csv,
                text = "Import CSV",
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
            SettingsSectionDivider(text = "App Settings")

            Spacer(Modifier.height(16.dp))

            LockAppSwitch(
                lockApp = lockApp,
                onSetLockApp = onSetLockApp
            )

            Spacer(Modifier.height(12.dp))

            StartDateOfMonth(
                startDateOfMonth = startDateOfMonth
            ) {
                chooseStartDateOfMonthVisible = true
            }
        }

        item {
            SettingsSectionDivider(text = "Other")

            Spacer(Modifier.height(16.dp))

            val ivyActivity = LocalContext.current as IvyActivity
            SettingsPrimaryButton(
                icon = R.drawable.ic_custom_star_m,
                text = "Rate us on Google Play",
                backgroundGradient = GradientIvy
            ) {
                ivyActivity.reviewIvyWallet(dismissReviewCard = false)
            }

            Spacer(Modifier.height(12.dp))

            SettingsPrimaryButton(
                icon = R.drawable.ic_custom_family_m,
                text = "Share Ivy Wallet",
                backgroundGradient = Gradient.solid(Red3)
            ) {
                ivyActivity.shareIvyWallet()
            }
        }

        item {
            SettingsSectionDivider(text = "Product")

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
                text = "Danger zone",
                color = Red
            )

            Spacer(Modifier.height(16.dp))

            SettingsPrimaryButton(
                icon = R.drawable.ic_delete,
                text = "Delete all user data",
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
        title = "Set currency",
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
        title = "Delete all user data?",
        description = "WARNING! This action will delete all data for ${user?.email ?: "your account"} PERMANENTLY and you won't be able to recover it.",
        visible = deleteAllDataModalVisible,
        dismiss = { deleteAllDataModalVisible = false },
        onDelete = {
            deleteAllDataModalVisible = false
            deleteAllDataModalFinalVisible = true
        }
    )

    DeleteModal(
        title = "Confirm permanent deletion for '${user?.email ?: "all of your data"}'",
        description = "FINAL WARNING! After clicking \"Delete\" your data will be gone forever.",
        visible = deleteAllDataModalFinalVisible,
        dismiss = { deleteAllDataModalFinalVisible = false },
        onDelete = {
            onDeleteAllUserData()
        }
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
        Spacer(Modifier.width(16.dp))

        IvyIcon(
            modifier = Modifier
                .size(48.dp)
                .padding(all = 4.dp),
            icon = R.drawable.ic_custom_calendar_m,
            tint = UI.colors.pureInverse
        )

        Spacer(Modifier.width(8.dp))

        Text(
            modifier = Modifier.padding(vertical = 20.dp),
            text = "Start date of month",
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
private fun HelpCenter() {
    val nav = navigation()
    SettingsDefaultButton(
        icon = R.drawable.ic_custom_education_m,
        text = "Help Center",
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
        text = "Roadmap",
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
        text = "Request a feature",
    ) {
        onClick()
    }
}

@Composable
private fun ContactSupport() {
    val ivyActivity = LocalContext.current as IvyActivity
    SettingsDefaultButton(
        icon = R.drawable.ic_support,
        text = "Contact support",
    ) {
        ivyActivity.contactSupport()
    }
}

@Composable
private fun ProjectContributors() {
    val nav = navigation()
    SettingsDefaultButton(
        icon = R.drawable.ic_custom_people_m,
        text = "Project Contributors",
    ) {
        nav.navigateTo(
            IvyWebView(url = URL_IVY_CONTRIBUTORS)
        )
    }
}

@Composable
private fun LockAppSwitch(
    lockApp: Boolean,
    onSetLockApp: (Boolean) -> Unit
) {
    SettingsButtonRow(
        onClick = {
            onSetLockApp(!lockApp)
        }
    ) {
        Spacer(Modifier.width(16.dp))

        IvyIcon(
            icon = R.drawable.ic_custom_fingerprint_m,
            tint = UI.colors.pureInverse
        )

        Spacer(Modifier.width(8.dp))

        Text(
            modifier = Modifier.padding(vertical = 20.dp),
            text = "Lock app",
            style = UI.typo.b2.style(
                color = UI.colors.pureInverse,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(Modifier.weight(1f))

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
            .clip(Shapes.rounded24)
            .background(UI.colors.medium, Shapes.rounded24)
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
                text = "ACCOUNT",
                style = UI.typo.c.style(
                    fontWeight = FontWeight.Black,
                    color = UI.colors.gray
                )
            )

            Spacer(Modifier.weight(1f))

            if (user != null) {
                AccountCardButton(
                    icon = R.drawable.ic_logout,
                    text = "Logout"
                ) {
                    onLogout()
                }
            } else {
                AccountCardButton(
                    icon = R.drawable.ic_login,
                    text = "Login"
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
            Image(
                modifier = Modifier
                    .clip(CircleShape)
                    .size(32.dp),
                painter = rememberCoilPainter(request = user.profilePicture),
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
        Spacer(Modifier.width(24.dp))

        IvyIcon(
            icon = R.drawable.ic_email
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
                Spacer(Modifier.width(24.dp))

                IvyIcon(
                    icon = R.drawable.ic_data_synced,
                    tint = Orange
                )

                Spacer(Modifier.width(12.dp))

                Text(
                    text = "Syncing...",
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
                    Spacer(Modifier.width(24.dp))

                    IvyIcon(
                        icon = R.drawable.ic_data_synced,
                        tint = Green
                    )

                    Spacer(Modifier.width(12.dp))

                    Text(
                        text = "Data synced to cloud",
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
                    text = "Tap to sync",
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
                text = "Sync failed. Tap to sync",
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

        IvyIcon(icon = R.drawable.ic_local_account)

        Spacer(Modifier.width(12.dp))

        Text(
            modifier = Modifier.testTag("local_account_name"),
            text = if (name != null && name.isNotBlank()) name else "Anonymous",
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
        icon = R.drawable.ic_export_csv,
        text = "Export to CSV",
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
                .clip(Shapes.roundedFull)
                .border(2.dp, UI.colors.medium, Shapes.roundedFull)
                .clickable {
                    uriHandler.openUri(Constants.URL_TC)
                }
                .padding(vertical = 14.dp),
            text = "Terms & Conditions",
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
                .clip(Shapes.roundedFull)
                .border(2.dp, UI.colors.medium, Shapes.roundedFull)
                .clickable {
                    uriHandler.openUri(Constants.URL_PRIVACY_POLICY)
                }
                .padding(vertical = 14.dp),
            text = "Privacy Policy",
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
    onClick: () -> Unit
) {
    SettingsButtonRow(
        hasShadow = hasShadow,
        backgroundGradient = backgroundGradient,
        onClick = onClick
    ) {
        Spacer(Modifier.width(16.dp))

        IvyIcon(
            icon = icon,
            tint = textColor
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
            .clip(Shapes.rounded16)
            .background(backgroundGradient.asHorizontalBrush(), Shapes.rounded16)
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
            .clip(Shapes.roundedFull)
            .background(UI.colors.pure, Shapes.roundedFull)
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(12.dp))

        IvyIcon(
            icon = icon
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
            .clip(Shapes.rounded16)
            .border(2.dp, UI.colors.medium, Shapes.rounded16)
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(20.dp))

        IvyIcon(icon = R.drawable.ic_currency)

        Spacer(Modifier.width(8.dp))

        Text(
            modifier = Modifier.padding(vertical = 20.dp),
            text = "Set currency",
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

        IvyIcon(icon = R.drawable.ic_arrow_right)

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
    onClick: () -> Unit
) {
    SettingsPrimaryButton(
        icon = icon,
        text = text,
        backgroundGradient = Gradient.solid(UI.colors.medium),
        textColor = UI.colors.pureInverse
    ) {
        onClick()
    }
}

@ExperimentalFoundationApi
@Preview
@Composable
private fun Preview_synced() {
    IvyAppPreview {
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
    IvyAppPreview {
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
    IvyAppPreview {
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
    IvyAppPreview {
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