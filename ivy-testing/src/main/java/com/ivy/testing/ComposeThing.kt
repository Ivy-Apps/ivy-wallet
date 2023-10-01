package com.ivy.testing

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun counter(): Int {
    var count by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        count = 42
    }
    return count
}