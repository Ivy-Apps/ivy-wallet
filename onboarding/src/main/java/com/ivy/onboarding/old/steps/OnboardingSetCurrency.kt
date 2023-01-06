//package com.ivy.onboarding.steps
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.material.Text
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import com.ivy.resources.R
//import com.ivy.data.IvyCurrency
//import com.ivy.design.l0_system.UI
//import com.ivy.design.l0_system.style
//import com.ivy.design.util.IvyPreview
//
//import com.ivy.wallet.ui.theme.GradientIvy
//import com.ivy.wallet.ui.theme.White
//import com.ivy.wallet.ui.theme.components.BackButton
//import com.ivy.wallet.ui.theme.components.CurrencyPicker
//import com.ivy.wallet.ui.theme.components.GradientCutBottom
//import com.ivy.wallet.ui.theme.components.OnboardingButton
//import com.ivy.wallet.utils.setStatusBarDarkTextCompat
//
//@Composable
//fun BoxWithConstraintsScope.OnboardingSetCurrency(
//    preselectedCurrency: IvyCurrency,
//    onSetCurrency: (IvyCurrency) -> Unit
//) {
//    setStatusBarDarkTextCompat(darkText = UI.colors.isLight)
//
//    var currency by remember { mutableStateOf(preselectedCurrency) }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .statusBarsPadding()
//            .navigationBarsPadding(),
//    ) {
//        Spacer(Modifier.height(16.dp))
//
//        var keyboardVisible by remember {
//            mutableStateOf(false)
//        }
//
//
//        BackButton(
//            modifier = Modifier.padding(start = 20.dp)
//        ) {
//            nav.onBackPressed()
//        }
//
//        if (!keyboardVisible) {
//            Spacer(Modifier.height(24.dp))
//
//            Text(
//                modifier = Modifier.padding(horizontal = 32.dp),
//                text = stringResource(R.string.set_currency),
//                style = UI.typo.h2.style(
//                    fontWeight = FontWeight.Black
//                )
//            )
//        }
//
//        Spacer(Modifier.height(24.dp))
//
//        CurrencyPicker(
//            modifier = Modifier
//                .fillMaxSize(),
//            initialSelectedCurrency = null,
//            preselectedCurrency = preselectedCurrency,
//            includeKeyboardShownInsetSpacer = true,
//            lastItemSpacer = 120.dp,
//            onKeyboardShown = { keyboardShown ->
//                keyboardVisible = keyboardShown
//            }
//        ) {
//            currency = it
//        }
//    }
//
//    GradientCutBottom(
//        height = 160.dp
//    )
//
//    OnboardingButton(
//        Modifier
//            .fillMaxWidth()
//            .padding(horizontal = 24.dp)
//            .align(Alignment.BottomCenter)
//            .navigationBarsPadding()
//            .padding(bottom = 20.dp),
//
//        text = stringResource(R.string.set),
//        textColor = White,
//        backgroundGradient = GradientIvy,
//        hasNext = true,
//        enabled = true
//    ) {
//        onSetCurrency(currency)
//    }
//
//}
//
//@Preview
//@Composable
//private fun Preview() {
//    IvyPreview {
//        OnboardingSetCurrency(
//            preselectedCurrency = IvyCurrency.getDefault()
//        ) {
//
//        }
//    }
//}