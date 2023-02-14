package com.ivy.wallet.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.appwidget.AppWidgetManager
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ivy.exchangeRates.ExchangeRatesScreen
import com.google.android.play.core.review.ReviewManagerFactory
import com.ivy.accounts.AccountsScreen
import com.ivy.api.screen.backup.ImportBackupScreen
import com.ivy.categories.CategoriesScreen
import com.ivy.common.Constants
import com.ivy.common.Constants.SUPPORT_EMAIL
import com.ivy.common.time.provider.TimeProvider
import com.ivy.common.time.timeNow
import com.ivy.common.time.toEpochMilli
import com.ivy.core.ui.RootScreen
import com.ivy.core.ui.Toaster
import com.ivy.data.file.FileType
import com.ivy.debug.TestScreen
import com.ivy.design.api.IvyUI
import com.ivy.design.api.setAppDesign
import com.ivy.design.api.systems.ivyWalletDesign
import com.ivy.drive.google_drive.api.GoogleDriveConnection
import com.ivy.file.CreateFileLauncher
import com.ivy.file.FilePickerLauncher
import com.ivy.home.HomeScreen
import com.ivy.menu.HomeMoreMenuScreen
import com.ivy.navigation.NavigationRoot
import com.ivy.navigation.Navigator
import com.ivy.navigation.graph.DebugScreens
import com.ivy.navigation.graph.OnboardingScreens
import com.ivy.navigation.graph.TransactionScreens
import com.ivy.onboarding.screen.debug.OnboardingDebug
import com.ivy.photo.frame.AddFrameScreen
import com.ivy.resources.R
import com.ivy.settings.SettingsScreen
import com.ivy.transaction.create.transfer.NewTransferScreen
import com.ivy.transaction.create.trn.NewTransactionScreen
import com.ivy.transaction.edit.transfer.EditTransferScreen
import com.ivy.transaction.edit.trn.EditTransactionScreen
import com.ivy.wallet.BuildConfig
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalTime
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class RootActivity : AppCompatActivity(), RootScreen {
    companion object {
        const val SHORTCUT_ACTION = "ivy.wallet.intent.action.add_transaction"
    }

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var timeProvider: TimeProvider

    @Inject
    lateinit var filePickerLauncher: FilePickerLauncher

    @Inject
    lateinit var createFileLauncher: CreateFileLauncher

    @Inject
    lateinit var googleDriveConnection: GoogleDriveConnection

    @Inject
    lateinit var toaster: Toaster


    private val viewModel: RootViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // region setup ActivityForResult launchers
        filePickerLauncher.wire(this)
        createFileLauncher.wire(this)
        googleDriveConnection.wire(this)
        // endregion

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                toaster.messages.collectLatest {
                    Toast.makeText(this@RootActivity, it, Toast.LENGTH_LONG)
                        .show()
                }
            }
        }

        // Check if the intent was triggered by a shortcut action, and notify the view model of the shortcut click event
        if (intent.action == SHORTCUT_ACTION) {
            viewModel.onEvent(RootEvent.ShortcutClick(intent))
        }

        // Make the app drawing area fullscreen (draw behind status and nav bars)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val viewModel: RootViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsState()
            val isSystemInDarkTheme = isSystemInDarkTheme()

            LaunchedEffect(state.theme, isSystemInDarkTheme) {
                setAppDesign(
                    ivyWalletDesign(
                        theme = state.theme,
                        isSystemInDarkTheme = isSystemInDarkTheme
                    )
                )
            }

            IvyUI {
                NavigationRoot(state)
            }
        }

        viewModel.onEvent(RootEvent.AppOpen)
    }

    @Composable
    private fun BoxWithConstraintsScope.NavigationRoot(state: RootState) {
        NavigationRoot(
            navigator = navigator,
            onboardingScreens = OnboardingScreens(
                debug = { OnboardingDebug() },
                loginOrOffline = {},
                importBackup = {},
                setCurrency = {},
                addAccounts = {},
                addCategories = {}
            ),
            home = { HomeScreen() },
            accounts = { AccountsScreen() },
            moreMenu = { HomeMoreMenuScreen() },
            categories = { CategoriesScreen() },
            settings = { SettingsScreen() },
            transactionScreens = TransactionScreens(
                accountTransactions = {},
                categoryTransactions = {},
                newTransaction = { NewTransactionScreen(arg = it) },
                newTransfer = { NewTransferScreen() },
                transaction = { EditTransactionScreen(trnId = it) },
                transfer = { EditTransferScreen(batchId = it) }
            ),
            addFrame = { AddFrameScreen() },
            importBackup = { ImportBackupScreen() },
            exchangeRates = { ExchangeRatesScreen() },
            debugScreens = DebugScreens(
                test = { TestScreen() }
            )
        )
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
//        if (viewModel.isAppLockEnabled() && !hasFocus) {
//            window.setFlags(
//                WindowManager.LayoutParams.FLAG_SECURE,
//                WindowManager.LayoutParams.FLAG_SECURE
//            )
//        } else {
//            window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
//        }
    }

    override fun onResume() {
        super.onResume()
//        if (viewModel.isAppLockEnabled())
//            viewModel.checkUserInactiveTimeStatus()
    }

    override fun onPause() {
        super.onPause()
//        if (viewModel.isAppLockEnabled())
//            viewModel.startUserInactiveTimeCounter()
    }

    private fun authenticateWithOSBiometricsModal(
        biometricPromptCallback: BiometricPrompt.AuthenticationCallback
    ) {
        val executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt = BiometricPrompt(
            this, executor,
            biometricPromptCallback
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(
                getString(R.string.authentication_required)
            )
            .setSubtitle(
                getString(R.string.authentication_required_description)
            )
            .setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_WEAK or
                        BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
            .setConfirmationRequired(false)
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    // region Helpers for Compose UI
    fun contactSupport() {
        val caseNumber: Int = Random().nextInt(100) + 100

        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:") // only email apps should handle this

            putExtra(Intent.EXTRA_EMAIL, arrayOf(SUPPORT_EMAIL))
            putExtra(
                Intent.EXTRA_SUBJECT, "Ivy Wallet Support Request #" + caseNumber +
                        "0" + BuildConfig.VERSION_CODE
            )
            putExtra(Intent.EXTRA_TEXT, "")
        }

        try {
            startActivity(emailIntent)

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Email: $SUPPORT_EMAIL", Toast.LENGTH_LONG).show()
        }
    }

    override fun openUrlInBrowser(url: String) {
        try {
            val browserIntent = Intent(Intent.ACTION_VIEW)
            browserIntent.data = Uri.parse(url)
            startActivity(browserIntent)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(
                this,
                "No browser app found. Visit manually: $url",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun fileChooser(fileType: FileType, onFileChosen: (Uri) -> Unit) {
        filePickerLauncher.launch(fileType) {
            it?.let(onFileChosen)
        }
    }

    override fun createFile(fileName: String, onFileCreated: (Uri) -> Unit) {
        createFileLauncher.launch(fileName) {
            it?.let(onFileCreated)
        }
    }

    override fun shareIvyWallet() {
        val share = Intent.createChooser(
            Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, Constants.URL_IVY_WALLET_GOOGLE_PLAY)
                type = "text/plain"
            },
            null
        )
        startActivity(share)
    }

    fun openIvyWalletGooglePlayPage() {
        openGooglePlayAppPage(appId = Constants.IVY_WALLET_APP_ID)
    }

    override fun openGooglePlayAppPage(appId: String) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appId")))
        } catch (e: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$appId")
                )
            )
        }
    }

    override fun shareCSVFile(fileUri: Uri) {
        val intent = Intent.createChooser(
            Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, fileUri)
                type = "text/csv"
            }, null
        )
        startActivity(intent)
    }

    override fun shareZipFile(fileUri: Uri) {
        val intent = Intent.createChooser(
            Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, fileUri)
                type = "application/zip"
            }, null
        )
        startActivity(intent)
    }

    override fun reviewIvyWallet(dismissReviewCard: Boolean) {
        val manager = ReviewManagerFactory.create(this)
        val request = manager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // We got the ReviewInfo object
                val reviewInfo = task.result
                val flow = manager.launchReviewFlow(this, reviewInfo)
                flow.addOnCompleteListener {
                    // The flow has finished. The API does not indicate whether the user
                    // reviewed or not, or even whether the review dialog was shown. Thus, no
                    // matter the result, we continue our app flow.
                    if (dismissReviewCard) {
//                        customerJourneyLogic.dismissCard(CustomerJourneyLogic.rateUsCard())
                    }

                    openIvyWalletGooglePlayPage()
                }
            } else {
                openIvyWalletGooglePlayPage()
            }
        }
    }

    override fun <T> pinWidget(widget: Class<T>) {
        val appWidgetManager: AppWidgetManager = this.getSystemService(AppWidgetManager::class.java)
        val addTransactionWidget = ComponentName(this, widget)
        appWidgetManager.requestPinAppWidget(addTransactionWidget, null, null)
    }

    // region Date Picker
    override fun datePicker(
        minDate: LocalDate?,
        maxDate: LocalDate?,
        initialDate: LocalDate?,
        onDatePicked: (LocalDate) -> Unit
    ) {
        val picker = DatePickerDialog(this)

        if (minDate != null) {
            picker.datePicker.minDate = minDate.atTime(12, 0)
                .toEpochMilli(timeProvider)
        }

        if (maxDate != null) {
            picker.datePicker.maxDate = maxDate.atTime(12, 0)
                .toEpochMilli(timeProvider)
        }

        picker.setOnDateSetListener { _, year, month, dayOfMonth ->
            Timber.i("Date picked: $year year $month month day $dayOfMonth")
            onDatePicked(LocalDate.of(year, month + 1, dayOfMonth))
        }
        picker.show()

        if (initialDate != null) {
            picker.updateDate(
                initialDate.year,
                //month - 1 because LocalDate start from 1 and date picker starts from 0
                initialDate.monthValue - 1,
                initialDate.dayOfMonth
            )
        }
    }
    // endregion

    // region Time Picker
    override fun timePicker(onTimePicked: (LocalTime) -> Unit) {
        val nowLocal = timeNow()
        val picker = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                onTimePicked(LocalTime.of(hourOfDay, minute).withSecond(0))
            },
            nowLocal.hour, nowLocal.minute, DateFormat.is24HourFormat(this)
        )
        picker.show()
    }
    // endregion
    // endregion
}