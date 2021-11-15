package com.ivy.wallet.base

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.pointerInput

fun Modifier.verticalSwipeListener(
    sensitivity: Int,
    onSwipeUp: () -> Unit = {},
    onSwipeDown: () -> Unit = {}
): Modifier = composed {
    var swipeOffset by remember {
        mutableStateOf(0f)
    }

    this.pointerInput(Unit) {
        detectVerticalDragGestures(
            onDragEnd = {
                swipeOffset = 0f
            },
            onVerticalDrag = { _, dragAmount ->
                //dragAmount: positive when scrolling down; negative when scrolling up
                swipeOffset += dragAmount

                when {
                    swipeOffset > sensitivity -> {
                        //offset > 0 when swipe down
                        onSwipeDown()
                    }

                    swipeOffset < -sensitivity -> {
                        //offset < 0 when swipe up
                        onSwipeUp()
                    }
                }

            }
        )
    }
}

fun Modifier.horizontalSwipeListener(
    sensitivity: Int,
    onSwipeLeft: () -> Unit = {},
    onSwipeRight: () -> Unit = {}
): Modifier = composed {
    var swipeOffset by remember {
        mutableStateOf(0f)
    }

    this.pointerInput(Unit) {
        detectHorizontalDragGestures(
            onDragEnd = {
                swipeOffset = 0f
            },
            onHorizontalDrag = { _, dragAmount ->
                //dragAmount: positive when scrolling down; negative when scrolling up
                swipeOffset += dragAmount

                when {
                    swipeOffset > sensitivity -> {
                        //offset > 0 when swipe right
                        onSwipeRight()
                    }

                    swipeOffset < -sensitivity -> {
                        //offset < 0 when swipe left
                        onSwipeLeft()
                    }
                }

            }
        )
    }
}