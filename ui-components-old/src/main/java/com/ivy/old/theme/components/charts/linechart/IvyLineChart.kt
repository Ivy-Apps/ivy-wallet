package com.ivy.wallet.ui.theme.components.charts.linechart

import android.text.TextPaint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ivy.core.ui.temp.trash.Month
import com.ivy.design.l0_system.UI
import com.ivy.design.util.ComponentPreview
import com.ivy.wallet.ui.theme.Gray
import com.ivy.wallet.ui.theme.Green
import com.ivy.wallet.ui.theme.Ivy
import com.ivy.wallet.utils.lerp
import timber.log.Timber
import java.text.DecimalFormat
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


private fun DrawScope.drawTappedPoint(
    functions: List<Function>,
    tapEvent: TapEvent?,
    chartWidth: Float,
    chartHeight: Float,
    minY: Double,
    maxY: Double
) {
    tapEvent?.let {
        val tappedValue = functions
            .getOrNull(it.functionIndex)?.values
            ?.get(it.valueIndex)
            ?: return@let
        val radius = 8.dp.toPx()

        drawCircle(
            color = Ivy,
            radius = radius,
            center = Offset(
                x = calculateXCoordinate(
                    values = functions[it.functionIndex].values,
                    valueIndex = it.valueIndex,
                    chartWidth = chartWidth
                ),
                y = calculateYCoordinate(
                    max = maxY,
                    min = minY,
                    value = tappedValue.y,
                    chartHeight = chartHeight,
                    offsetTop = 0f, //TODO: Fix
                    offsetBottom = 0f //TODO: Fix
                ) - 4.dp.toPx() //marginFromX //TODO: FIX
            )
        )
    }
}

private fun DrawScope.drawFunctions(
    chartWidth: Float,
    lineDistance: Float,
    chartHeight: Float,
    offsetLeft: Float = 0f,
    offsetTop: Float = 0f,
    offsetBottom: Float = 0f,
    cellSize: Float,
    maxY: Double,
    minY: Double,
    functions: List<Function>,
): List<FunctionPoint> {
    // Add some kind of a "Padding" for the initial point where the line starts.
    val lineWidth = 3.dp.toPx()
    val marginFromX = 4.dp.toPx()


    return functions.flatMapIndexed { index, function ->
        drawFunction(
            function = function,
            functionIndex = index,
            minY = minY,
            maxY = maxY,
            cellSize = cellSize,
            lineDistance = lineDistance,
            lineWidth = lineWidth,
            chartHeight = chartHeight,
            offsetLeft = offsetLeft,
            offsetTop = offsetTop,
            offsetBottom = offsetBottom
        )
    }
}

private fun DrawScope.drawFunction(
    function: Function,
    functionIndex: Int,
    minY: Double,
    maxY: Double,
    chartHeight: Float,
    cellSize: Float,
    lineDistance: Float,
    lineWidth: Float,
    offsetLeft: Float,
    offsetTop: Float,
    offsetBottom: Float,
): List<FunctionPoint> {
    val points = mutableListOf<FunctionPoint>()

    var currentX = offsetLeft
    val values = function.values
    val totalRecords = values.size

    values.forEachIndexed { index, value ->
        if (totalRecords >= index + 2) {
            val valueStart = value.y
            val valueEnd = values[index + 1].y

            val pointStart = Offset(
                x = currentX,
                y = calculateYCoordinate(
                    max = maxY,
                    min = minY,
                    value = valueStart,
                    chartHeight = chartHeight,
                    offsetTop = offsetTop,
                    offsetBottom = offsetBottom
                )
            )
            val pointEnd = Offset(
                x = currentX + lineDistance,
                y = calculateYCoordinate(
                    max = maxY,
                    min = minY,
                    value = valueEnd,
                    chartHeight = chartHeight,
                    offsetTop = offsetTop,
                    offsetBottom = offsetBottom
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

        currentX += lineDistance
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
    chartHeight: Float,
    offsetTop: Float,
    offsetBottom: Float
): Float {
    //Lerp: (start + x * (end - start)) = value
    //x * (end - start) = value - start
    //x = (value - start) / (end - start)
    val yPercent = (value - min) / (max - min)

    return lerp(
        start = offsetTop.toDouble(),
        end = (chartHeight - offsetBottom).toDouble(),
        fraction = 1f - yPercent
    ).toFloat()
}

private fun Offset.distance(point2: Offset): Float {
    return sqrt((point2.x - x).pow(2) + (point2.y - y).pow(2))
}


@Composable
fun IvyLineChart(
    modifier: Modifier = Modifier,
    height: Dp = 300.dp,
    functions: List<Function>,
    title: String,
    xLabel: (x: Double) -> String,
    yLabel: (y: Double) -> String,
    onTap: (TapEvent) -> Unit = {}
) {
    val allValues = functions.flatMap { it.values }
    if (allValues.isEmpty()) return

    val maxY = allValues.maxOf { it.y }
    val minY = allValues.minOf { it.y }

    var tapEvent: TapEvent? by remember {
        mutableStateOf(null)
    }
    val onTapInternal = { event: TapEvent ->
        tapEvent = event
        onTap(event)
    }

    IvyChart(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(UI.shapes.r2)
            .border(2.dp, Gray, UI.shapes.r2),
        title = title,
        allValues = allValues,
        xLabel = xLabel,
        yLabel = yLabel,
        maxY = maxY,
        minY = minY,
        functions = functions,
        tapEvent = tapEvent,
        onTap = onTapInternal
    )
}

@Composable
private fun IvyChart(
    modifier: Modifier,
    title: String,
    allValues: List<Value>,
    xLabel: (x: Double) -> String,
    yLabel: (y: Double) -> String,
    maxY: Double,
    minY: Double,
    functions: List<Function>,
    tapEvent: TapEvent?,
    onTap: (TapEvent) -> Unit
) {
    var points: List<FunctionPoint> by remember {
        mutableStateOf(emptyList())
    }

    val xLabelColor = UI.colors.pureInverse

    Canvas(
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { clickPoint ->
                        val targetPoint = points.minByOrNull {
                            clickPoint.distance(it.point)
                        } ?: return@detectTapGestures

                        Timber.i("points.size = ${points.size}")

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

        val offsetCellsLeft = 2
        val offsetCellsRight = 1

        val totalRecords = functions.first().values.size
        val xDistance = chartWidth / (totalRecords + offsetCellsLeft + offsetCellsRight)


        val cellSize = xDistance
        val offsetTop = cellSize * 3
        val offsetBottom = cellSize



        drawTitle(
            title = title,
            cellSize = cellSize,
            chartWidth = chartWidth
        )

        drawXLabels(
            cellSize = cellSize,
            offsetLeft = offsetCellsLeft * cellSize,
            offsetRight = offsetCellsRight * cellSize,
            lineDistance = xDistance,
            chartHeight = chartHeight,
            allValues = allValues,
            textColor = xLabelColor,
            xLabel = xLabel
        )

        drawYValues(
            chartHeight = chartHeight,
            offsetBottom = offsetBottom,
            offsetTop = offsetTop,
            cellSize = cellSize,
            maxY = maxY,
            minY = minY,
            yLabel = yLabel
        )

        grid(
            chartWidth = chartWidth,
            chartHeight = chartHeight,
            cellSize = cellSize
        )

        points = drawFunctions(
            chartWidth = chartWidth,
            chartHeight = chartHeight,
            maxY = maxY,
            minY = minY,
            cellSize = cellSize,
            offsetLeft = offsetCellsLeft * cellSize,
            offsetTop = offsetTop,
            offsetBottom = offsetBottom,
            functions = functions,
            lineDistance = xDistance
        )

        drawTappedPoint(
            functions = functions,
            tapEvent = tapEvent,
            chartWidth = chartWidth,
            chartHeight = chartHeight,
            minY = minY,
            maxY = maxY
        )
    }
}

fun DrawScope.drawTitle(
    title: String,
    cellSize: Float,
    chartWidth: Float
) {
    drawText(
        text = title,
        x = chartWidth / 2f,
        y = cellSize + 4.dp.toPx(),
        textSize = 16.sp
    )
}

fun DrawScope.drawYValues(
    minY: Double,
    maxY: Double,
    offsetTop: Float,
    chartHeight: Float,
    offsetBottom: Float,
    yLabel: (y: Double) -> String,
    cellSize: Float
) {
    val yValues = yValues(
        min = minY,
        max = maxY
    )

    val coordsMinY = chartHeight - offsetBottom
    val coordsMaxY = offsetTop

    val centerY = (coordsMinY + coordsMaxY) / 2
    val centerTopY = (centerY + coordsMaxY) / 2
    val centerBottomY = (centerY + coordsMinY) / 2

    val yCoords = listOf(
        coordsMaxY,
        centerTopY,
        centerY,
        centerBottomY,
        coordsMinY
    )

    for ((index, value) in yValues.withIndex()) {
        drawText(
            text = yLabel(value),
            x = cellSize,
            y = yCoords[index],
            textColor = Gray,
            textSize = 12.sp
        )
    }
}

fun DrawScope.drawXLabels(
    cellSize: Float,
    offsetLeft: Float,
    offsetRight: Float,
    lineDistance: Float,
    chartHeight: Float,
    allValues: List<Value>,
    xLabel: (x: Double) -> String,
    textColor: Color
) {
    allValues.map { it.x }.toSet().forEachIndexed { index, x ->
        drawText(
            text = xLabel(x),
            x = offsetLeft + (index * lineDistance),
            y = chartHeight - cellSize / 2f,
            textSize = 12.sp,
            textColor = textColor
        )
    }
}

fun DrawScope.drawText(
    text: String,
    x: Float,
    y: Float,
    textColor: Color = Gray,
    textSize: TextUnit,
) {
    val textPaint = TextPaint()
    textPaint.isAntiAlias = true
    textPaint.textSize = textSize.toPx()
    textPaint.color = textColor.toArgb()

    val textWidth = textPaint.measureText(text).toInt()

    drawIntoCanvas {
        it.nativeCanvas.drawText(
            text,
            x - textWidth / 2f,
            y,
            textPaint
        )
    }
}

private fun DrawScope.grid(
    chartWidth: Float,
    chartHeight: Float,
    cellSize: Float //24.dp
) {
    verticalLineXS(
        chartWidth = chartWidth,
        cellSize = cellSize
    ).forEach { x ->
        drawLine(
            color = Gray,
            start = Offset(
                x = x,
                y = 0f
            ),
            end = Offset(
                x = x,
                y = chartHeight
            )
        )
    }

    horizontalLineYS(
        chartHeight = chartHeight,
        cellSize = cellSize
    ).forEach { y ->
        drawLine(
            color = Gray,
            start = Offset(
                x = 0f,
                y = y
            ),
            end = Offset(
                x = chartWidth,
                y = y
            )
        )
    }
}

private fun verticalLineXS(
    chartWidth: Float,
    cellSize: Float,
    accumulator: List<Float> = emptyList(),
): List<Float> {
    val last = accumulator.lastOrNull()
    return if (cellSize >= chartWidth || (last != null && last >= chartWidth)) {
        accumulator
    } else {
        //recurse
        val next = (last ?: 0f) + cellSize

        verticalLineXS(
            chartWidth = chartWidth,
            cellSize = cellSize,
            accumulator = accumulator + next
        )
    }
}

private tailrec fun horizontalLineYS(
    chartHeight: Float,
    cellSize: Float,
    accumulator: List<Float> = emptyList()
): List<Float> {
    val last = accumulator.lastOrNull()
    return if (cellSize >= chartHeight || (last != null && last <= 0)) {
        accumulator
    } else {
        //recurse
        val next = (last ?: chartHeight) - cellSize

        horizontalLineYS(
            chartHeight = chartHeight,
            cellSize = cellSize,
            accumulator = accumulator + next
        )
    }
}


@Preview
@Composable
private fun Preview_IvyChart() {
    ComponentPreview {
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
            title = "EXPENSES LAST 12 MONTHS",
            height = 400.dp,
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
                DecimalFormat("#,###").format(it)
            }
        )
    }
}