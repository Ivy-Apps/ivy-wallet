package com.ivy.wallet.ui.onboarding.steps.archived

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.wallet.Constants
import com.ivy.wallet.R
import com.ivy.wallet.ui.IvyWalletPreview
import com.ivy.wallet.ui.RootActivity
import com.ivy.wallet.ui.ivyWalletCtx
import com.ivy.wallet.ui.theme.GradientGreen
import com.ivy.wallet.ui.theme.Gray
import com.ivy.wallet.ui.theme.Green
import com.ivy.wallet.ui.theme.White
import com.ivy.wallet.ui.theme.components.IvyIcon
import com.ivy.wallet.utils.drawColoredShadow
import com.ivy.wallet.utils.toDensityDp
import com.ivy.wallet.utils.toDensityPx
import timber.log.Timber

@Composable
fun OnboardingPrivacyTC(
    onAccepted: () -> Unit
) {
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
            modifier = Modifier.padding(start = 32.dp),
            text = stringResource(R.string.privacy_and_data_collection),
            style = UI.typo.h2.style(
                fontWeight = FontWeight.ExtraBold
            )
        )

        Spacer(Modifier.height(20.dp))

        URLsRow()

        Spacer(Modifier.height(44.dp))

        LongText()

        Spacer(Modifier.weight(1f))

        var tcAccepted by remember { mutableStateOf(false) }
        var privacyAccepted by remember { mutableStateOf(false) }

        SwipeToAgree(
            swipeToAgreeText = stringResource(R.string.swipe_to_agree_terms_conditions),
            agreedText = stringResource(R.string.agreed_terms_conditions)
        ) {
            tcAccepted = it
        }

        Spacer(Modifier.height(24.dp))

        SwipeToAgree(
            swipeToAgreeText = stringResource(R.string.swipe_to_agree_privacy_policy),
            agreedText = stringResource(R.string.agreed_privacy_policy)
        ) {
            privacyAccepted = it
        }

        if (tcAccepted && privacyAccepted) {
            onAccepted()
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun URLsRow() {
    Row {
        Spacer(Modifier.width(32.dp))

        TextLink(
            text = stringResource(R.string.terms_and_conditions),
            url = Constants.URL_TC
        )

        Spacer(Modifier.width(36.dp))

        TextLink(
            text = stringResource(R.string.privacy_policy),
            url = Constants.URL_PRIVACY_POLICY
        )

        Spacer(Modifier.width(32.dp))
    }
}

@Composable
private fun LongText() {
    Text(
        modifier = Modifier.padding(
            start = 32.dp,
            end = 48.dp
        ),
        text = stringResource(R.string.wallet_description),
        style = UI.typo.b2.style(
            fontWeight = FontWeight.Medium
        )
    )
}

@Composable
private fun TextLink(
    text: String,
    url: String,
) {
    val context = LocalContext.current
    val ivy1 = UI.colors.primary1

    Text(
        modifier = Modifier
            .drawBehind {
                drawRoundRect(
                    color = ivy1,
                    topLeft = Offset(
                        x = 0f,
                        y = this.size.height + 4.dp.toPx()
                    ),
                    size = this.size.copy(
                        height = 2.dp.toPx()
                    ),
                    cornerRadius = CornerRadius(x = 1.dp.toPx()),
                )
            }
            .clickable {
                (context as RootActivity).openUrlInBrowser(url)

            },
        text = text,
        style = UI.typo.b2.style(
            color = ivy1,
            fontWeight = FontWeight.Medium
        )
    )
}

@Composable
private fun SwipeToAgree(
    swipeToAgreeText: String,
    agreedText: String,
    onAgreed: (Boolean) -> Unit
) {
    val ivyContext = ivyWalletCtx()

    val maxOffsetX = ivyContext.screenWidth - 80.dp.toDensityPx() - 72.dp.toDensityPx()
    var offsetX by remember { mutableStateOf(0f) }

    val percentSwiped = offsetX / maxOffsetX
    val agreed = percentSwiped > 0.5f

    if (percentSwiped > 0.99f || percentSwiped < 0.01f) {
        onAgreed(agreed)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 32.dp
            )
            .border(
                width = 2.dp,
                color = if (agreed) Green else UI.colors.medium,
                shape = UI.shapes.r4
            )
            .padding(
                vertical = 8.dp
            ),
        contentAlignment = Alignment.CenterStart
    ) {
        if (agreed) {
            Text(
                modifier = Modifier
                    .padding(start = 32.dp) //24+8=32.dp
                    .width(164.dp),
                text = agreedText,
                style = UI.typo.c.style(
                    color = Green,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        if (!agreed) {
            Text(
                modifier = Modifier
                    .padding(start = 100.dp)
                    .width(164.dp),
                text = swipeToAgreeText,
                style = UI.typo.c.style(
                    color = Gray,
                    fontWeight = FontWeight.Medium
                )
            )
        }

        IvyIcon(
            modifier = Modifier
                .padding(start = 8.dp)
                .offset(x = offsetX.toDensityDp())
                .drawColoredShadow(color = Green)
                .background(GradientGreen.asHorizontalBrush(), UI.shapes.r4)
                .pointerInput(onAgreed) {
                    detectHorizontalDragGestures(
                        onDragCancel = {
                            if (percentSwiped < 0.9f) {
                                offsetX = 0f
                            }
                        },
                        onDragEnd = {
                            if (percentSwiped < 0.9f) {
                                offsetX = 0f
                            }
                        }
                    ) { _, dragAmount ->
                        val dragAmountScaled = dragAmount * 10
                        val newOffsetX = offsetX + dragAmountScaled
                        Timber.i("dragAmount=$dragAmount, offsetX=$offsetX, newOffsetX=$newOffsetX, maxOffset=$maxOffsetX")
                        offsetX = newOffsetX.coerceIn(
                            minimumValue = 0f,
                            maximumValue = maxOffsetX
                        )
                    }
                }
                .padding(horizontal = 20.dp, vertical = 8.dp),
            icon = if (agreed) R.drawable.ic_agreed else R.drawable.ic_swipe_horizontal,
            tint = White
        )
    }
}

@Preview
@Composable
private fun Preview() {
    IvyWalletPreview {
        OnboardingPrivacyTC(
            onAccepted = {

            }
        )
    }
}