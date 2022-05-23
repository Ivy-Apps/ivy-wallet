package com.ivy.wallet.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.frp.view.navigation.navigation
import com.ivy.wallet.R
import com.ivy.wallet.domain.deprecated.logic.CustomerJourneyLogic
import com.ivy.wallet.domain.deprecated.logic.model.CustomerJourneyCardData
import com.ivy.wallet.ui.IvyWalletComponentPreview
import com.ivy.wallet.ui.RootActivity
import com.ivy.wallet.ui.ivyWalletCtx
import com.ivy.wallet.ui.theme.Gradient
import com.ivy.wallet.ui.theme.components.IvyButton
import com.ivy.wallet.ui.theme.components.IvyIcon
import com.ivy.wallet.ui.theme.dynamicContrast
import com.ivy.wallet.ui.theme.findContrastTextColor
import com.ivy.wallet.utils.drawColoredShadow

@Composable
fun CustomerJourney(
    customerJourneyCards: List<CustomerJourneyCardData>,
    onDismiss: (CustomerJourneyCardData) -> Unit
) {
    val ivyContext = ivyWalletCtx()
    val nav = navigation()
    val ivyActivity = LocalContext.current as RootActivity

    if (customerJourneyCards.isNotEmpty()) {
        Spacer(Modifier.height(12.dp))
    }

    for (card in customerJourneyCards) {
        Spacer(Modifier.height(12.dp))

        CustomerJourneyCard(
            cardData = card,
            onDismiss = {
                onDismiss(card)
            }
        ) {
            card.onAction(nav, ivyContext, ivyActivity)
        }
    }
}

@Composable
fun CustomerJourneyCard(
    cardData: CustomerJourneyCardData,

    onDismiss: () -> Unit,
    onCTA: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .drawColoredShadow(cardData.backgroundColor)
            .background(cardData.backgroundColor, UI.shapes.r3)
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
                    color = findContrastTextColor(cardData.backgroundColor)
                )
            )

            if (cardData.hasDismiss) {
                IvyIcon(
                    modifier = Modifier
                        .clickable {
                            onDismiss()
                        }
                        .padding(8.dp), //enlarge click area
                    icon = R.drawable.ic_dismiss,
                    tint = cardData.backgroundColor.dynamicContrast(),
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
                color = findContrastTextColor(cardData.backgroundColor)
            )
        )

        Spacer(Modifier.height(32.dp))

        IvyButton(
            modifier = Modifier
                .align(Alignment.End)
                .padding(end = 20.dp)
                .testTag("cta_prompt_${cardData.id}"),
            text = cardData.cta,
            shadowAlpha = 0f,
            iconStart = cardData.ctaIcon,
            iconTint = cardData.backgroundColor,
            textStyle = UI.typo.b2.style(
                color = cardData.backgroundColor,
                fontWeight = FontWeight.Bold
            ),
            padding = 8.dp,
            backgroundGradient = Gradient.solid(findContrastTextColor(cardData.backgroundColor))
        ) {
            onCTA()
        }

        Spacer(Modifier.height(20.dp))
    }
}

@Preview
@Composable
private fun PreviewCard() {
    IvyWalletComponentPreview {
        CustomerJourneyCard(
            cardData = CustomerJourneyLogic.adjustBalanceCard(),
            onCTA = { },
            onDismiss = {}
        )
    }
}