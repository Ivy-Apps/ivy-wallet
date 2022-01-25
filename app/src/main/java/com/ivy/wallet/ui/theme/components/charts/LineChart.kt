package com.ivy.wallet.ui.theme.components.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.wallet.base.format
import com.ivy.wallet.base.lerp
import com.ivy.wallet.base.toDensityDp
import com.ivy.wallet.ui.theme.*
import com.ivy.wallet.ui.theme.modal.model.Month
import kotlin.math.abs

data class Value(
    val x: Double,
    val y: Double
)

@Composable
fun IvyLineChart(
    modifier: Modifier = Modifier,
    values: List<Value>,
    xLabel: (x: Double) -> String,
    yLabel: (y: Double) -> String = { it.format("BGN") }
) {
    if (values.isEmpty()) return

    val maxY = remember(values) {
        values.maxOf { it.y }
    }
    val minY = remember(values) {
        values.minOf { it.y }
    }
    val chartColor = IvyTheme.colors.pureInverse

    Column(
        modifier = modifier
    ) {
        var yLabelWidthPx by remember {
            mutableStateOf(0)
        }

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            //Y values
            Column(
                modifier = Modifier
                    .onSizeChanged {
                        yLabelWidthPx = it.width
                    }
                    .height(300.dp)
                    .padding(end = 8.dp)
            ) {
                val yValues = remember(minY, maxY) {
                    yValues(
                        min = minY,
                        max = maxY
                    )
                }

                for ((index, value) in yValues.withIndex()) {
                    Text(
                        text = yLabel(value),
                        style = Typo.numberCaption
                    )

                    if (index < yValues.size - 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            Chart(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                chartColor = chartColor,
                maxY = maxY,
                minY = minY,
                values = values
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        //X labels
        Row {
            if (yLabelWidthPx > 0) {
                Spacer(Modifier.width(yLabelWidthPx.toDensityDp()))
            }

            Spacer(modifier = Modifier.weight(1f))

            values.forEachIndexed { index, value ->
                Text(
                    modifier = Modifier.width(10.dp),
                    text = xLabel(value.x),
                    style = Typo.body1.style(
                        textAlign = TextAlign.Center
                    )
                )

                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

private fun yValues(
    min: Double,
    max: Double
): List<Double> {
    val center = (min + max) / 2
    val centerTop = (center + max) / 2
    val centerBottom = (center + min) / 2

    return listOf(
        max,
        centerTop,
        center,
        centerBottom,
        min
    )
}

@Composable
private fun Chart(
    modifier: Modifier,
    chartColor: Color,
    maxY: Double,
    minY: Double,
    values: List<Value>
) {
    Canvas(modifier = modifier) {
        val chartWidth = size.width
        val chartHeight = size.height

        //draw chart coordinate system
        //Y line
        drawLine(
            color = chartColor,
            start = Offset.Zero,
            end = Offset(x = 0f, y = chartHeight),
            strokeWidth = 4.dp.toPx(),
            cap = StrokeCap.Round
        )

        //X line
        drawLine(
            color = chartColor,
            start = Offset(x = 0f, y = chartHeight),
            end = Offset(x = chartWidth, y = chartHeight),
            strokeWidth = 4.dp.toPx(),
            cap = StrokeCap.Round
        )

        drawValues(
            chartWidth = chartWidth,
            chartHeight = chartHeight,
            maxY = maxY,
            minY = minY,
            values = values
        )
    }
}

private fun DrawScope.drawValues(
    chartWidth: Float,
    chartHeight: Float,
    maxY: Double,
    minY: Double,
    values: List<Value>
) {
    // Total number of transactions.
    val totalRecords = values.size
    // Maximum distance between dots (transactions)
    val lineDistance = chartWidth / (totalRecords + 1)
    // Add some kind of a "Padding" for the initial point where the line starts.
    var currentX = 0F + lineDistance
    val lineWidth = 2.dp.toPx()

    values.forEachIndexed { index, value ->
        if (totalRecords >= index + 2) {
            drawLine(
                start = Offset(
                    x = currentX,
                    y = calculateYCoordinate(
                        max = maxY,
                        min = minY,
                        value = value.y,
                        chartHeight = chartHeight - 8.dp.toPx()
                    )
                ),
                end = Offset(
                    x = currentX + lineDistance,
                    y = calculateYCoordinate(
                        max = maxY,
                        min = minY,
                        value = values[index + 1].y,
                        chartHeight = chartHeight - 8.dp.toPx()
                    )
                ),
                color = Ivy,
                strokeWidth = lineWidth,
                cap = StrokeCap.Butt
            )
        }
        currentX += lineDistance
    }
}


private fun calculateYCoordinate(
    max: Double,
    min: Double,
    value: Double,
    chartHeight: Float
): Float {
    //maxY = 0
    //minY = chartHeight
    /* Example:
        min = -100
        max = 100
        v1 = 25
        v2 = 100
        v3 = -100

        range = abs(min) + abs(max) //200
        v1' = abs(min) + v          //125
        v2' = 200
        v3' = 0
     */
    val range = abs(min) + abs(max)
    val vAdjusted = abs(min) + value
    val yPercent = vAdjusted / range

    return lerp(0.0, chartHeight.toDouble(), 1f - yPercent).toFloat()
}

@Preview
@Composable
private fun Preview() {
    IvyComponentPreview {
        val values = listOf(
            Value(
                x = 0.0,
                y = 5235.60
            ),
            Value(
                x = 1.0,
                y = 8000.0
            ),
            Value(
                x = 2.0,
                y = 15032.89
            ),
            Value(
                x = 3.0,
                y = 4123.0
            ),
            Value(
                x = 4.0,
                y = 1000.0
            ),
            Value(
                x = 5.0,
                y = -5000.0
            ),
            Value(
                x = 6.0,
                y = 3000.0
            ),
            Value(
                x = 7.0,
                y = 9000.0
            ),
            Value(
                x = 8.0,
                y = 15600.50
            ),
            Value(
                x = 9.0,
                y = 20000.0
            ),
            Value(
                x = 10.0,
                y = 0.0
            ),
            Value(
                x = 11.0,
                y = 1000.0
            ),
        )

        IvyLineChart(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            values = values,
            xLabel = {
                Month.monthsList()[it.toInt()].name.first().toString()
            }
        )
    }
}