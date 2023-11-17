package com.ivy.onboarding.steps

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.legacy.Constants
import com.ivy.legacy.IvyWalletCtx
import com.ivy.legacy.IvyWalletPreview
import com.ivy.legacy.ivyWalletCtx
import com.ivy.legacy.utils.clickableNoIndication
import com.ivy.legacy.utils.drawColoredShadow
import com.ivy.legacy.utils.lerp
import com.ivy.legacy.utils.openUrl
import com.ivy.legacy.utils.springBounceSlow
import com.ivy.legacy.utils.thenIf
import com.ivy.legacy.utils.toDensityDp
import com.ivy.legacy.utils.toDensityPx
import com.ivy.onboarding.OnboardingState
import com.ivy.resources.R
import com.ivy.wallet.ui.theme.Gradient
import com.ivy.wallet.ui.theme.Gray
import com.ivy.wallet.ui.theme.Green
import com.ivy.wallet.ui.theme.components.IvyIcon
import kotlin.math.roundToInt

@Composable
fun BoxWithConstraintsScope.OnboardingSplashLogin(
    onboardingState: OnboardingState,
    onSkip: () -> Unit,
) {
    var internalSwitch by remember { mutableStateOf(true) }

    val transition = updateTransition(
        targetState = if (!internalSwitch) OnboardingState.LOGIN else onboardingState,
        label = "Splash"
    )

    val logoWidth by transition.animateDp(
        transitionSpec = {
            springBounceSlow()
        },
        label = "logoWidth"
    ) {
        when (it) {
            OnboardingState.SPLASH -> 113.dp
            else -> 76.dp
        }
    }

    val logoHeight by transition.animateDp(
        transitionSpec = {
            springBounceSlow()
        },
        label = "logoHeight"
    ) {
        when (it) {
            OnboardingState.SPLASH -> 96.dp
            else -> 64.dp
        }
    }

    val ivyContext = ivyWalletCtx()

    val spacerTop by transition.animateDp(
        transitionSpec = {
            springBounceSlow()
        },
        label = "spacerTop"
    ) {
        when (it) {
            OnboardingState.SPLASH -> {
                (ivyContext.screenHeight / 2f - logoHeight.toDensityPx() / 2f).toDensityDp()
            }

            else -> 56.dp
        }
    }

    val percentTransition by transition.animateFloat(
        transitionSpec = {
            springBounceSlow()
        },
        label = "percentTransition"
    ) {
        when (it) {
            OnboardingState.SPLASH -> 0f
            else -> 1f
        }
    }

    val marginTextTop by transition.animateDp(
        transitionSpec = {
            springBounceSlow()
        },
        label = "marginTextTop"
    ) {
        when (it) {
            OnboardingState.SPLASH -> 64.dp
            else -> 40.dp
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(UI.colors.pure)
            .systemBarsPadding()
            .navigationBarsPadding()
    ) {
        Spacer(Modifier.height(spacerTop))

        Image(
            modifier = Modifier
                .size(
                    width = logoWidth,
                    height = logoHeight
                )
                .layout { measurable, constraints ->
                    val placeable = measurable.measure(constraints)

                    val xSplash = ivyContext.screenWidth / 2f - placeable.width / 2
                    val xLogin = 24.dp.toPx()

                    layout(placeable.width, placeable.height) {
                        placeable.placeRelative(
                            x = lerp(xSplash, xLogin, percentTransition).roundToInt(),
                            y = 0,
                        )
                    }
                }
                .clickableNoIndication {
                    internalSwitch = !internalSwitch
                },
            painter = painterResource(id = R.drawable.ivy_wallet_logo),
            contentScale = ContentScale.FillBounds,
            contentDescription = "Ivy Wallet logo"
        )

        Spacer(Modifier.height(marginTextTop))

        Text(
            modifier = Modifier.animateXCenterToLeft(
                ivyContext = ivyContext,
                percentTransition = percentTransition
            ),
            text = "Ivy Wallet",
            style = UI.typo.h2.style(
                color = UI.colors.pureInverse,
                fontWeight = FontWeight.ExtraBold
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            modifier = Modifier.animateXCenterToLeft(
                ivyContext = ivyContext,
                percentTransition = percentTransition
            ),
            text = stringResource(R.string.your_personal_money_manager),
            style = UI.typo.b2.style(
                color = UI.colors.pureInverse,
                fontWeight = FontWeight.SemiBold
            )
        )

        val uriHandler = LocalUriHandler.current
        Text(
            modifier = Modifier
                .animateXCenterToLeft(
                    ivyContext = ivyContext,
                    percentTransition = percentTransition
                )
                .clickable {
                    openUrl(
                        uriHandler = uriHandler,
                        url = Constants.URL_IVY_WALLET_REPO
                    )
                }
                .padding(vertical = 8.dp)
                .padding(end = 8.dp),
            text = stringResource(R.string.opensource),
            style = UI.typo.c.style(
                color = Green,
                fontWeight = FontWeight.Bold
            )
        )

        LoginSection(
            percentTransition = percentTransition,
            onSkip = onSkip
        )
    }
}

private fun Modifier.animateXCenterToLeft(
    ivyContext: IvyWalletCtx,
    percentTransition: Float
): Modifier {
    return this.layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)

        layout(placeable.width, placeable.height) {
            val xSplash = ivyContext.screenWidth / 2f - placeable.width / 2
            val xLogin = 32.dp.toPx()

            placeable.placeRelative(
                x = lerp(xSplash, xLogin, percentTransition).roundToInt(),
                y = 0
            )
        }
    }
}

@Composable
private fun LoginSection(
    percentTransition: Float,
    onSkip: () -> Unit
) {
    if (percentTransition > 0.01f) {
        Column(
            modifier = Modifier
                .alpha(percentTransition),
        ) {
            Spacer(Modifier.height(16.dp))
            Spacer(Modifier.weight(1f))

            LocalAccountExplanation()

            Spacer(Modifier.height(16.dp))

            LoginButton(
                icon = R.drawable.ic_local_account,
                text = stringResource(R.string.offline_account),
                textColor = UI.colors.pureInverse,
                backgroundGradient = Gradient.solid(UI.colors.medium),
                hasShadow = false
            ) {
                onSkip()
            }

            Spacer(Modifier.weight(3f))
            Spacer(Modifier.height(16.dp))

            PrivacyPolicyAndTC()

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun LoginWithGoogleExplanation() {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(24.dp))

        IvyIcon(
            icon = R.drawable.ic_secure,
            tint = Green
        )

        Spacer(Modifier.width(4.dp))

        Column {
            Text(
                text = stringResource(R.string.sync_data_ivy_cloud),
                style = UI.typo.c.style(
                    color = Green,
                    fontWeight = FontWeight.ExtraBold
                )
            )

            Spacer(Modifier.height(2.dp))

            Text(
                text = stringResource(R.string.data_integrity_protection_warning),
                style = UI.typo.c.style(
                    color = UI.colors.pureInverse,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

@Composable
private fun LocalAccountExplanation() {
    Text(
        modifier = Modifier.padding(start = 32.dp),
        text = "ENTER WITH OFFLINE ACCOUNT",
        style = UI.typo.c.style(
            color = Gray,
            fontWeight = FontWeight.ExtraBold
        )
    )

    Spacer(Modifier.height(4.dp))

    Text(
        modifier = Modifier.padding(start = 32.dp, end = 32.dp),
        text = "Your data will be saved only locally on your phone. " +
                "You risk losing your data if you uninstall the app or change your device. " +
                "To prevent data loss, we recommend exporting backup from settings regularly.",
        style = UI.typo.c.style(
            color = Gray,
            fontWeight = FontWeight.Medium
        )
    )
}

@Composable
private fun PrivacyPolicyAndTC() {
    val terms = stringResource(R.string.terms_conditions)
    val privacy = stringResource(R.string.privacy_policy)
    val text = stringResource(R.string.by_signing_in, terms, privacy)

    val tcStart = text.indexOf(terms)
    val tcEnd = tcStart + terms.length

    val privacyStart = text.indexOf(privacy)
    val privacyEnd = privacyStart + privacy.length

    val annotatedString = buildAnnotatedString {
        append(text)

        addStringAnnotation(
            tag = "URL",
            annotation = Constants.URL_TC,
            start = tcStart,
            end = tcEnd
        )

        addStringAnnotation(
            tag = "URL",
            annotation = Constants.URL_PRIVACY_POLICY,
            start = privacyStart,
            end = privacyEnd
        )

        addStyle(
            style = SpanStyle(
                color = Green,
                textDecoration = TextDecoration.Underline
            ),
            start = tcStart,
            end = tcEnd
        )

        addStyle(
            style = SpanStyle(
                color = Green,
                textDecoration = TextDecoration.Underline
            ),
            start = privacyStart,
            end = privacyEnd
        )
    }

    val uriHandler = LocalUriHandler.current
    ClickableText(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        text = annotatedString,
        style = UI.typo.c.style(
            color = UI.colors.pureInverse,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        ),
        onClick = {
            annotatedString
                .getStringAnnotations("URL", it, it)
                .forEach { stringAnnotation ->
                    uriHandler.openUri(stringAnnotation.item)
                }
        }
    )
}

@Composable
private fun LoginButton(
    @DrawableRes icon: Int,
    text: String,
    textColor: Color,
    backgroundGradient: Gradient,
    hasShadow: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .fillMaxWidth()
            .thenIf(hasShadow) {
                drawColoredShadow(backgroundGradient.startColor)
            }
            .clip(UI.shapes.r4)
            .background(backgroundGradient.asHorizontalBrush(), UI.shapes.r4)
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(20.dp))

        IvyIcon(
            icon = icon,
            tint = textColor
        )

        Spacer(Modifier.width(16.dp))

        Text(
            modifier = Modifier.padding(vertical = 20.dp),
            text = text,
            style = UI.typo.b2.style(
                color = textColor,
                fontWeight = FontWeight.ExtraBold
            )
        )

        Spacer(Modifier.width(20.dp))
    }
}

@Preview
@Composable
private fun Preview() {
    IvyWalletPreview {
        OnboardingSplashLogin(
            onboardingState = OnboardingState.SPLASH,
            onSkip = {}
        )
    }
}
