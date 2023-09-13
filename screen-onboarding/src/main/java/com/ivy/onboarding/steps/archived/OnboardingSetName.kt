package com.ivy.onboarding.steps.archived

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.legacy.IvyWalletPreview
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.resources.R
import com.ivy.wallet.ui.theme.GradientIvy
import com.ivy.wallet.ui.theme.White
import com.ivy.wallet.ui.theme.components.IvyOutlinedTextField
import com.ivy.wallet.ui.theme.components.OnboardingButton
import com.ivy.legacy.utils.addKeyboardListener
import com.ivy.legacy.utils.densityScope
import com.ivy.legacy.utils.isNotNullOrBlank
import com.ivy.legacy.utils.keyboardOnlyWindowInsets
import com.ivy.legacy.utils.onScreenStart
import com.ivy.legacy.utils.springBounceSlow

@Composable
fun OnboardingSetName(
    onNameSet: (String) -> Unit
) {
    val rootView = LocalView.current
    var keyboardShown by remember { mutableStateOf(false) }

    onScreenStart {
        rootView.addKeyboardListener {
            keyboardShown = it
        }
    }

    val keyboardShownInsetDp by animateDpAsState(
        targetValue = densityScope {
            if (keyboardShown) keyboardOnlyWindowInsets().bottom.toDp() else 24.dp
        },
        animationSpec = springBounceSlow()
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Spacer(Modifier.height(40.dp))

        Image(
            modifier = Modifier
                .padding(start = 32.dp)
                .size(
                    width = 56.dp,
                    height = 48.dp
                ),
            painter = painterResource(id = R.drawable.ivy_wallet_logo),
            contentScale = ContentScale.FillBounds,
            contentDescription = "Ivy Wallet logo"
        )

        Spacer(Modifier.height(40.dp))

        Text(
            modifier = Modifier.padding(horizontal = 32.dp),
            text = stringResource(R.string.enter_your_name),
            style = UI.typo.h2.style(
                fontWeight = FontWeight.ExtraBold
            )
        )

        Spacer(Modifier.weight(1f))

        var nameTextField by remember { mutableStateOf(TextFieldValue("")) }

        val nameFocus = FocusRequester()

        onScreenStart {
            nameFocus.requestFocus()
        }

        IvyOutlinedTextField(
            Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth()
                .focusRequester(nameFocus),
            value = nameTextField,
            hint = stringResource(R.string.what_is_your_name),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                autoCorrect = false,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    if (nameTextField.text.trim().isNotNullOrBlank()) {
                        onNameSet(nameTextField.text.trim())
                    }
                }
            )
        ) {
            nameTextField = it.copy(text = it.text.trim())
        }

        Spacer(Modifier.height(32.dp))

        OnboardingButton(
            Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth(),
            text = stringResource(R.string.enter),
            textColor = White,
            backgroundGradient = GradientIvy,
            hasNext = true,
            enabled = nameTextField.text.trim().isNotNullOrBlank()
        ) {
            onNameSet(nameTextField.text.trim())
        }

        Spacer(Modifier.height(24.dp))

        if (keyboardShownInsetDp.value > 0) {
            Spacer(Modifier.height(keyboardShownInsetDp))
        }
    }
}

@Preview
@Composable
private fun Preview() {
    IvyWalletPreview {
        OnboardingSetName {
        }
    }
}
