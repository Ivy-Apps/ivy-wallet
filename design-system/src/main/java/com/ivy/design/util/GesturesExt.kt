package com.ivy.wallet.utils

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.pointerInput

/**
 * sensitivity - the lower the number, the higher the sensitivity
 */
fun Modifier.verticalSwipeListener(
    sensitivity: Int,
    onSwipeUp: () -> Unit = {},
    onSwipeDown: () -> Unit = {}
): Modifier = composed {
    var swipeOffset by remember { mutableStateOf(0f) }
    var gestureConsumed by remember { mutableStateOf(false) }

    this.pointerInput(Unit) {
        detectVerticalDragGestures(
            onDragEnd = {
                swipeOffset = 0f
                gestureConsumed = false
            },
            onVerticalDrag = { _, dragAmount ->
                //dragAmount: positive when scrolling down; negative when scrolling up
                swipeOffset += dragAmount

                when {
                    swipeOffset > sensitivity -> {
                        //offset > 0 when swipe down
                        if (!gestureConsumed) {
                            onSwipeDown()
                            gestureConsumed = true
                        }
                    }

                    swipeOffset < -sensitivity -> {
                        //offset < 0 when swipe up
                        if (!gestureConsumed) {
                            onSwipeUp()
                            gestureConsumed = true
                        }
                    }
                }

            }
        )
    }
}

/**
 * sensitivity - the lower the number, the higher the sensitivity
 */
fun Modifier.horizontalSwipeListener(
    sensitivity: Int,
    onSwipeLeft: () -> Unit = {},
    onSwipeRight: () -> Unit = {}
): Modifier = composed {
    var swipeOffset by remember { mutableStateOf(0f) }
    var gestureConsumed by remember { mutableStateOf(false) }

    this.pointerInput(Unit) {
        detectHorizontalDragGestures(
            onDragEnd = {
                swipeOffset = 0f
                gestureConsumed = false
            },
            onHorizontalDrag = { _, dragAmount ->
                //dragAmount: positive when scrolling down; negative when scrolling up
                swipeOffset += dragAmount

                when {
                    swipeOffset > sensitivity -> {
                        //offset > 0 when swipe right
                        if (!gestureConsumed) {
                            onSwipeRight()
                            gestureConsumed = true
                        }
                    }

                    swipeOffset < -sensitivity -> {
                        //offset < 0 when swipe left
                        if (!gestureConsumed) {
                            onSwipeLeft()
                            gestureConsumed = true
                        }
                    }
                }

            }
        )
    }
}