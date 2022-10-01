package com.ivy.reports

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.ivy.base.R
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.reports.ReportsEvent.SaveTemplate
import com.ivy.reports.extensions.LogCompositions
import com.ivy.reports.ui.ReportTemplateCard
import com.ivy.wallet.ui.theme.Gradient
import com.ivy.wallet.ui.theme.GradientGreen
import com.ivy.wallet.ui.theme.Transparent
import com.ivy.wallet.ui.theme.components.IvyButton
import com.ivy.wallet.ui.theme.modal.AddModalBackHandling
import java.util.*

@Composable
fun BoxWithConstraintsScope.ReportTemplate(
    visible: Boolean,
    onClose: () -> Unit,
    onEvent: (ReportsEvent) -> Unit
) {
    val modalId = remember { UUID.randomUUID() }

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(easing = LinearOutSlowInEasing)
        ),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(easing = LinearOutSlowInEasing)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(100f)
                .background(UI.colors.pure)
                .systemBarsPadding()
        ) {
            LazyColumn {
                item {
                    TemplateHeader(
                        modifier = Modifier
                            .padding(vertical = 24.dp),
                    )
                }
                item {
                    ReportTemplateCard()
                }
            }
            TemplateOptions(onSaveTemplate = {
                onEvent(SaveTemplate(visible = true))
            })
        }
    }

    AddBackHandlingSupport(
        id = modalId,
        visible = visible,
        action = onClose
    )
}

@Composable
private fun TemplateHeader(modifier: Modifier = Modifier) {
    LogCompositions(tag = TAG, msg = "Template Header")
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.padding(start = 32.dp),
            text = "Template",
            style = UI.typo.h2.style(
                fontWeight = FontWeight.ExtraBold
            )
        )

        Spacer(Modifier.width(24.dp))
    }
}

@Composable
fun BoxScope.TemplateOptions(onSaveTemplate: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(196.dp)
            .zIndex(200f)
            .background(Gradient(Transparent, UI.colors.pure).asVerticalBrush())
            .align(Alignment.BottomCenter),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Center
    ) {
        //Spacer(Modifier.width(24.dp))

        //CloseButton(onClick = onClose)

        // Spacer(Modifier.weight(1f))

        IvyButton(

            text = "Save Current Filter As Template",
            iconStart = R.drawable.ic_filter_xs,
            backgroundGradient = GradientGreen,
            padding = 10.dp,
        ) {
            onSaveTemplate()
        }

        //Spacer(Modifier.width(24.dp))
    }
}


@Composable
private fun AddBackHandlingSupport(id: UUID, visible: Boolean, action: () -> Unit) {
    AddModalBackHandling(
        modalId = id,
        visible = visible,
        action = action
    )
}