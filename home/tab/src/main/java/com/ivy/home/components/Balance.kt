package com.ivy.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.core.domain.pure.format.ValueUi
import com.ivy.core.domain.pure.format.dummyValueUi
import com.ivy.design.l0_system.UI
import com.ivy.design.l1_buildingBlocks.SpacerHor
import com.ivy.design.l2_components.H1Second
import com.ivy.design.util.ComponentPreview

@Composable
internal fun Balance(
    balance: ValueUi,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clip(UI.shapes.rounded)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        H1Second(
            text = balance.currency,
            fontWeight = FontWeight.Normal,
        )
        SpacerHor(width = 8.dp)
        H1Second(
            text = balance.amount,
            modifier = Modifier.testTag("balance_amount"),
            fontWeight = FontWeight.Bold,
        )
    }
}


// region Preview
@Preview
@Composable
private fun Preview() {
    ComponentPreview {
        Balance(balance = dummyValueUi("15,300.87")) {

        }
    }
}
// endregion
