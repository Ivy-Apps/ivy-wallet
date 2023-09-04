package com.ivy.design.l3_ivyComponents

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.colorAs
import com.ivy.design.utils.IvyComponentPreview

@Composable
fun ScreenTitleLarge(
    text: String,
    paddingStart: Dp = 0.dp,
    textColor: Color = UI.colors.primary
) {
    ScreenTitle(
        modifier = Modifier
            .padding(start = paddingStart),
        text = text,
        textStyle = UI.typo.h1.colorAs(textColor)
    )
}

@Composable
fun ScreenTitle(
    text: String,
    paddingStart: Dp = 0.dp,
    textColor: Color = UI.colors.primary
) {
    ScreenTitle(
        modifier = Modifier
            .padding(start = paddingStart),
        text = text,
        textStyle = UI.typo.h2.colorAs(textColor)
    )
}

@Composable
fun ScreenTitle(
    modifier: Modifier = Modifier,
    text: String,
    textStyle: TextStyle
) {
    Text(
        modifier = modifier,
        text = text,
        style = textStyle
    )
}

@Preview
@Composable
private fun Preview_Large() {
    IvyComponentPreview {
        ScreenTitleLarge(text = "Home")
    }
}

@Preview
@Composable
private fun Preview_Standard() {
    IvyComponentPreview {
        ScreenTitle(text = "Home")
    }
}
