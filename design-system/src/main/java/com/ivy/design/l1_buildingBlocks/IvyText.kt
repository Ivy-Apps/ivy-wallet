package com.ivy.design.l1_buildingBlocks

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import com.ivy.design.l1_buildingBlocks.data.IvyPadding
import com.ivy.design.util.paddingIvy
import com.ivy.design.util.thenIf

@Deprecated("don't use")
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
                paddingIvy(ivyPadding = padding!!)
            }
            .then(modifier),
        text = text,
        style = typo,
    )
}