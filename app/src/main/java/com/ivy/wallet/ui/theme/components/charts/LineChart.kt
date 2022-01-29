package com.ivy.wallet.ui.theme.components.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ivy.wallet.base.format
import com.ivy.wallet.base.lerp
import com.ivy.wallet.base.toDensityDp
import com.ivy.wallet.ui.theme.*
import com.ivy.wallet.ui.theme.modal.model.Month
import kotlin.math.pow
import kotlin.math.sqrt

data class Value(
    val x: Double,
    val y: Double
)

data class Function(
    val values: List<Value>,
    val color: Color,
    val colorDown: Color? = null,
) {
    fun determineLineColor(valueStart: Double, valueEnd: Double): Color {
        return if (colorDown != null) {
            if (valueStart <= valueEnd) color else colorDown
        } else {
            color
        }
    }
}

data class TapEvent(
    val functionIndex: Int,
    val valueIndex: Int
)

data class FunctionPoint(
    val functionIndex: Int,
    val valueIndex: Int,
    val point: Offset
)

@Composable
fun IvyLineChart(
    modifier: Modifier = Modifier,
    height: Dp = 300.dp,
    functions: List<Function>,
    xLabel: (x: Double) -> String,
    yLabel: (y: Double) -> String,
    onTap: (TapEvent) -> Unit = {}
) {
    val allValues = functions.flatMap { it.values }
    if (allValues.isEmpty()) return

    val maxY = remember(functions) {
        allValues.maxOf { it.y }
    }
    val minY = remember(functions) {
        allValues.minOf { it.y }
    }
    val chartColor = IvyTheme.colors.pureInverse

    var tapEvent: TapEvent? by remember(functions) {
        mutableStateOf(null)
    }
    val onTapInternal = { event: TapEvent ->
        tapEvent = event
        onTap(event)
    }

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
                    .height(height = height)
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
                    .height(height),
                chartColor = chartColor,
                maxY = maxY,
                minY = minY,
                functions = functions,
                tapEvent = tapEvent,
                onTap = onTapInternal
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        //X labels
        Row {
            if (yLabelWidthPx > 0) {
                Spacer(Modifier.width(yLabelWidthPx.toDensityDp()))
            }

            Spacer(modifier = Modifier.weight(1f))

            allValues.map { it.x }.toSet().forEachIndexed { index, x ->
                Text(
                    modifier = Modifier
                        .width(10.dp)
                        .clickable {
                            if (functions.size == 1) {
                                onTapInternal(
                                    TapEvent(
                                        functionIndex = 0,
                                        valueIndex = x.toInt()
                                    )
                                )
                            }
                        },
                    text = xLabel(x),
                    style = Typo.body1.style(
                        textAlign = TextAlign.Center,
                        color = if (index == tapEvent?.valueIndex)
                            Ivy else IvyTheme.colors.pureInverse
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
    functions: List<Function>,
    tapEvent: TapEvent?,
    onTap: (TapEvent) -> Unit
) {
    var points: List<FunctionPoint> by remember(functions) {
        mutableStateOf(emptyList())
    }

    Canvas(
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { clickPoint ->
                        val targetPoint = points.minByOrNull {
                            clickPoint.distance(it.point)
                        } ?: return@detectTapGestures

                        onTap(
                            TapEvent(
                                functionIndex = targetPoint.functionIndex,
                                valueIndex = targetPoint.valueIndex
                            )
                        )
                    }
                )
            }
    ) {
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

        points = drawValues(
            chartWidth = chartWidth,
            chartHeight = chartHeight,
            maxY = maxY,
            minY = minY,
            functions = functions,
        )

        if (tapEvent != null) {
            val tappedValue = functions[tapEvent.functionIndex].values[tapEvent.valueIndex]
            val radius = 8.dp.toPx()

            drawCircle(
                color = Ivy,
                radius = radius,
                center = Offset(
                    x = calculateXCoordinate(
                        values = functions[tapEvent.functionIndex].values,
                        valueIndex = tapEvent.valueIndex,
                        chartWidth = chartWidth
                    ),
                    y = calculateYCoordinate(
                        max = maxY,
                        min = minY,
                        value = tappedValue.y,
                        chartHeight = chartHeight
                    ) - 4.dp.toPx() //marginFromX
                )
            )
        }
    }
}

private fun DrawScope.drawValues(
    chartWidth: Float,
    chartHeight: Float,
    maxY: Double,
    minY: Double,
    functions: List<Function>,
): List<FunctionPoint> {
    // Total number of transactions.
    val totalRecords = functions.first().values.size
    // Maximum distance between dots (transactions)
    val lineDistance = chartWidth / (totalRecords + 1)
    // Add some kind of a "Padding" for the initial point where the line starts.
    val lineWidth = 3.dp.toPx()
    val marginFromX = 4.dp.toPx()


    return functions.flatMapIndexed { index, function ->
        drawFunction(
            function = function,
            functionIndex = index,
            lineDistance = lineDistance,
            minY = minY,
            maxY = maxY,
            lineWidth = lineWidth,
            marginFromX = marginFromX,
            chartHeight = chartHeight,
        )
    }
}

private fun DrawScope.drawFunction(
    function: Function,
    functionIndex: Int,
    lineDistance: Float,
    minY: Double,
    maxY: Double,
    marginFromX: Float,
    chartHeight: Float,
    lineWidth: Float,
): List<FunctionPoint> {
    val points = mutableListOf<FunctionPoint>()

    var currentX = 0f
    val values = function.values
    val totalRecords = values.size

    values.forEachIndexed { index, value ->
        currentX += lineDistance
        if (totalRecords >= index + 2) {
            val valueStart = value.y
            val valueEnd = values[index + 1].y

            val pointStart = Offset(
                x = currentX,
                y = calculateYCoordinate(
                    max = maxY,
                    min = minY,
                    value = valueStart,
                    chartHeight = chartHeight - marginFromX
                )
            )
            val pointEnd = Offset(
                x = currentX + lineDistance,
                y = calculateYCoordinate(
                    max = maxY,
                    min = minY,
                    value = valueEnd,
                    chartHeight = chartHeight - marginFromX
                )
            )

            if (index == 0) {
                points.add(
                    FunctionPoint(
                        functionIndex = functionIndex,
                        valueIndex = index,
                        point = pointStart
                    )
                )
            }

            points.add(
                FunctionPoint(
                    functionIndex = functionIndex,
                    valueIndex = index + 1,
                    point = pointEnd
                )
            )

            drawLine(
                start = pointStart,
                end = pointEnd,
                color = function.determineLineColor(
                    valueStart = valueStart,
                    valueEnd = valueEnd
                ),
                strokeWidth = lineWidth,
                pathEffect = PathEffect.cornerPathEffect(8.dp.toPx()),
                cap = StrokeCap.Round
            )
        }
    }

    return points
}


private fun calculateXCoordinate(
    values: List<Value>,
    valueIndex: Int,
    chartWidth: Float,
): Float {
    val totalRecords = values.size
    val lineDistance = chartWidth / (totalRecords + 1)

    return lineDistance * valueIndex + lineDistance
}


private fun calculateYCoordinate(
    max: Double,
    min: Double,
    value: Double,
    chartHeight: Float
): Float {
    //Lerp: (start + x * (end - start)) = value
    //x * (end - start) = value - start
    //x = (value - start) / (end - start)
    val yPercent = (value - min) / (max - min)

    return lerp(0.0, chartHeight.toDouble(), 1f - yPercent).toFloat()
}

private fun Offset.distance(point2: Offset): Float {
    return sqrt((point2.x - x).pow(2) + (point2.y - y).pow(2))
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
            functions = listOf(
                Function(
                    values = values,
                    color = Green
                )
            ),
            xLabel = {
                Month.monthsList()[it.toInt()].name.first().toString()
            },
            yLabel = {
                it.format("BGN")
            }
        )
    }
}

fun redGreenGradient(startY: Float, endY: Float): Brush {
    return if (startY >= endY) Green.asBrush() else Red.asBrush()
}