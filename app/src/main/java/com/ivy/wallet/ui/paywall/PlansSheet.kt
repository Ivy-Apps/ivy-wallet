package com.ivy.wallet.ui.paywall

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.android.billingclient.api.SkuDetails
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.wallet.R
import com.ivy.wallet.android.billing.Plan
import com.ivy.wallet.android.billing.PlanType
import com.ivy.wallet.ui.IvyWalletPreview
import com.ivy.wallet.ui.theme.GradientGreen
import com.ivy.wallet.ui.theme.GradientIvy
import com.ivy.wallet.ui.theme.Gray
import com.ivy.wallet.ui.theme.Green
import com.ivy.wallet.ui.theme.White
import com.ivy.wallet.ui.theme.components.ActionsRow
import com.ivy.wallet.ui.theme.components.CloseButton
import com.ivy.wallet.ui.theme.components.IvyButton
import com.ivy.wallet.ui.theme.gradientCutBackgroundTop
import com.ivy.wallet.ui.theme.modal.AddModalBackHandling
import com.ivy.wallet.ui.theme.modal.ModalTitle
import com.ivy.wallet.ui.theme.pureBlur
import com.ivy.wallet.utils.consumeClicks
import com.ivy.wallet.utils.drawColoredShadow
import com.ivy.wallet.utils.springBounce
import com.ivy.wallet.utils.thenIf
import com.ivy.wallet.utils.toUpperCaseLocal
import java.util.UUID
import kotlin.math.roundToInt

@Composable
fun BoxWithConstraintsScope.PlansSheet(
    plans: List<Plan>,
    purchasedSkus: List<String>,

    onPlansSheetHeightChanged: (Int) -> Unit = {},

    onPlanSelected: (Plan?) -> Unit,
    onBuy: (Plan) -> Unit
) {
    var selectedPlan: Plan? by remember { mutableStateOf(null) }

    val percentExpanded by animateFloatAsState(
        targetValue = if (selectedPlan != null) 1f else 0f,
        animationSpec = springBounce()
    )

    if (percentExpanded > 0.01f) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(100f)
                .background(pureBlur())
                .alpha(percentExpanded)
                .clickable {
                    selectedPlan = null
                }
        ) {
        }
    }

    Column(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .zIndex(200f)
            .fillMaxWidth()
            .padding(top = 24.dp)
            .onSizeChanged {
                onPlansSheetHeightChanged(it.height)
            }
            .drawColoredShadow(
                color = UI.colors.mediumInverse,
                alpha = if (UI.colors.isLight) 0.3f else 0.2f,
                borderRadius = 24.dp,
                shadowRadius = 24.dp
            )
            .background(UI.colors.pure, UI.shapes.r2Top)
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
            .consumeClicks()
    ) {
        Spacer(Modifier.height(32.dp))

        ModalTitle(text = "Choose your plan")

        Spacer(Modifier.height(4.dp))

        Text(
            modifier = Modifier.padding(start = 32.dp, end = 32.dp),
            text = "All plans include a one-time free trial period so you can try Ivy Premium for free.",
            style = UI.typo.c.style(
                fontWeight = FontWeight.Bold,
                color = Gray
            )
        )

        Spacer(Modifier.height(24.dp))

        Plans(
            plans = plans,
            purchasedSkus = purchasedSkus,
            selectedPlan = selectedPlan,
            onSetSelectedPlan = {
                onPlanSelected(it)
                selectedPlan = it
            }
        )

        Spacer(Modifier.height(32.dp))

        if (percentExpanded > 0.01f) {
            Column(
                modifier = Modifier
                    .layout { measurable, constraints ->
                        val placealbe = measurable.measure(constraints)

                        val height = placealbe.height * percentExpanded

                        layout(placealbe.width, height.roundToInt()) {
                            placealbe.placeRelative(
                                x = 0,
                                y = -(placealbe.height * (1f - percentExpanded)).roundToInt()
                            )
                        }
                    }
                    .alpha(percentExpanded)
            ) {
                LongNoticeText(
                    plan = selectedPlan
                )

                Spacer(Modifier.height(64.dp))

                if (!purchasedSkus.contains(selectedPlan?.sku ?: "")) {
                    IvyButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        wrapContentMode = false,
                        text = if (selectedPlan?.type == PlanType.LIFETIME) {
                            "GET IT NOW"
                        } else {
                            "Try ${selectedPlan?.freePeriod()}".toUpperCaseLocal()
                        },
                        textStyle = UI.typo.nB2.style(
                            color = White,
                            fontWeight = FontWeight.Bold
                        ),
                        iconStart = R.drawable.ic_custom_crown_s,
                        padding = 16.dp,
                        iconEdgePadding = 24.dp,
                    ) {
                        selectedPlan?.let {
                            onBuy(it)
                        }
                    }

                    Spacer(Modifier.height(24.dp))
                }
            }
        }

        val modalId = remember { UUID.randomUUID() }
        AddModalBackHandling(
            modalId = modalId,
            visible = selectedPlan != null
        ) {
            selectedPlan = null
        }
    }
}

@Composable
private fun LongNoticeText(
    plan: Plan?
) {
    Text(
        modifier = Modifier
            .padding(horizontal = 32.dp),
        text = "Notice",
        style = UI.typo.nB2.style(
            fontWeight = FontWeight.ExtraBold
        )
    )

    Spacer(Modifier.height(8.dp))

    val longText = if (plan?.type == PlanType.LIFETIME) {
        // Lifetime plan
        "This limited lifetime offer gives you unlimited access to our app.\n\n" +
            "You'll be billed ${plan.price} once" +
            " and you'll receive unlimited lifetime access to Ivy Wallet Premium." +
            " By subscribing you agree to our Terms & Conditions and Privacy Policy." +
            "\n\n*lifetime refers to the product's lifetime but not your lifetime"
    } else {
        // Subscription
        val part1 = "This subscription gives you unlimited access to our app.\n\n"
        val part2 = "You'll be billed ${plan?.price} for a ${
            when (plan?.type) {
                PlanType.MONTHLY -> "1-month"
                PlanType.SIX_MONTH -> "6-months"
                PlanType.YEARLY -> "12-months"
                else -> "N months"
            }
        } subscription that'll renew automatically at ${plan?.price}, billed ${
            when (plan?.type) {
                PlanType.MONTHLY -> "monthly."
                PlanType.SIX_MONTH -> "every 6 months."
                PlanType.YEARLY -> "yearly."
                else -> "."
            }
        } You can cancel it anytime from the Google PlayStore. By subscribing you agree to our Terms & Conditions and Privacy Policy."

        part1 + part2
    }

    Text(
        modifier = Modifier
            .padding(horizontal = 32.dp),
        text = longText,
        style = UI.typo.nB2.style(
            fontWeight = FontWeight.Medium
        )
    )
}

@Composable
private fun Plans(
    plans: List<Plan>,
    purchasedSkus: List<String>,
    selectedPlan: Plan?,

    onSetSelectedPlan: (Plan?) -> Unit,
) {
    Column(
        modifier = Modifier,
    ) {
        val monthly = plans.find { it.type == PlanType.MONTHLY }
        if (monthly != null) {
            PlanCard(
                plan = monthly,
                purchased = purchasedSkus.contains(monthly.sku),
                monthlyPlan = monthly,
                selectedPlan = selectedPlan
            ) {
                onSetSelectedPlan(monthly)
            }
        }

        val yearly = plans.find { it.type == PlanType.YEARLY }
        if (yearly != null) {
            Spacer(Modifier.height(12.dp))

            PlanCard(
                plan = yearly,
                purchased = purchasedSkus.contains(yearly.sku),
                monthlyPlan = monthly,
                selectedPlan = selectedPlan
            ) {
                onSetSelectedPlan(yearly)
            }
        }

        val lifetime = plans.find { it.type == PlanType.LIFETIME }
        if (lifetime != null) {
            Spacer(Modifier.height(12.dp))

            PlanCard(
                plan = lifetime,
                purchased = purchasedSkus.contains(lifetime.sku),
                monthlyPlan = monthly,
                selectedPlan = selectedPlan
            ) {
                onSetSelectedPlan(lifetime)
            }
        }
    }
}

@Composable
private fun ColumnScope.PlanCard(
    plan: Plan,
    purchased: Boolean,
    selectedPlan: Plan?,

    monthlyPlan: Plan?,

    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 56.dp)
            .padding(horizontal = 16.dp)
            .thenIf(selectedPlan != null && selectedPlan != plan) {
                padding(horizontal = 8.dp)
            }
            .thenIf(selectedPlan == plan) {
                drawColoredShadow(Green)
            }
            .clip(UI.shapes.r4)
            .thenIf(!purchased && selectedPlan != plan) {
                border(2.dp, UI.colors.medium, UI.shapes.r4)
            }
            .thenIf(selectedPlan == plan) {
                background(GradientGreen.asHorizontalBrush(), UI.shapes.r4)
            }
            .thenIf(purchased) {
                background(GradientIvy.asHorizontalBrush(), UI.shapes.r4)
            }
            .clickable {
                onClick()
            }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(24.dp))

        val textColor = if (purchased || selectedPlan == plan) {
            White
        } else {
            UI.colors.pureInverse
        }

        Column {
            Text(
                text = when (plan.type) {
                    PlanType.MONTHLY -> "Monthly"
                    PlanType.SIX_MONTH -> "6 months"
                    PlanType.YEARLY -> "Yearly"
                    PlanType.LIFETIME -> "Lifetime"
                },
                style = UI.typo.b2.style(
                    color = textColor,
                    fontWeight = FontWeight.ExtraBold
                )
            )

            if (purchased) {
                Spacer(Modifier.height(4.dp))

                Text(
                    text = "Active",
                    style = UI.typo.c.style(
                        color = textColor
                    )
                )
            }
        }

        Spacer(Modifier.width(16.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 24.dp)
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = when (plan.type) {
                    PlanType.MONTHLY -> "${plan.price}/month"
                    PlanType.SIX_MONTH -> "${plan.price}/6m"
                    PlanType.YEARLY -> "${plan.price}/year"
                    PlanType.LIFETIME -> plan.price
                },
                style = UI.typo.nB2.style(
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    textAlign = TextAlign.End
                )
            )

            if (plan.type == PlanType.YEARLY && monthlyPlan != null) {
                val monthlyPrice = monthlyPlan.parsePrice().takeIf { it != null && it.amount > 0 }
                val yearlyPrice = plan.parsePrice()

                val savePercentage = if (monthlyPrice != null && yearlyPrice != null) {
                    (1 - (yearlyPrice.amount / (monthlyPrice.amount * 12)))
                        .times(100).roundToInt()
                } else {
                    null
                }

                if (savePercentage != null) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Save $savePercentage%",
                        style = UI.typo.nC.style(
                            fontWeight = FontWeight.ExtraBold,
                            color = if (selectedPlan == plan) White else Green,
                            textAlign = TextAlign.End
                        )
                    )
                }
            }

            if (plan.type == PlanType.LIFETIME) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Pay once",
                    style = UI.typo.c.style(
                        fontWeight = FontWeight.ExtraBold,
                        color = textColor,
                        textAlign = TextAlign.End
                    )
                )
            }
        }
    }
}

@Composable
private fun BoxWithConstraintsScope.BottomBar(
    Action: @Composable () -> Unit,

    setBottomBarHeight: (Int) -> Unit,
    onClose: () -> Unit,
) {
    ActionsRow(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .onSizeChanged {
                setBottomBarHeight(it.height)
            }
            .gradientCutBackgroundTop()
            .padding(bottom = 12.dp)
            .navigationBarsPadding()
    ) {
        Spacer(Modifier.width(24.dp))

        CloseButton {
            onClose()
        }

        Spacer(Modifier.weight(1f))

        Action()

        Spacer(Modifier.width(24.dp))
    }
}

@Preview
@Composable
private fun Preview() {
    IvyWalletPreview {
        PlansSheet(
            plans = listOf(
                Plan(
                    sku = "sku1",
                    type = PlanType.MONTHLY,
                    price = "BGN 1.99",
                    skuDetails = SkuDetails("{\"productId\":\"test\", \"type\":\"MONTHLY\"}")
                ),
                Plan(
                    sku = "sku2",
                    type = PlanType.YEARLY,
                    price = "BGN 9.99",
                    skuDetails = SkuDetails("{\"productId\":\"test\", \"type\":\"MONTHLY\"}")
                ),
                Plan(
                    sku = "sku3",
                    type = PlanType.LIFETIME,
                    price = "BGN 13.99",
                    skuDetails = SkuDetails("{\"productId\":\"test\", \"type\":\"MONTHLY\"}")
                ),
            ),
            purchasedSkus = listOf("sku3"),
            onPlanSelected = {},
            onBuy = {}
        )
    }
}

@Preview
@Composable
private fun Preview_Shitty() {
    IvyWalletPreview {
        PlansSheet(
            plans = listOf(
                Plan(
                    sku = "sku1",
                    type = PlanType.MONTHLY,
                    price = "HUF  _1,799.00",
                    skuDetails = SkuDetails("{\"productId\":\"test\", \"type\":\"MONTHLY\"}")
                ),
                Plan(
                    sku = "sku3",
                    type = PlanType.YEARLY,
                    price = "HUF _125,999.00",
                    skuDetails = SkuDetails("{\"productId\":\"test\", \"type\":\"MONTHLY\"}")
                ),
                Plan(
                    sku = "sku3",
                    type = PlanType.LIFETIME,
                    price = "HUF _1300,999.00",
                    skuDetails = SkuDetails("{\"productId\":\"test\", \"type\":\"MONTHLY\"}")
                )
            ),
            purchasedSkus = listOf("sdgf"),
            onPlanSelected = {},
            onBuy = {}
        )
    }
}
