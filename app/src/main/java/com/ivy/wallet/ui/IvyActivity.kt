package com.ivy.wallet.ui

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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.insets.systemBarsPadding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.play.core.review.ReviewManagerFactory
import com.ivy.wallet.BuildConfig
import com.ivy.wallet.Constants
import com.ivy.wallet.R
import com.ivy.wallet.base.*
import com.ivy.wallet.logic.CustomerJourneyLogic
import com.ivy.wallet.model.TransactionType
import com.ivy.wallet.ui.analytics.AnalyticsReport
import com.ivy.wallet.ui.balance.BalanceScreen
import com.ivy.wallet.ui.bankintegrations.ConnectBankScreen
import com.ivy.wallet.ui.budget.BudgetScreen
import com.ivy.wallet.ui.category.CategoriesScreen
import com.ivy.wallet.ui.csvimport.ImportCSVScreen
import com.ivy.wallet.ui.edit.EditTransactionScreen
import com.ivy.wallet.ui.main.MainScreen
import com.ivy.wallet.ui.onboarding.OnboardingScreen
import com.ivy.wallet.ui.paywall.PaywallScreen
import com.ivy.wallet.ui.planned.edit.EditPlannedScreen
import com.ivy.wallet.ui.planned.list.PlannedPaymentsScreen
import com.ivy.wallet.ui.reports.ReportScreen
import com.ivy.wallet.ui.settings.SettingsScreen
import com.ivy.wallet.ui.statistic.level1.PieChartStatisticScreen
import com.ivy.wallet.ui.statistic.level2.ItemStatisticScreen
import com.ivy.wallet.ui.test.TestScreen
import com.ivy.wallet.ui.theme.*
import com.ivy.wallet.ui.theme.components.IvyButton
import com.ivy.wallet.ui.webView.WebViewScreen
import com.ivy.wallet.widget.AddTransactionWidget
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalTime
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class IvyActivity : AppCompatActivity() {

    companion object {
        const val SUPPORT_EMAIL = "iliyan.germanov971@gmail.com"
        fun getIntent(context: Context): Intent = Intent(context, IvyActivity::class.java)

        fun addTransactionStart(context: Context, type: TransactionType): Intent =
            Intent(context, IvyActivity::class.java).apply {
                putExtra(IvyViewModel.EXTRA_ADD_TRANSACTION_TYPE, type)
            }
    }

    @Inject
    lateinit var ivyContext: IvyContext

    @Inject
    lateinit var customerJourneyLogic: CustomerJourneyLogic

    private lateinit var googleSignInContract: ActivityResultLauncher<GoogleSignInClient>
    private lateinit var onGoogleSignInIdTokenResult: (idToken: String?) -> Unit

    private lateinit var createFileContract: ActivityResultLauncher<Unit>
    private var fileName = UUID.randomUUID().toString() //random UUID is just an initial value
    private lateinit var onFileCreated: (fileUri: Uri) -> Unit

    private lateinit var openFileContract: ActivityResultLauncher<Unit>
    private lateinit var onFileOpened: (fileUri: Uri) -> Unit

    private var appLockedEnabled: Boolean = false

    @ExperimentalAnimationApi
    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Make the app drawing area fullscreen (draw behind status and nav bars)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        ivyContext.onShowDatePicker = { minDate,
                                        maxDate,
                                        initialDate,
                                        onDatePicked ->
            val picker = android.app.DatePickerDialog(this)

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
                    //month-1 because LocalDate start from 1 and date picker starts from 0
                    initialDate.monthValue - 1,
                    initialDate.dayOfMonth
                )
            }
        }

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

        ivyContext.onContactSupport = {
            contactSupport()
        }


        googleSignInContract = registerGoogleSignInContract()
        ivyContext.googleSignIn = { idTokenResult: (String?) -> Unit ->
            onGoogleSignInIdTokenResult = idTokenResult

            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .requestIdToken("364763737033-t1d2qe7s0s8597k7anu3sb2nq79ot5tp.apps.googleusercontent.com")
                .build()
            val googleSignInClient = GoogleSignIn.getClient(this, gso)
            googleSignInContract.launch(googleSignInClient)
        }

        createFileContract = registerCreateFileContract()
        ivyContext.createNewFile = { fileName, onFileCreatedCallback ->
            this.fileName = fileName
            onFileCreated = onFileCreatedCallback

            createFileContract.launch(Unit)
        }

        openFileContract = registerOpenFileContract()
        ivyContext.openFile = { onFileOpenedCallback ->
            onFileOpened = onFileOpenedCallback

            openFileContract.launch(Unit)
        }

        AddTransactionWidget.updateBroadcast(this)

        setContent {
            val viewModel : IvyViewModel = viewModel()
            val isSystemInDarkTheme = isSystemInDarkTheme()

            val appLocked by viewModel.appLocked.observeAsState(false)
            val isUserInactive = ivyContext.isUserInactive

            LaunchedEffect(isSystemInDarkTheme) {
                viewModel.start(isSystemInDarkTheme, intent)
                viewModel.initBilling(this@IvyActivity)
            }

            IvyApp(
                ivyContext = ivyContext,
            ) {
                if (appLocked) {
                    appLockedEnabled = true
                    ivyContext.navigateTo(
                        Screen.AppLock({
                            authenticateWithOSBiometricsModal(
                                viewModel.handleBiometricAuthenticationResult(onAuthSuccess = {
                                    viewModel.unlockAuthenticated(intent)
                                })
                            )
                        }, { viewModel.unlockAuthenticated(intent) }), false
                    )
                }

                if (appLockedEnabled && isUserInactive.value) {
                    ivyContext.resetUserInActiveTimer()
                    ivyContext.navigateTo(
                        Screen.AppLock({
                            authenticateWithOSBiometricsModal(
                                viewModel.handleBiometricAuthenticationResult(onAuthSuccess = {
                                    ivyContext.back()
                                })
                            )
                        }, { ivyContext.back() })
                    )
                }

                when (val screen = ivyContext.currentScreen) {

                    is Screen.Main -> MainScreen(screen = screen)
                    is Screen.Onboarding -> OnboardingScreen(screen = screen)
                    is Screen.EditTransaction -> EditTransactionScreen(screen = screen)
                    is Screen.ItemStatistic -> ItemStatisticScreen(screen = screen)
                    is Screen.PieChartStatistic -> PieChartStatisticScreen(screen = screen)
                    is Screen.Categories -> CategoriesScreen(screen = screen)
                    is Screen.Settings -> SettingsScreen(screen = screen)
                    is Screen.PlannedPayments -> PlannedPaymentsScreen(screen = screen)
                    is Screen.EditPlanned -> EditPlannedScreen(screen = screen)
                    is Screen.BalanceScreen -> BalanceScreen(screen = screen)
                    is Screen.Paywall -> PaywallScreen(
                        screen = screen,
                        activity = this@IvyActivity
                    )
                    is Screen.Test -> TestScreen(screen = screen)
                    is Screen.AnalyticsReport -> AnalyticsReport(screen = screen)
                    is Screen.Import -> ImportCSVScreen(screen = screen)
                    is Screen.ConnectBank -> ConnectBankScreen(screen = screen)
                    is Screen.Report -> ReportScreen(screen = screen)
                    is Screen.Budget -> BudgetScreen(screen = screen)
                    is Screen.WebView -> WebViewScreen(screen = screen)
                    is Screen.AppLock -> {
                        AppLockedScreen(
                            onShowOSBiometricsModal = {
                                screen.onShowOSBiometricsModal()
                            },
                            onContinueWithoutAuthentication = {
                                screen.onContinueWithoutAuthentication()
                            })
                    }
                    null -> {
                    }
                }
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (appLockedEnabled && !hasFocus) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
        } else
            window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
    }

    override fun onResume() {
        super.onResume()
        if (appLockedEnabled)
            ivyContext.checkUserInactiveTimeStatus()
    }

    override fun onPause() {
        super.onPause()
        if (appLockedEnabled)
            ivyContext.startUserInactiveTimeCounter()
    }

    @Composable
    private fun AppLockedScreen(
        onShowOSBiometricsModal: () -> Unit,
        onContinueWithoutAuthentication: () -> Unit,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(32.dp))

            Text(
                modifier = Modifier
                    .background(IvyTheme.colors.medium, Shapes.roundedFull)
                    .padding(vertical = 12.dp)
                    .padding(horizontal = 32.dp),
                text = "APP LOCKED",
                style = Typo.body2.style(
                    fontWeight = FontWeight.ExtraBold,
                )
            )

            Spacer(Modifier.weight(1f))

            Image(
                modifier = Modifier
                    .size(width = 96.dp, height = 138.dp),
                painter = painterResource(id = R.drawable.ic_fingerprint),
                colorFilter = ColorFilter.tint(IvyTheme.colors.medium),
                contentScale = ContentScale.FillBounds,
                contentDescription = "unlock icon"
            )

            Spacer(Modifier.weight(1f))

            Text(
                text = "Authenticate to enter the app",
                style = Typo.body2.style(
                    fontWeight = FontWeight.SemiBold,
                    color = Gray
                )
            )

            Spacer(Modifier.height(24.dp))

            val context = LocalContext.current
            IvyButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                text = "Unlock",
                textStyle = Typo.body2.style(
                    color = White,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                ),
                wrapContentMode = false
            ) {
                if (hasLockScreen(context)) {
                    onShowOSBiometricsModal()
                } else {
                    onContinueWithoutAuthentication()
                }
            }
            Spacer(Modifier.height(24.dp))

            //To automatically launch the biometric screen on load of this composable
            LaunchedEffect(true)
            {
                if (hasLockScreen(context)) {
                    onShowOSBiometricsModal()
                } else {
                    onContinueWithoutAuthentication()
                }
            }
        }
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
                "Authentication required"
            )
            .setSubtitle(
                "Prove that you have access to this device to unlock the app."
            )
            .setDeviceCredentialAllowed(true)
            .setConfirmationRequired(false)
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    override fun onBackPressed() {
        if (!ivyContext.onBackPressed()) {
            super.onBackPressed()
        }
    }

    private fun registerGoogleSignInContract(): ActivityResultLauncher<GoogleSignInClient> {
        return registerForActivityResult(GoogleSignInContract()) { intent: Intent ->
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
    }

    class GoogleSignInContract : ActivityResultContract<GoogleSignInClient, Intent>() {
        override fun createIntent(context: Context, client: GoogleSignInClient): Intent {
            return client.signInIntent
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Intent? {
            return intent
        }
    }

    private fun registerCreateFileContract(): ActivityResultLauncher<Unit> {
        return registerForActivityResult(CreateFileContract()) { intent ->
            intent?.data?.also {
                onFileCreated(it)
            }
        }
    }

    inner class CreateFileContract : ActivityResultContract<Unit, Intent>() {
        override fun createIntent(context: Context, param: Unit): Intent {
            return Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
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

        override fun parseResult(resultCode: Int, intent: Intent?): Intent? {
            return intent
        }
    }

    private fun registerOpenFileContract(): ActivityResultLauncher<Unit> {
        return registerForActivityResult(OpenFileContract()) { intent ->
            intent?.data?.also {
                onFileOpened(it)
            }
        }
    }

    inner class OpenFileContract : ActivityResultContract<Unit, Intent>() {
        override fun createIntent(context: Context, input: Unit?): Intent {
            return Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"
            }
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Intent? {
            return intent
        }

    }

    private fun contactSupport() {
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

    fun openUrlInBrowser(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }

    fun shareIvyWallet() {
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

    fun openGooglePlayAppPage(appId: String = packageName) {
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

    fun shareCSVFile(fileUri: Uri) {
        val intent = Intent.createChooser(
            Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, fileUri)
                type = "text/csv"
            }, null
        )
        startActivity(intent)
    }

    private fun openUrlInDefaultBrowser(url: String) {
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

    fun reviewIvyWallet(dismissReviewCard: Boolean) {
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
                        customerJourneyLogic.dismissCard(CustomerJourneyLogic.rateUsCard())
                    }

                    openGooglePlayAppPage()
                }
            } else {
                openGooglePlayAppPage()
            }
        }
    }

    fun pinAddTransactionWidget() {
        val appWidgetManager: AppWidgetManager = this.getSystemService(AppWidgetManager::class.java)
        val addTransactionWidget = ComponentName(this, AddTransactionWidget::class.java)
        appWidgetManager.requestPinAppWidget(addTransactionWidget, null, null)
    }

    @Preview
    @Composable
    private fun Preview_Locked() {
        IvyAppPreview {
            AppLockedScreen(
                onContinueWithoutAuthentication = {},
                onShowOSBiometricsModal = {}
            )
        }
    }
}
