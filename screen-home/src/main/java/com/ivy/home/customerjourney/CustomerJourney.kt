package com.ivy.home.customerjourney

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.legacy.ivyWalletCtx
import com.ivy.legacy.rootScreen
import com.ivy.legacy.utils.drawColoredShadow
import com.ivy.navigation.IvyPreview
import com.ivy.navigation.navigation
import com.ivy.resources.R
import com.ivy.wallet.ui.theme.Gradient
import com.ivy.wallet.ui.theme.components.IvyButton
import com.ivy.wallet.ui.theme.components.IvyIcon
import com.ivy.wallet.ui.theme.dynamicContrast
import com.ivy.wallet.ui.theme.findContrastTextColor
import kotlinx.collections.immutable.ImmutableList

@Composable
fun CustomerJourney(
    customerJourneyCards: ImmutableList<CustomerJourneyCardModel>,
    modifier: Modifier = Modifier,
    onDismiss: (CustomerJourneyCardModel) -> Unit
) {
    val ivyContext = ivyWalletCtx()
    val nav = navigation()
    val rootScreen = rootScreen()

    if (customerJourneyCards.isNotEmpty()) {
        Spacer(Modifier.height(12.dp))
    }

    for (card in customerJourneyCards) {
        Spacer(Modifier.height(12.dp))

        CustomerJourneyCard(
            modifier = modifier,
            cardData = card,
            onDismiss = {
                onDismiss(card)
            }
        ) {
            card.onAction(nav, ivyContext, rootScreen)
        }
    }
}

@Composable
fun CustomerJourneyCard(
    cardData: CustomerJourneyCardModel,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    onCTA: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .drawColoredShadow(cardData.background.startColor)
            .background(cardData.background.asHorizontalBrush(), UI.shapes.r3)
            .clip(UI.shapes.r3)
            .clickable {
                onCTA()
            }
    ) {
        Spacer(Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 24.dp, end = 16.dp),
                text = cardData.title,
                style = UI.typo.b1.style(
                    fontWeight = FontWeight.ExtraBold,
                    color = findContrastTextColor(cardData.background.startColor)
                )
            )

            if (cardData.hasDismiss) {
                IvyIcon(
                    modifier = Modifier
                        .clickable {
                            onDismiss()
                        }
                        .padding(8.dp), // enlarge click area
                    icon = R.drawable.ic_dismiss,
                    tint = cardData.background.startColor.dynamicContrast(),
                    contentDescription = "prompt_dismiss",
                )

                Spacer(Modifier.width(20.dp))
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 32.dp),
            text = cardData.description,
            style = UI.typo.b2.style(
                fontWeight = FontWeight.Medium,
                color = findContrastTextColor(cardData.background.startColor)
            )
        )

        Spacer(Modifier.height(32.dp))

        if (cardData.cta != null) {
            IvyButton(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(end = 20.dp)
                    .testTag("cta_prompt_${cardData.id}"),
                text = cardData.cta,
                shadowAlpha = 0f,
                iconStart = cardData.ctaIcon,
                iconTint = cardData.background.startColor,
                textStyle = UI.typo.b2.style(
                    color = cardData.background.startColor,
                    fontWeight = FontWeight.Bold
                ),
                padding = 8.dp,
                backgroundGradient = Gradient.solid(findContrastTextColor(cardData.background.startColor))
            ) {
                onCTA()
            }

            Spacer(Modifier.height(20.dp))
        }
    }
}

@Preview
@Composable
private fun PreviewCard() {
    IvyPreview {
        CustomerJourneyCard(
            cardData = CustomerJourneyCardsProvider.adjustBalanceCard(),
            onCTA = { },
            onDismiss = {}
        )
    }
}
