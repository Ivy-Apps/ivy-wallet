package com.ivy.design.l2_components

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.util.ComponentPreview

// region Text typography
@Composable
fun String.H1(
    modifier: Modifier = Modifier,
    fontWeight: FontWeight = FontWeight.Bold,
    color: Color = UI.colorsInverted.pure,
    textAlign: TextAlign = TextAlign.Start,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    maxLines: Int = Int.MAX_VALUE,
) {
    Text(
        typo = UI.typo.h1,
        modifier = modifier,
        fontWeight = fontWeight,
        color = color,
        textAlign = textAlign,
        overflow = overflow,
        maxLines = maxLines
    )
}

@Composable
fun String.H2(
    modifier: Modifier = Modifier,
    fontWeight: FontWeight = FontWeight.Bold,
    color: Color = UI.colorsInverted.pure,
    textAlign: TextAlign = TextAlign.Start,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    maxLines: Int = Int.MAX_VALUE,
) {
    Text(
        typo = UI.typo.h2,
        modifier = modifier,
        fontWeight = fontWeight,
        color = color,
        textAlign = textAlign,
        overflow = overflow,
        maxLines = maxLines
    )
}

@Composable
fun String.B1(
    modifier: Modifier = Modifier,
    fontWeight: FontWeight = FontWeight.Bold,
    color: Color = UI.colorsInverted.pure,
    textAlign: TextAlign = TextAlign.Start,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    maxLines: Int = Int.MAX_VALUE,
) {
    Text(
        typo = UI.typo.b1,
        modifier = modifier,
        fontWeight = fontWeight,
        color = color,
        textAlign = textAlign,
        overflow = overflow,
        maxLines = maxLines
    )
}

@Composable
fun String.B2(
    modifier: Modifier = Modifier,
    fontWeight: FontWeight = FontWeight.Bold,
    color: Color = UI.colorsInverted.pure,
    textAlign: TextAlign = TextAlign.Start,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    maxLines: Int = Int.MAX_VALUE,
) {
    Text(
        typo = UI.typo.b2,
        modifier = modifier,
        fontWeight = fontWeight,
        color = color,
        textAlign = textAlign,
        overflow = overflow,
        maxLines = maxLines
    )
}

@Composable
fun String.C(
    modifier: Modifier = Modifier,
    fontWeight: FontWeight = FontWeight.Bold,
    color: Color = UI.colorsInverted.pure,
    textAlign: TextAlign = TextAlign.Start,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    maxLines: Int = Int.MAX_VALUE,
) {
    Text(
        typo = UI.typo.c,
        modifier = modifier,
        fontWeight = fontWeight,
        color = color,
        textAlign = textAlign,
        overflow = overflow,
        maxLines = maxLines
    )
}
// endregion

// region Secondary typography
@Composable
fun String.H1Second(
    modifier: Modifier = Modifier,
    fontWeight: FontWeight = FontWeight.Bold,
    color: Color = UI.colorsInverted.pure,
    textAlign: TextAlign = TextAlign.Start,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    maxLines: Int = Int.MAX_VALUE,
) {
    Text(
        typo = UI.typoSecond.h1,
        modifier = modifier,
        fontWeight = fontWeight,
        color = color,
        textAlign = textAlign,
        overflow = overflow,
        maxLines = maxLines
    )
}

@Composable
fun String.H2Second(
    modifier: Modifier = Modifier,
    fontWeight: FontWeight = FontWeight.Bold,
    color: Color = UI.colorsInverted.pure,
    textAlign: TextAlign = TextAlign.Start,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    maxLines: Int = Int.MAX_VALUE,
) {
    Text(
        typo = UI.typoSecond.h2,
        modifier = modifier,
        fontWeight = fontWeight,
        color = color,
        textAlign = textAlign,
        overflow = overflow,
        maxLines = maxLines
    )
}

@Composable
fun String.B1Second(
    modifier: Modifier = Modifier,
    fontWeight: FontWeight = FontWeight.Bold,
    color: Color = UI.colorsInverted.pure,
    textAlign: TextAlign = TextAlign.Start,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    maxLines: Int = Int.MAX_VALUE,
) {
    Text(
        typo = UI.typoSecond.b1,
        modifier = modifier,
        fontWeight = fontWeight,
        color = color,
        textAlign = textAlign,
        overflow = overflow,
        maxLines = maxLines
    )
}

@Composable
fun String.B2Second(
    modifier: Modifier = Modifier,
    fontWeight: FontWeight = FontWeight.Bold,
    color: Color = UI.colorsInverted.pure,
    textAlign: TextAlign = TextAlign.Start,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    maxLines: Int = Int.MAX_VALUE,
) {
    Text(
        typo = UI.typoSecond.b2,
        modifier = modifier,
        fontWeight = fontWeight,
        color = color,
        textAlign = textAlign,
        overflow = overflow,
        maxLines = maxLines
    )
}

@Composable
fun String.CSecond(
    modifier: Modifier = Modifier,
    fontWeight: FontWeight = FontWeight.Bold,
    color: Color = UI.colorsInverted.pure,
    textAlign: TextAlign = TextAlign.Start,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    maxLines: Int = Int.MAX_VALUE,
) {
    Text(
        typo = UI.typoSecond.c,
        modifier = modifier,
        fontWeight = fontWeight,
        color = color,
        textAlign = textAlign,
        overflow = overflow,
        maxLines = maxLines
    )
}
// endregion

@Composable
fun String.Text(
    typo: TextStyle,
    modifier: Modifier = Modifier,
    fontWeight: FontWeight = FontWeight.Bold,
    color: Color = UI.colorsInverted.pure,
    textAlign: TextAlign = TextAlign.Start,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    maxLines: Int = Int.MAX_VALUE,
) {
    Text(
        modifier = modifier,
        text = this,
        style = typo.style(
            fontWeight = fontWeight,
            color = color,
            textAlign = textAlign,
        ),
        overflow = overflow,
        maxLines = maxLines,
    )
}

@Preview
@Composable
private fun Preview() {
    ComponentPreview {
        Column {
            "Heading 1".H1()
            "Heading 2".H2()
            "Body 1".B1()
            "Body 2".B2()
            "Caption".C()

            SpacerVer(height = 8.dp)

            "1".H1Second()
            "2".H2Second()
            "3".B1Second()
            "4".B2Second()
            "5".CSecond()
        }
    }
}