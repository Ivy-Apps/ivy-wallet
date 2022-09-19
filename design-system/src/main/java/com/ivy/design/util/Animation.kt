package com.ivy.design.util

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring

fun <T> springBounce(
    stiffness: Float = 500f,
) = spring<T>(
    dampingRatio = 0.75f,
    stiffness = stiffness,
)

fun <T> springBounceFast() = springBounce<T>(
    stiffness = 2000f
)

fun <T> springBounceMedium() = spring<T>(
    dampingRatio = 0.75f,
    stiffness = Spring.StiffnessLow,
)

fun <T> springBounceSlow() = spring<T>(
    dampingRatio = 0.75f,
    stiffness = Spring.StiffnessVeryLow,
)

fun <T> springBounceVerySlow() = spring<T>(
    dampingRatio = 0.75f,
    stiffness = 20f,
)