package com.ivy.core.ui.account.create.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.design.l0_system.UI
import com.ivy.design.l1_buildingBlocks.B2
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l2_components.Switch
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.l2_components.modal.Modal
import com.ivy.design.l2_components.modal.components.Body
import com.ivy.design.l2_components.modal.components.Positive
import com.ivy.design.l2_components.modal.components.Title
import com.ivy.design.l2_components.modal.rememberIvyModal
import com.ivy.design.l3_ivyComponents.MoreInfoButton
import com.ivy.design.util.ComponentPreview
import com.ivy.design.util.IvyPreview

@Composable
internal fun ExcludeAccount(
    excluded: Boolean,
    modifier: Modifier = Modifier,
    onMoreInfo: () -> Unit,
    onExcludedChange: (excluded: Boolean) -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(UI.shapes.fullyRounded)
            .clickable { onExcludedChange(!excluded) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Switch(
            enabled = excluded,
            enabledColor = UI.colors.red,
            onEnabledChange = onExcludedChange
        )
        B2(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp, end = 4.dp),
            text = "Exclude account",
            fontWeight = FontWeight.ExtraBold,
            color = UI.colorsInverted.pure,
        )
        MoreInfoButton(onClick = onMoreInfo)
    }
}

@Composable
internal fun BoxScope.ExcludedAccInfoModal(
    modal: IvyModal,
    level: Int = 1,
) {
    Modal(
        modal = modal,
        level = level,
        actions = {
            Positive(text = "Got it") {
                modal.hide()
            }
        }
    ) {
        Title(text = "Excluded accounts")
        SpacerVer(height = 24.dp)
        Body(
            text = "Excluded accounts don't count to your balance" +
                    " that you see on the \"Home\" screen. However, they're calculated" +
                    " in your expenses and you can still add transactions in them."
        )
        SpacerVer(height = 48.dp)
    }
}


// region Preview
@Preview
@Composable
private fun Preview() {
    ComponentPreview {
        ExcludeAccount(excluded = false, onMoreInfo = { }, onExcludedChange = {})
    }
}

@Preview
@Composable
private fun Preview_InfoModal() {
    IvyPreview {
        val modal = rememberIvyModal()
        modal.show()
        ExcludedAccInfoModal(modal = modal)
    }
}
// endregion