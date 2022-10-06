package com.ivy.core.ui.amount.components

import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ivy.core.ui.amount.rememberDecimalSeparator
import com.ivy.data.CurrencyCode
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.color.rememberContrastColor
import com.ivy.design.l1_buildingBlocks.B1Second
import com.ivy.design.l1_buildingBlocks.IconRes
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l1_buildingBlocks.SpacerWeight
import com.ivy.design.l3_ivyComponents.button.ButtonFeeling
import com.ivy.design.l3_ivyComponents.button.ButtonVisibility
import com.ivy.design.l3_ivyComponents.button.toColor
import com.ivy.design.util.ComponentPreview
import com.ivy.design.util.thenWhen
import com.ivy.resources.R

// region Customize UI
private const val keypadOuterWeight = 1f
private const val keypadInnerWeight = 0.25f
private val keypadButtonBig = 64.dp
private val keypadButtonSmall = 56.dp
private val keyboardVerticalMargin = 12.dp
// endregion

@Suppress("unused")
@Composable
internal fun ColumnScope.Keyboard(
    calculatorVisible: Boolean,
    onAmountChange: (String) -> Unit,
    onCurrencyChange: (CurrencyCode) -> Unit,
) {
    val onSymbolClick = { symbol: String ->
        // TODO:
    }
    val keypadBtnSize by animateDpAsState(
        targetValue = if (calculatorVisible)
            keypadButtonSmall else keypadButtonBig
    )
    CalculatorTopRow(
        calculatorVisible = calculatorVisible,
        keypadBtnSize = keypadBtnSize,
        onSymbolClick = onSymbolClick
    )
    // margin is built-in in calculator's top row
    KeyboardRow {
        SpacerWeight(weight = keypadOuterWeight)
        KeypadButton(symbol = "7", size = keypadBtnSize, onClick = onSymbolClick)
        SpacerWeight(weight = keypadInnerWeight)
        KeypadButton(symbol = "8", size = keypadBtnSize, onClick = onSymbolClick)
        SpacerWeight(weight = keypadInnerWeight)
        KeypadButton(symbol = "9", size = keypadBtnSize, onClick = onSymbolClick)
        AnimatedCalculatorButton(
            calculatorVisible = calculatorVisible,
            symbol = "*",
            onClick = onSymbolClick
        )
        SpacerWeight(weight = keypadOuterWeight)
    }
    SpacerVer(height = keyboardVerticalMargin)
    KeyboardRow {
        SpacerWeight(weight = keypadOuterWeight)
        KeypadButton(symbol = "4", size = keypadBtnSize, onClick = onSymbolClick)
        SpacerWeight(weight = keypadInnerWeight)
        KeypadButton(symbol = "5", size = keypadBtnSize, onClick = onSymbolClick)
        SpacerWeight(weight = keypadInnerWeight)
        KeypadButton(symbol = "6", size = keypadBtnSize, onClick = onSymbolClick)
        AnimatedCalculatorButton(
            calculatorVisible = calculatorVisible,
            symbol = "-",
            onClick = onSymbolClick
        )
        SpacerWeight(weight = keypadOuterWeight)
    }
    SpacerVer(height = keyboardVerticalMargin)
    KeyboardRow {
        SpacerWeight(weight = keypadOuterWeight)
        KeypadButton(symbol = "1", size = keypadBtnSize, onClick = onSymbolClick)
        SpacerWeight(weight = keypadInnerWeight)
        KeypadButton(symbol = "2", size = keypadBtnSize, onClick = onSymbolClick)
        SpacerWeight(weight = keypadInnerWeight)
        KeypadButton(symbol = "3", size = keypadBtnSize, onClick = onSymbolClick)
        AnimatedCalculatorButton(
            calculatorVisible = calculatorVisible,
            symbol = "+",
            onClick = onSymbolClick
        )
        SpacerWeight(weight = keypadOuterWeight)
    }
    SpacerVer(height = keyboardVerticalMargin)
    KeyboardRow {
        SpacerWeight(weight = keypadOuterWeight)
        KeypadButton(
            symbol = rememberDecimalSeparator().toString(),
            size = keypadBtnSize,
            onClick = onSymbolClick
        )
        SpacerWeight(weight = keypadInnerWeight)
        KeypadButton(symbol = "0", size = keypadBtnSize, onClick = onSymbolClick)
        SpacerWeight(weight = keypadInnerWeight)
        BackSpaceButton(size = keypadBtnSize) {
            // TODO: Handle backspace
        }
        AnimatedCalculatorButton(
            calculatorVisible = calculatorVisible,
            symbol = "=",
            feeling = ButtonFeeling.Positive,
            onClick = onSymbolClick
        )
        SpacerWeight(weight = keypadOuterWeight)
    }
}

// region Calculator
@Composable
private fun CalculatorTopRow(
    calculatorVisible: Boolean,
    keypadBtnSize: Dp,
    onSymbolClick: (String) -> Unit,
) {
    AnimatedVisibility(
        visible = calculatorVisible,
        enter = expandHorizontally() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
    ) {
        KeyboardRow(
            modifier = Modifier.padding(bottom = keyboardVerticalMargin)
        ) {
            SpacerWeight(weight = keypadOuterWeight)
            KeypadButton(
                symbol = "C",
                size = keypadBtnSize,
                visibility = ButtonVisibility.High,
                feeling = ButtonFeeling.Negative,
                onClick = onSymbolClick
            )
            SpacerWeight(weight = keypadInnerWeight)
            KeypadButton(
                symbol = "( )",
                visibility = ButtonVisibility.High,
                feeling = ButtonFeeling.Positive,
                size = keypadBtnSize,
                onClick = onSymbolClick
            )
            SpacerWeight(weight = keypadInnerWeight)
            KeypadButton(
                symbol = "%",
                visibility = ButtonVisibility.High,
                feeling = ButtonFeeling.Positive,
                size = keypadBtnSize,
                onClick = onSymbolClick
            )
            SpacerWeight(weight = keypadInnerWeight)
            KeypadButton(
                symbol = "/",
                size = keypadBtnSize,
                visibility = ButtonVisibility.High,
                feeling = ButtonFeeling.Positive,
                onClick = onSymbolClick
            )
            SpacerWeight(weight = keypadOuterWeight)
        }
    }
}

@Composable
private fun RowScope.AnimatedCalculatorButton(
    calculatorVisible: Boolean,
    symbol: String,
    modifier: Modifier = Modifier,
    feeling: ButtonFeeling = ButtonFeeling.Positive,
    onClick: (String) -> Unit
) {
    if (calculatorVisible) {
        SpacerWeight(weight = keypadInnerWeight)
    }
    AnimatedVisibility(
        visible = calculatorVisible,
        enter = expandHorizontally() + fadeIn(),
        exit = shrinkHorizontally() + fadeOut(),
    ) {
        KeypadButton(
            modifier = modifier,
            feeling = feeling,
            visibility = ButtonVisibility.High,
            size = keypadButtonSmall,
            symbol = symbol,
            onClick = onClick
        )
    }
}
// endregion

@Composable
private fun KeyboardRow(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        content = content,
    )
}

// region Keypad Buttons
@Composable
private fun KeypadButton(
    symbol: String,
    size: Dp,
    modifier: Modifier = Modifier,
    visibility: ButtonVisibility = ButtonVisibility.Medium,
    feeling: ButtonFeeling = ButtonFeeling.Positive,
    onClick: (String) -> Unit
) {
    KeypadButtonBox(
        modifier = modifier,
        feeling = feeling,
        visibility = visibility,
        size = size,
        onClick = { onClick(symbol) }
    ) {
        B1Second(
            text = symbol,
            color = when (visibility) {
                ButtonVisibility.Focused,
                ButtonVisibility.High ->
                    rememberContrastColor(feeling.toColor())
                ButtonVisibility.Medium,
                ButtonVisibility.Low -> UI.colorsInverted.pure
            },
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun BackSpaceButton(
    size: Dp,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    KeypadButtonBox(
        modifier = modifier,
        feeling = ButtonFeeling.Negative,
        size = size,
        onClick = onClick,
    ) {
        IconRes(
            icon = R.drawable.outline_backspace_24,
            tint = UI.colorsInverted.pure,
        )
    }
}

@Composable
private fun KeypadButtonBox(
    feeling: ButtonFeeling,
    size: Dp,
    modifier: Modifier = Modifier,
    visibility: ButtonVisibility = ButtonVisibility.Medium,
    onClick: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(UI.shapes.circle)
            .thenWhen {
                when (visibility) {
                    ButtonVisibility.Focused,
                    ButtonVisibility.High -> background(
                        color = feeling.toColor(),
                        shape = UI.shapes.circle
                    )
                    ButtonVisibility.Low,
                    ButtonVisibility.Medium -> border(
                        width = 1.dp,
                        color = feeling.toColor(),
                        shape = UI.shapes.circle
                    )
                }
            }
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
        content = content
    )
}
// endregion


// region Previews
@Preview
@Composable
private fun Preview() {
    ComponentPreview {
        Column {
            Keyboard(
                calculatorVisible = false,
                onAmountChange = {},
                onCurrencyChange = {}
            )
        }
    }
}

@Preview
@Composable
private fun Preview_calculator_visible() {
    ComponentPreview {
        Column {
            Keyboard(
                calculatorVisible = true,
                onAmountChange = {},
                onCurrencyChange = {}
            )
        }
    }
}
// endregion