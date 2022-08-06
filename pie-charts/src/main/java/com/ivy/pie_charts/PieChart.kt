package com.ivy.pie_charts

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.view.MotionEvent
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.ivy.pie_charts.model.CategoryAmount
import com.ivy.base.IvyWalletComponentPreview
import com.ivy.data.Category
import com.ivy.data.transaction.TransactionType
import com.ivy.design.l0_system.UI
import com.ivy.wallet.ui.theme.*
import com.ivy.wallet.ui.theme.components.IvyIcon
import com.ivy.wallet.utils.convertDpToPixel
import com.ivy.wallet.utils.drawColoredShadow
import com.ivy.wallet.utils.timeNowUTC
import com.ivy.wallet.utils.toEpochMilli
import timber.log.Timber
import kotlin.math.acos
import kotlin.math.sqrt


const val PIE_CHART_RADIUS_DP = 128
const val RADIUS_DP = 112f

@Composable
fun PieChart(
    type: TransactionType,
    categoryAmounts: List<CategoryAmount>,
    selectedCategory: SelectedCategory?,

    onCategoryClicked: (Category?) -> Unit = {}
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            modifier = Modifier
                .size((PIE_CHART_RADIUS_DP * 2).dp)
                .drawColoredShadow(
                    color = Black,
                    alpha = if (UI.colors.isLight) 0.05f else 0.5f,
                    offsetY = 32.dp,
                    shadowRadius = 48.dp
                )
                .clip(CircleShape)
                .background(
                    brush = Gradient(
                        UI.colors.medium,
                        UI.colors.pure
                    ).asVerticalBrush(),
                    shape = CircleShape
                )
                .padding(all = 16.dp),
            factory = {
                PieChartView(it)
            },
            update = { view ->
                view.display(
                    categoryAmounts = categoryAmounts,
                    selectedCategory = selectedCategory,
                    onCategoryClicked = onCategoryClicked
                )
            }
        )

        IvyIcon(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(UI.colors.medium)
                .padding(all = 20.dp),
            icon = if (type == TransactionType.INCOME) R.drawable.ic_income else R.drawable.ic_expense,
            tint = Gray
        )
    }

}

private class PieChartView(context: Context) : View(context) {
    private var categoryAmounts = emptyList<CategoryAmount>()
    private var paints = mapOf<Category?, Paint>()
    private var totalAmount = 0.0

    val rectangle = RectF(
        0f, 0f,
        convertDpToPixel(context, 2 * RADIUS_DP),
        convertDpToPixel(context, 2 * RADIUS_DP)
    )

    var onCategoryClicked: (Category?) -> Unit = {}

    fun display(
        categoryAmounts: List<CategoryAmount>,
        selectedCategory: SelectedCategory?,
        onCategoryClicked: (Category?) -> Unit
    ) {
        this.onCategoryClicked = onCategoryClicked

        this.categoryAmounts = categoryAmounts
        this.totalAmount = categoryAmounts.sumOf { it.amount }

        this.paints = categoryAmounts
            .map {
                val category = it.category
                val categoryColor = category?.color?.toComposeColor() ?: Gray
                val color = if (selectedCategory == null) {
                    categoryColor
                } else {
                    if (selectedCategory.category == category) {
                        categoryColor
                    } else {
                        categoryColor.copy(
                            alpha = 0.15f
                        )
                    }
                }

                category to paintFor(
                    color = color
                )
            }
            .toMap()

        invalidate()
    }

    private fun paintFor(color: Color): Paint {
        return Paint().apply {
            this.color = color.toArgb()
            this.strokeWidth = convertDpToPixel(context, 2f)
            this.strokeCap = Paint.Cap.ROUND
            this.strokeJoin = Paint.Join.ROUND
            this.isAntiAlias = true
        }
    }

    private val zones = mutableListOf<Zone>()

    override fun onDraw(canvas: Canvas) {
        var startAngle = -90.0

        zones.clear()

        for (categoryAmount in categoryAmounts) {
            val paint = paints[categoryAmount.category] ?: continue
            val amount = categoryAmount.amount

            if (amount == 0.0) continue

            val percent = amount / totalAmount
            val sweepAngle = 360 * percent

            zones.add(
                Zone(
                    startAngle = startAngle,
                    endAngle = startAngle + sweepAngle,
                    category = categoryAmount.category
                )
            )

            canvas.drawArc(
                rectangle,
                startAngle.toFloat(),
                sweepAngle.toFloat(),
                true,
                paint
            ) //draw

            startAngle += sweepAngle
        }
    }


    private var startClickTime = 0L

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startClickTime = timeNowUTC().toEpochMilli()
            }
            MotionEvent.ACTION_UP -> {
                val clickDuration: Long = timeNowUTC().toEpochMilli() - startClickTime
                if (clickDuration < MAX_CLICK_DURATION) {
                    val touchX = event.x
                    val touchY = event.y

                    val centerX = width / 2f
                    val centerY = height / 2f
                    Timber.d("click: x = $touchX, y = $touchY (width = $width, height = $height)")

                    val angle = getAngle(
                        touchX = touchX,
                        touchY = touchY,
                        centerX = centerX,
                        centerY = centerY
                    )

                    Timber.d("degrees = $angle")

                    val clickedCategory = zones
                        .firstOrNull { zone ->
                            zone.contains(angle = angle)
                        }?.category
                    Timber.i("clicked category = ${clickedCategory?.name}")

                    onCategoryClicked(clickedCategory)
                }
            }
        }
        return true
    }

    private fun getAngle(
        touchX: Float,
        touchY: Float,
        centerX: Float,
        centerY: Float
    ): Double {
        val angle: Double
        val x2 = touchX - centerX
        val y2 = touchY - centerY
        val d1 = sqrt((centerY * centerY).toDouble())
        val d2 = sqrt((x2 * x2 + y2 * y2).toDouble())
        angle = if (touchX >= centerX) {
            Math.toDegrees(acos((-centerY * y2) / (d1 * d2)))
        } else
            360 - Math.toDegrees(acos((-centerY * y2) / (d1 * d2)))
        return angle - 90.0
    }

    companion object {
        private const val MAX_CLICK_DURATION = 200
    }

    private data class Zone(
        val startAngle: Double,
        val endAngle: Double,
        val category: Category?
    ) {
        fun contains(angle: Double): Boolean =
            angle > startAngle && angle < endAngle
    }
}

@Preview
@Composable
private fun Preview() {
    IvyWalletComponentPreview {
        Column(
            Modifier.fillMaxSize()
        ) {
            Spacer(Modifier.weight(1f))

            PieChart(
                type = TransactionType.EXPENSE,
                categoryAmounts = listOf(
                    CategoryAmount(
                        category = Category("Bills", Green.toArgb()),
                        amount = 791.0
                    ),
                    CategoryAmount(
                        category = Category("Shisha", Green.toArgb()),
                        amount = 411.93
                    ),
                    CategoryAmount(
                        category = Category("Food & Drink", IvyDark.toArgb()),
                        amount = 260.03
                    ),
                    CategoryAmount(
                        category = Category("Gifts", RedLight.toArgb()),
                        amount = 160.0
                    ),
                    CategoryAmount(
                        category = null,
                        amount = 497.0
                    ),
                ),
                selectedCategory = null
            )

            Spacer(Modifier.weight(1f))
        }
    }
}