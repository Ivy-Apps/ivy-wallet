package com.ivy.core.ui.amount.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.ivy.math.localDecimalSeparator

@Composable
fun rememberDecimalSeparator(): Char = remember { localDecimalSeparator() }