package com.ivy.wallet.compose.component.category

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import com.ivy.wallet.compose.IvyComposeTestRule
import com.ivy.wallet.compose.component.picker.ColorPicker
import com.ivy.wallet.compose.component.picker.IconPicker
import com.ivy.wallet.compose.component.picker.IvyColorPicker
import com.ivy.wallet.compose.component.picker.IvyIconPicker

class CategoryModal(
    private val composeTestRule: IvyComposeTestRule
) : ColorPicker<CategoryModal>, IconPicker<CategoryModal> {
    fun enterTitle(
        title: String
    ): CategoryModal {
        composeTestRule.onNodeWithTag("base_input")
            .performTextReplacement(title)
        return this
    }

    fun <N> clickSave(next: N): N {
        composeTestRule
            .onNode(hasText("Save"))
            .performClick()
        return next
    }

    fun <N> clickAdd(next: N): N {
        composeTestRule
            .onNode(hasText("Add"))
            .performClick()
        return next
    }

    override fun chooseColor(color: Color): CategoryModal {
        IvyColorPicker(composeTestRule).chooseColor(color)
        return this
    }

    override fun chooseIcon(icon: String): CategoryModal {
        IvyIconPicker(composeTestRule).chooseIcon(icon)
        return this
    }
}