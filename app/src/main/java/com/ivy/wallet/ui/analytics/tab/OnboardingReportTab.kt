package com.ivy.wallet.ui.analytics.tab

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.wallet.ui.IvyAppPreview
import com.ivy.wallet.ui.analytics.DataCircle
import com.ivy.wallet.ui.analytics.model.OnboardingReport
import com.ivy.wallet.ui.theme.*

@Composable
fun ColumnScope.OnboardingReportTab(
    report: OnboardingReport
) {
    Row(
        modifier = Modifier
            .weight(1f)
            .horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(Modifier.width(32.dp))

        DataCircle(
            count = report.onboardingStarted ?: 0,
            metric = "Started",
            circleColor = Blue,
            avgCount = report.avgOnboardingStarted,
            parentCount = null
        )

        Spacer(Modifier.width(32.dp))

        LoginGoogleOrLocalAccount(
            report = report
        )

        Spacer(Modifier.width(32.dp))

//        PrivacyTCorSetName(
//            report = report
//        )
//
//        Spacer(Modifier.width(32.dp))

        DataCircle(
            count = report.onboardingSetCurrency ?: 0,
            metric = "Set currency",
            circleColor = IvyLight,
            parentCount = report.onboardingLogin?.plus(report.onboardingLocalAccount ?: 0)
        )

        Spacer(Modifier.width(32.dp))

        DataCircle(
            count = report.onboardingAddAccount ?: 0,
            metric = "Add account",
            circleColor = IvyDark,
            parentCount = report.onboardingSetCurrency
        )

        Spacer(Modifier.width(32.dp))

        DataCircle(
            count = report.onboardingCompleted ?: 0,
            metric = "Completed",
            circleColor = Green,
            parentCount = report.onboardingAddAccount
        )

        Spacer(Modifier.width(32.dp))
    }
}

@Composable
private fun LoginGoogleOrLocalAccount(
    report: OnboardingReport
) {
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(32.dp))

        DataCircle(
            count = report.onboardingLogin ?: 0,
            metric = "Login Google",
            circleColor = Red,
            parentCount = report.onboardingStarted,
        )


        Spacer(Modifier.height(32.dp))

        DataCircle(
            count = report.onboardingLocalAccount ?: 0,
            metric = "Local Account",
            circleColor = Gray,
            parentCount = report.onboardingStarted,
        )

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun PrivacyTCorSetName(
    report: OnboardingReport
) {
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(32.dp))

        DataCircle(
            count = report.onboardingPrivacyTCAccepted ?: 0,
            metric = "Privacy & TC",
            circleColor = IvyDark,
            parentCount = report.onboardingLogin,
        )

        Spacer(Modifier.height(32.dp))

        DataCircle(
            count = report.onboardingSetName ?: 0,
            metric = "Set name",
            circleColor = Orange,
            parentCount = report.onboardingLocalAccount,
        )

        Spacer(Modifier.height(32.dp))
    }
}

@Preview
@Composable
private fun Preview() {
    IvyAppPreview {
        Column(Modifier.fillMaxSize()) {
            OnboardingReportTab(
                report = OnboardingReport(
                    onboardingStarted = 1000,
                    avgOnboardingStarted = 1.34,

                    onboardingLogin = 970,
                    onboardingPrivacyTCAccepted = 950,
                    onboardingLocalAccount = 25,
                    onboardingSetName = 23,
                    onboardingSetCurrency = 900,
                    onboardingAddAccount = 800,
                    onboardingCompleted = 799
                )
            )
        }
    }
}