@file:Suppress("UnusedPrivateMember")

import androidx.compose.runtime.Composable
import com.ivy.onboarding.steps.OnboardingAccountUiTest
import com.ivy.onboarding.steps.OnboardingCategoriesUiTest
import com.ivy.onboarding.steps.OnboardingImportCSVFileUiTest
import com.ivy.onboarding.steps.OnboardingSetCurrencyUiTest
import com.ivy.onboarding.steps.OnboardingSplashLoginUiTest
import com.ivy.ui.annotation.IvyPreviews

@IvyPreviews
@Composable
private fun PreviewOnboardingAccountsLight() {
    OnboardingAccountUiTest(isDark = false, false)
}

@IvyPreviews
@Composable
private fun PreviewOnboardingAccountsDark() {
    OnboardingAccountUiTest(isDark = true, false)
}

@IvyPreviews
@Composable
private fun PreviewOnboardingAccountsLightEmptyState() {
    OnboardingAccountUiTest(isDark = false, true)
}

@IvyPreviews
@Composable
private fun PreviewOnboardingAccountsDarkEmptyState() {
    OnboardingAccountUiTest(isDark = true, true)
}

@IvyPreviews
@Composable
private fun PreviewOnboardingCategoriesLight() {
    OnboardingCategoriesUiTest(isDark = false, false)
}

@IvyPreviews
@Composable
private fun PreviewOnboardingCategoriesDark() {
    OnboardingCategoriesUiTest(isDark = true, false)
}

@IvyPreviews
@Composable
private fun PreviewOnboardingCategoriesLightEmptyState() {
    OnboardingCategoriesUiTest(isDark = false, true)
}

@IvyPreviews
@Composable
private fun PreviewOnboardingCategoriesDarkEmptyState() {
    OnboardingCategoriesUiTest(isDark = true, true)
}

@IvyPreviews
@Composable
private fun PreviewOnboardingSetCurrencyLight() {
    OnboardingSetCurrencyUiTest(isDark = false)
}

@IvyPreviews
@Composable
private fun PreviewOnboardingSetCurrencyDark() {
    OnboardingSetCurrencyUiTest(isDark = true)
}

@IvyPreviews
@Composable
private fun PreviewOnboardingSplashLoginLight() {
    OnboardingSplashLoginUiTest(isDark = false)
}

@IvyPreviews
@Composable
private fun PreviewOnboardingSplashLoginDark() {
    OnboardingSplashLoginUiTest(isDark = true)
}

@IvyPreviews
@Composable
private fun PreviewOnboardingImportCSVFileLight() {
    OnboardingImportCSVFileUiTest(isDark = false)
}

@IvyPreviews
@Composable
private fun PreviewOnboardingImportCSVFileDark() {
    OnboardingImportCSVFileUiTest(isDark = true)
}