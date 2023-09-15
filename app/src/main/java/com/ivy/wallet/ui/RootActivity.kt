package com.ivy.wallet.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.appwidget.AppWidgetManager
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.text.format.DateFormat
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.play.core.review.ReviewManagerFactory
import com.ivy.balance.BalanceScreen
import com.ivy.budgets.BudgetScreen
import com.ivy.categories.CategoriesScreen
import com.ivy.core.Constants
import com.ivy.core.RootScreen
import com.ivy.core.data.db.entity.TransactionType
import com.ivy.core.utils.toEpochMilli
import com.ivy.design.api.IvyUI
import com.ivy.donate.DonateScreen
import com.ivy.exchangerates.ExchangeRatesScreen
import com.ivy.home.customerjourney.CustomerJourneyCardsProvider
import com.ivy.importdata.csv.CSVScreen
import com.ivy.importdata.csvimport.ImportCSVScreen
import com.ivy.legacy.IvyWalletCtx
import com.ivy.legacy.appDesign
import com.ivy.legacy.utils.activityForResultLauncher
import com.ivy.legacy.utils.convertLocalToUTC
import com.ivy.legacy.utils.sendToCrashlytics
import com.ivy.legacy.utils.simpleActivityForResultLauncher
import com.ivy.legacy.utils.timeNowLocal
import com.ivy.loans.loan.LoansScreen
import com.ivy.loans.loandetails.LoanDetailsScreen
import com.ivy.navigation.BalanceScreen
import com.ivy.navigation.BudgetScreen
import com.ivy.navigation.CSVScreen
import com.ivy.navigation.CategoriesScreen
import com.ivy.navigation.DonateScreen
import com.ivy.navigation.EditPlannedScreen
import com.ivy.navigation.EditTransactionScreen
import com.ivy.navigation.ExchangeRatesScreen
import com.ivy.navigation.ImportScreen
import com.ivy.navigation.ItemStatisticScreen
import com.ivy.navigation.LoanDetailsScreen
import com.ivy.navigation.LoansScreen
import com.ivy.navigation.MainScreen
import com.ivy.navigation.Navigation
import com.ivy.navigation.NavigationRoot
import com.ivy.navigation.OnboardingScreen
import com.ivy.navigation.PieChartStatisticScreen
import com.ivy.navigation.PlannedPaymentsScreen
import com.ivy.navigation.ReportScreen
import com.ivy.navigation.Screen
import com.ivy.navigation.SearchScreen
import com.ivy.navigation.SettingsScreen
import com.ivy.navigation.TestScreen
import com.ivy.onboarding.OnboardingScreen
import com.ivy.piechart.PieChartStatisticScreen
import com.ivy.planned.edit.EditPlannedScreen
import com.ivy.planned.list.PlannedPaymentsScreen
import com.ivy.reports.ReportScreen
import com.ivy.resources.R
import com.ivy.search.SearchScreen
import com.ivy.settings.SettingsScreen
import com.ivy.test.TestScreen
import com.ivy.transaction.EditTransactionScreen
import com.ivy.transactions.ItemStatisticScreen
import com.ivy.wallet.BuildConfig
import com.ivy.wallet.ui.applocked.AppLockedScreen
import com.ivy.wallet.ui.main.MainScreen
import com.ivy.widget.balance.WalletBalanceWidgetReceiver
import com.ivy.widget.transaction.AddTransactionWidget
import com.ivy.widget.transaction.AddTransactionWidgetCompact
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@AndroidEntryPoint
class RootActivity : AppCompatActivity(), RootScreen {

    companion object {

        fun getIntent(context: Context): Intent = Intent(context, RootActivity::class.java)

        fun addTransactionStart(context: Context, type: TransactionType): Intent =
            Intent(context, RootActivity::class.java).apply {
                putExtra(RootViewModel.EXTRA_ADD_TRANSACTION_TYPE, type)
            }
    }

    @Inject
    lateinit var ivyContext: IvyWalletCtx

    @Inject
    lateinit var navigation: Navigation

    @Inject
    lateinit var customerJourneyLogic: CustomerJourneyCardsProvider

    private lateinit var googleSignInLauncher: ActivityResultLauncher<GoogleSignInClient>
    private lateinit var onGoogleSignInIdTokenResult: (idToken: String?) -> Unit

    private lateinit var createFileLauncher: ActivityResultLauncher<String>
    private lateinit var onFileCreated: (fileUri: Uri) -> Unit

    private lateinit var openFileLauncher: ActivityResultLauncher<Unit>
    private lateinit var onFileOpened: (fileUri: Uri) -> Unit

    private val viewModel: RootViewModel by viewModels()

    @OptIn(
        ExperimentalAnimationApi::class,
        ExperimentalFoundationApi::class
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setupActivityForResultLaunchers()

        // Make the app drawing area fullscreen (draw behind status and nav bars)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setupDatePicker()
        setupTimePicker()

        AddTransactionWidget.updateBroadcast(this)
        AddTransactionWidgetCompact.updateBroadcast(this)
        WalletBalanceWidgetReceiver.updateBroadcast(this)

        setContent {
            val viewModel: RootViewModel = viewModel()
            val isSystemInDarkTheme = isSystemInDarkTheme()

            LaunchedEffect(isSystemInDarkTheme) {
                viewModel.start(isSystemInDarkTheme, intent)
            }

            IvyUI(
                design = appDesign(ivyContext)
            ) {
                UI(viewModel)
            }
        }
    }

    @ExperimentalFoundationApi
    @ExperimentalAnimationApi
    @Composable
    private fun BoxWithConstraintsScope.UI(viewModel: RootViewModel) {
        val appLocked by viewModel.appLocked.collectAsState()

        when (appLocked) {
            null -> {
                // display nothing
            }

            true -> {
                AppLockedScreen(
                    onShowOSBiometricsModal = {
                        authenticateWithOSBiometricsModal(
                            biometricPromptCallback = viewModel.handleBiometricAuthResult()
                        )
                    },
                    onContinueWithoutAuthentication = {
                        viewModel.unlockApp()
                    }
                )
            }

            false -> {
                NavigationRoot(navigation = navigation) { screen ->
                    Screens(screen)
                }
            }
        }
    }

    @ExperimentalFoundationApi
    @ExperimentalAnimationApi
    @Composable
    private fun BoxWithConstraintsScope.Screens(screen: Screen?) {
        when (screen) {
            is MainScreen -> MainScreen(screen = screen)
            is OnboardingScreen -> OnboardingScreen(screen = screen)
            is ExchangeRatesScreen -> ExchangeRatesScreen()
            is EditTransactionScreen -> EditTransactionScreen(screen = screen)
            is ItemStatisticScreen -> ItemStatisticScreen(screen = screen)
            is PieChartStatisticScreen -> PieChartStatisticScreen(screen = screen)
            is CategoriesScreen -> CategoriesScreen(screen = screen)
            is SettingsScreen -> SettingsScreen(screen = screen)
            is PlannedPaymentsScreen -> PlannedPaymentsScreen(screen = screen)
            is EditPlannedScreen -> EditPlannedScreen(screen = screen)
            is BalanceScreen -> BalanceScreen(screen = screen)
            is TestScreen -> TestScreen(screen = screen)
            is ImportScreen -> ImportCSVScreen(screen = screen)
            is ReportScreen -> ReportScreen(screen = screen)
            is BudgetScreen -> BudgetScreen(screen = screen)
            is LoansScreen -> LoansScreen(screen = screen)
            is LoanDetailsScreen -> LoanDetailsScreen(screen = screen)
            is SearchScreen -> SearchScreen(screen = screen)
            is DonateScreen -> DonateScreen(screen = screen)
            is CSVScreen -> CSVScreen(screen = screen)
            null -> {
            }
        }
    }

    private fun setupDatePicker() {
        ivyContext.onShowDatePicker = { minDate,
                                        maxDate,
                                        initialDate,
                                        onDatePicked ->
            val picker = DatePickerDialog(this)

            if (minDate != null) {
                picker.datePicker.minDate = minDate.atTime(12, 0).toEpochMilli()
            }

            if (maxDate != null) {
                picker.datePicker.maxDate = maxDate.atTime(12, 0).toEpochMilli()
            }

            picker.setOnDateSetListener { _, year, month, dayOfMonth ->
                Timber.i("Date picked: $year year $month month day $dayOfMonth")
                onDatePicked(LocalDate.of(year, month + 1, dayOfMonth))
            }
            picker.show()

            if (initialDate != null) {
                picker.updateDate(
                    initialDate.year,
                    // month-1 because LocalDate start from 1 and date picker starts from 0
                    initialDate.monthValue - 1,
                    initialDate.dayOfMonth
                )
            }
        }
    }

    private fun setupTimePicker() {
        ivyContext.onShowTimePicker = { onTimePicked ->
            val nowLocal = timeNowLocal()
            val picker = TimePickerDialog(
                this,
                { _, hourOfDay, minute ->
                    onTimePicked(
                        LocalTime.of(hourOfDay, minute)
                            .convertLocalToUTC().withSecond(0)
                    )
                },
                nowLocal.hour, nowLocal.minute, DateFormat.is24HourFormat(this)
            )
            picker.show()
        }
    }

    private fun setupActivityForResultLaunchers() {
        googleSignInLauncher()

        createFileLauncher()

        openFileLauncher()
    }

    private fun googleSignInLauncher() {
        googleSignInLauncher = activityForResultLauncher(
            createIntent = { _, client ->
                client.signInIntent
            }
        ) { _, intent ->
            try {
                val task: Task<GoogleSignInAccount> =
                    GoogleSignIn.getSignedInAccountFromIntent(intent)
                val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
                val idToken = account.idToken
                Timber.d("idToken = $idToken")

                onGoogleSignInIdTokenResult(idToken)
            } catch (e: ApiException) {
                e.sendToCrashlytics("GOOGLE_SIGN_IN - registerGoogleSignInContract(): ApiException")
                e.printStackTrace()
                onGoogleSignInIdTokenResult(null)
            }
        }

        ivyContext.googleSignIn = { idTokenResult: (String?) -> Unit ->
            onGoogleSignInIdTokenResult = idTokenResult

            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .requestIdToken("364763737033-t1d2qe7s0s8597k7anu3sb2nq79ot5tp.apps.googleusercontent.com")
                .build()
            val googleSignInClient = GoogleSignIn.getClient(this, gso)
            googleSignInLauncher.launch(googleSignInClient)
        }
    }

    private fun createFileLauncher() {
        createFileLauncher = activityForResultLauncher(
            createIntent = { _, fileName ->
                Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "application/csv"
                    putExtra(Intent.EXTRA_TITLE, fileName)

                    // Optionally, specify a URI for the directory that should be opened in
                    // the system file picker before your app creates the document.
                    putExtra(
                        DocumentsContract.EXTRA_INITIAL_URI,
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                            .toURI()
                    )
                }
            }
        ) { _, intent ->
            intent?.data?.also {
                onFileCreated(it)
            }
        }

        ivyContext.createNewFile = { fileName, onFileCreatedCallback ->
            onFileCreated = onFileCreatedCallback

            createFileLauncher.launch(fileName)
        }
    }

    private fun openFileLauncher() {
        openFileLauncher = simpleActivityForResultLauncher(
            intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"
            }
        ) { _, intent ->
            intent?.data?.also {
                onFileOpened(it)
            }
        }

        ivyContext.openFile = { onFileOpenedCallback ->
            onFileOpened = onFileOpenedCallback

            openFileLauncher.launch(Unit)
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (viewModel.isAppLockEnabled() && !hasFocus) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.isAppLockEnabled()) {
            viewModel.checkUserInactiveTimeStatus()
        }
    }

    override fun onPause() {
        super.onPause()
        if (viewModel.isAppLockEnabled()) {
            viewModel.startUserInactiveTimeCounter()
        }
    }

    private fun authenticateWithOSBiometricsModal(
        biometricPromptCallback: BiometricPrompt.AuthenticationCallback
    ) {
        val executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt = BiometricPrompt(
            this,
            executor,
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

    override fun onBackPressed() {
        if (viewModel.isAppLocked()) {
            super.onBackPressed()
        } else {
            if (!navigation.onBackPressed()) {
                super.onBackPressed()
            }
        }
    }

    override fun openUrlInBrowser(url: String) {
        try {
            val browserIntent = Intent(Intent.ACTION_VIEW)
            browserIntent.data = Uri.parse(url)
            startActivity(browserIntent)
        } catch (e: Exception) {
            e.printStackTrace()
            e.sendToCrashlytics("Cannot open URL in browser, intent not supported.")
            Toast.makeText(
                this,
                "No browser app found. Visit manually: $url",
                Toast.LENGTH_LONG
            ).show()
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
            },
            null
        )
        startActivity(intent)
    }

    override fun shareZipFile(fileUri: Uri) {
        val intent = Intent.createChooser(
            Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, fileUri)
                type = "application/zip"
            },
            null
        )
        startActivity(intent)
    }

    override val isDebug: Boolean
        get() = BuildConfig.DEBUG
    override val buildVersionName: String
        get() = BuildConfig.VERSION_NAME
    override val buildVersionCode: Int
        get() = BuildConfig.VERSION_CODE

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
                        customerJourneyLogic.dismissCard(CustomerJourneyCardsProvider.rateUsCard())
                    }

                    openGooglePlayAppPage(packageName)
                }
            } else {
                openGooglePlayAppPage(packageName)
            }
        }
    }

    override fun <T> pinWidget(widget: Class<T>) {
        val appWidgetManager: AppWidgetManager = this.getSystemService(AppWidgetManager::class.java)
        val addTransactionWidget = ComponentName(this, widget)
        appWidgetManager.requestPinAppWidget(addTransactionWidget, null, null)
    }
}
