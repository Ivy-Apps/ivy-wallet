package com.ivy.design.l1_buildingBlocks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ivy.design.l0_system.UI
import com.ivy.design.utils.IvyComponentPreview
import com.ivy.design.utils.thenWhen
import com.ivy.ui.annotation.IvyPreviews

@Deprecated("Old design system. Use `:ivy-design` and Material3")
@Composable
fun DividerH(
    size: DividerSize = DividerSize.FillMax(
        padding = 16.dp
    ),
    width: Dp = 1.dp,
    color: Color = UI.colors.gray,
    shape: Shape = UI.shapes.rFull,
) {
    Spacer(
        modifier = Modifier
            .thenWhen {
                when (size) {
                    is DividerSize.FillMax -> {
                        fillMaxWidth()
                            .padding(horizontal = size.padding)
                    }

                    is DividerSize.Fixed -> {
                        this.width(size.size)
                    }
                }
            }
            .height(width)
            .background(color, shape)
    )
}

@Deprecated("Old design system. Use `:ivy-design` and Material3")
@Composable
fun DividerV(
    size: DividerSize = DividerSize.FillMax(
        padding = 16.dp
    ),
    width: Dp = 1.dp,
    color: Color = UI.colors.gray,
    shape: Shape = UI.shapes.rFull,
) {
    Spacer(
        modifier = Modifier
            .thenWhen {
                when (size) {
                    is DividerSize.FillMax -> {
                        fillMaxHeight()
                            .padding(vertical = size.padding)
                    }

                    is DividerSize.Fixed -> {
                        this.height(size.size)
                    }
                }
            }
            .width(width)
            .background(color, shape)
    )
}

@Deprecated("Old design system. Use `:ivy-design` and Material3")
@Composable
fun RowScope.DividerW(
    weight: Float = 1f,
    height: Dp = 1.dp,
    color: Color = UI.colors.gray,
    shape: Shape = UI.shapes.rFull,
) {
    Divider(
        modifier = Modifier
            .weight(weight)
            .height(1.dp),
        color = color,
        shape = shape
    )
}

@Deprecated("Old design system. Use `:ivy-design` and Material3")
@Composable
fun Divider(
    modifier: Modifier = Modifier,
    color: Color = UI.colors.gray,
    shape: Shape = UI.shapes.rFull,
) {
    Spacer(
        modifier = modifier
            .background(color, shape)
    )
}

@Deprecated("Old design system. Use `:ivy-design` and Material3")
sealed class DividerSize {
    @Deprecated("Old design system. Use `:ivy-design` and Material3")
    data class Fixed(val size: Dp) : DividerSize()

    @Deprecated("Old design system. Use `:ivy-design` and Material3")
    data class FillMax(val padding: Dp) : DividerSize()
}

@Suppress("UnusedPrivateMember")
@IvyPreviews
@Composable
private fun PreviewHorizontalDivider_fillMax() {
    IvyComponentPreview {
        DividerH(
            size = DividerSize.FillMax(
                padding = 16.dp
            )
        )
    }
}

@Suppress("UnusedPrivateMember")
@IvyPreviews
@Composable
private fun PreviewHorizontalDivider_fixed() {
    IvyComponentPreview {
        DividerH(
            size = DividerSize.Fixed(
                size = 32.dp
            )
        )
    }
}

@Suppress("UnusedPrivateMember")
@IvyPreviews
@Composable
private fun PreviewVerticalDivider_fillMax() {
    IvyComponentPreview {
        DividerV(
            size = DividerSize.FillMax(
                padding = 16.dp
            )
        )
    }
}

@Suppress("UnusedPrivateMember")
@IvyPreviews
@Composable
private fun PreviewVerticalDivider_fixed() {
    IvyComponentPreview {
        DividerV(
            size = DividerSize.Fixed(
                size = 16.dp
            )
        )
    }
}

@Suppress("UnusedPrivateMember")
@IvyPreviews
@Composable
private fun PreviewDivider() {
    IvyComponentPreview {
        Row {
            SpacerHor(16.dp)

            Divider(
                modifier = Modifier
                    .weight(1f)
                    .height(2.dp)
            )

            SpacerHor(16.dp)

            Divider(
                modifier = Modifier
                    .weight(1f)
                    .height(2.dp)
            )

            SpacerHor(16.dp)

            Divider(
                modifier = Modifier
                    .weight(1f)
                    .height(2.dp)
            )

            SpacerHor(16.dp)
        }
    }
}
