package com.ivy.transaction.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ivy.core.domain.pure.format.ValueUi
import com.ivy.core.domain.pure.format.dummyValueUi
import com.ivy.design.l0_system.UI
import com.ivy.design.l3_ivyComponents.Feeling
import com.ivy.design.l3_ivyComponents.Visibility
import com.ivy.design.l3_ivyComponents.button.ButtonSize
import com.ivy.design.l3_ivyComponents.button.IvyButton
import com.ivy.design.util.ComponentPreview
import com.ivy.resources.R

@Composable
internal fun FeeComponent(
    fee: ValueUi,
    validFee: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    if (!validFee) {
        IvyButton(
            modifier = modifier,
            size = ButtonSize.Small,
            visibility = Visibility.Medium,
            feeling = Feeling.Negative,
            text = "Add fee",
            icon = R.drawable.ic_custom_bills_s,
            onClick = onClick
        )
    } else {
        IvyButton(
            modifier = modifier,
            size = ButtonSize.Small,
            visibility = Visibility.Medium,
            feeling = Feeling.Negative,
            text = "Fee ${fee.amount} ${fee.currency}",
            typo = UI.typoSecond.b2,
            icon = R.drawable.ic_custom_bills_s,
            onClick = onClick
        )
    }
}


// region Preview
@Preview
@Composable
private fun Preview_NoFee() {
    ComponentPreview {
        FeeComponent(
            fee = dummyValueUi(),
            validFee = false,
            onClick = {}
        )
    }
}

@Preview
@Composable
private fun Preview_Fee() {
    ComponentPreview {
        FeeComponent(
            fee = dummyValueUi(amount = "2"),
            validFee = true,
            onClick = {}
        )
    }
}
// endregion