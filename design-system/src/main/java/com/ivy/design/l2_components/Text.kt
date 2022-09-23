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
    color: Color = UI.colors.pureInverse,
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
    color: Color = UI.colors.pureInverse,
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
    color: Color = UI.colors.pureInverse,
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
    color: Color = UI.colors.pureInverse,
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
    color: Color = UI.colors.pureInverse,
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

// region Numbers typography
@Composable
fun String.NH1(
    modifier: Modifier = Modifier,
    fontWeight: FontWeight = FontWeight.Bold,
    color: Color = UI.colors.pureInverse,
    textAlign: TextAlign = TextAlign.Start,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    maxLines: Int = Int.MAX_VALUE,
) {
    Text(
        typo = UI.typo.nH1,
        modifier = modifier,
        fontWeight = fontWeight,
        color = color,
        textAlign = textAlign,
        overflow = overflow,
        maxLines = maxLines
    )
}

@Composable
fun String.NH2(
    modifier: Modifier = Modifier,
    fontWeight: FontWeight = FontWeight.Bold,
    color: Color = UI.colors.pureInverse,
    textAlign: TextAlign = TextAlign.Start,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    maxLines: Int = Int.MAX_VALUE,
) {
    Text(
        typo = UI.typo.nH2,
        modifier = modifier,
        fontWeight = fontWeight,
        color = color,
        textAlign = textAlign,
        overflow = overflow,
        maxLines = maxLines
    )
}

@Composable
fun String.NB1(
    modifier: Modifier = Modifier,
    fontWeight: FontWeight = FontWeight.Bold,
    color: Color = UI.colors.pureInverse,
    textAlign: TextAlign = TextAlign.Start,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    maxLines: Int = Int.MAX_VALUE,
) {
    Text(
        typo = UI.typo.nB1,
        modifier = modifier,
        fontWeight = fontWeight,
        color = color,
        textAlign = textAlign,
        overflow = overflow,
        maxLines = maxLines
    )
}

@Composable
fun String.NB2(
    modifier: Modifier = Modifier,
    fontWeight: FontWeight = FontWeight.Bold,
    color: Color = UI.colors.pureInverse,
    textAlign: TextAlign = TextAlign.Start,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    maxLines: Int = Int.MAX_VALUE,
) {
    Text(
        typo = UI.typo.nB2,
        modifier = modifier,
        fontWeight = fontWeight,
        color = color,
        textAlign = textAlign,
        overflow = overflow,
        maxLines = maxLines
    )
}

@Composable
fun String.NC(
    modifier: Modifier = Modifier,
    fontWeight: FontWeight = FontWeight.Bold,
    color: Color = UI.colors.pureInverse,
    textAlign: TextAlign = TextAlign.Start,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    maxLines: Int = Int.MAX_VALUE,
) {
    Text(
        typo = UI.typo.nC,
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
    color: Color = UI.colors.pureInverse,
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

            "1".NH1()
            "2".NH2()
            "3".NB1()
            "4".NB2()
            "5".NC()
        }
    }
}