package com.ivy.wallet.compose.component.picker

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.ivy.wallet.compose.IvyComposeTestRule

class IvyColorPicker(
    private val composeTestRule: IvyComposeTestRule
) {
    fun chooseColor(color: Color) {
        composeTestRule.onNode(hasTestTag("color_item_${color.value}"))
            .performScrollTo()
            .performClick()
    }
}

interface ColorPicker<T> {
    fun chooseColor(color: Color): T
}