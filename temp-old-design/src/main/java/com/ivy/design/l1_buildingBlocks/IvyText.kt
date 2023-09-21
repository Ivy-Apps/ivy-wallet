package com.ivy.design.l1_buildingBlocks

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import com.ivy.design.l1_buildingBlocks.data.IvyPadding
import com.ivy.design.utils.ivyPadding
import com.ivy.design.utils.thenIf

@Deprecated("Old design system. Use `:ivy-design` and Material3")
@Composable
fun IvyText(
    modifier: Modifier = Modifier,
    text: String,
    typo: TextStyle,
    padding: IvyPadding? = null
) {
    Text(
        modifier = Modifier
            .thenIf(padding != null) {
                ivyPadding(ivyPadding = padding!!)
            }
            .then(modifier),
        text = text,
        style = typo,
    )
}
