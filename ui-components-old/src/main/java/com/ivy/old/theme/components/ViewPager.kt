/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivy.wallet.ui.theme.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.unit.Density
import com.ivy.base.ivyWalletCtx
import com.ivy.wallet.utils.densityScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * This is a modified version of:
 * https://gist.github.com/adamp/07d468f4bcfe632670f305ce3734f511
 */

@Composable
fun Pager(
    state: PagerState,
    modifier: Modifier = Modifier,
    pageContent: @Composable PagerScope.() -> Unit
) {
    var pageSize by remember { mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()

    val screenWidth = ivyWalletCtx().screenWidth

    Layout(
        content = {
            //pageOffset <0 when moving forward
            val currentPageOffset = state.currentPageOffset
            val movingForward = currentPageOffset < 0
            val currentPage = state.currentPage

            val minPage = if (!movingForward)
                (state.currentPage - 1).coerceAtLeast(state.minPage) else currentPage
            val maxPage = if (movingForward)
                (state.currentPage + 1).coerceAtMost(state.maxPage) else currentPage

            for (page in minPage..maxPage) {
                val pageData = PageData(page)
                val scope = PagerScope(state, page)

                val alpha = if (page == currentPage)
                    1f - abs(currentPageOffset) else abs(currentPageOffset)
                key(pageData) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = pageData
                            .width(densityScope {
                                screenWidth.toDp()
                            })
                            .fillMaxHeight()
                            .alpha(alpha)
                    ) {
                        scope.pageContent()
                    }
                }
            }
        },
        modifier = modifier.draggable(
            orientation = Orientation.Horizontal,
            onDragStarted = {
                state.selectionState = PagerState.SelectionState.Undecided
            },
            onDragStopped = { velocity ->
                // Velocity is in pixels per second, but we deal in percentage offsets, so we
                // need to scale the velocity to match
                val finalVelocity = velocity / pageSize
                Timber.d("onDragStopped(): velocity = $velocity, finalVelocity = $finalVelocity, currentPageOffset = ${state.currentPageOffset}")

                if (abs(state.currentPageOffset) > 0.1f) {
                    val finalOffset = if (state.currentPageOffset > 0) 1f else -1f
                    state.animateTo(coroutineScope, finalOffset)
                } else {
                    state.animateTo(coroutineScope, 0f)
                }
            },
            state = rememberDraggableState { dx ->
                coroutineScope.launch {
                    with(state) {
                        val pos = pageSize * currentPageOffset
                        val max = if (currentPage == minPage) 0 else pageSize
                        val min = if (currentPage == maxPage) 0 else -pageSize
                        val newPos = (pos + dx).coerceIn(min.toFloat(), max.toFloat())
                        snapToOffset(newPos / pageSize)
                        Timber.d("pos = $pos, newPos = $newPos, dx = $dx, currentPageOffset = $currentPageOffset")
                    }
                }
            }
        )
    ) { measurables, constraints ->
        layout(constraints.maxWidth, constraints.maxHeight) {
            val currentPage = state.currentPage
            val swipeLeft = state.currentPageOffset < 0
            val offset = abs(state.currentPageOffset) //0f to 1f
            val childConstraints = constraints.copy(minWidth = 0, minHeight = 0)

            measurables
                .map {
                    it.measure(childConstraints) to it.page
                }
                .forEach { (placeable, page) ->
                    val xCenterOffset = (constraints.maxWidth - placeable.width) / 2
                    val yCenterOffset = (constraints.maxHeight - placeable.height) / 2

                    if (currentPage == page) {
                        pageSize = placeable.width
                    }

                    val x = if (page == currentPage) {
                        //page disappears
                        val distance = (screenWidth * offset).roundToInt()
                        xCenterOffset + if (swipeLeft) -distance else distance
                    } else {
                        //page appears
                        val distance = (screenWidth * (1f - offset)).roundToInt()
                        xCenterOffset + if (swipeLeft) distance else -distance
                    }

                    placeable.place(
                        x = x,
                        y = yCenterOffset
                    )
                }
        }
    }
}

class PagerState(
    currentPage: Int = 0,
    minPage: Int = 0,
    maxPage: Int = 0
) {
    private var _minPage by mutableStateOf(minPage)
    var minPage: Int
        get() = _minPage
        set(value) {
            _minPage = value.coerceAtMost(_maxPage)
            _currentPage = _currentPage.coerceIn(_minPage, _maxPage)
        }

    private var _maxPage by mutableStateOf(maxPage, structuralEqualityPolicy())
    var maxPage: Int
        get() = _maxPage
        set(value) {
            _maxPage = value.coerceAtLeast(_minPage)
            _currentPage = _currentPage.coerceIn(_minPage, maxPage)
        }

    private var _currentPage by mutableStateOf(currentPage.coerceIn(minPage, maxPage))
    var currentPage: Int
        get() = _currentPage
        set(value) {
            _currentPage = value.coerceIn(minPage, maxPage)
        }

    enum class SelectionState { Selected, Undecided }

    var selectionState by mutableStateOf(SelectionState.Selected)

    suspend fun selectPage() {
        currentPage -= currentPageOffset.roundToInt()
        snapToOffset(0f)
        selectionState = SelectionState.Selected
    }

    private var _currentPageOffset = Animatable(0f).apply {
        updateBounds(-1f, 1f)
    }
    val currentPageOffset: Float
        get() = _currentPageOffset.value

    suspend fun snapToOffset(offset: Float) {
        val max = if (currentPage == minPage) 0f else 1f
        val min = if (currentPage == maxPage) 0f else -1f
        _currentPageOffset.snapTo(offset.coerceIn(min, max))
    }

    suspend fun animateTo(coroutineScope: CoroutineScope, targetValue: Float) {
        _currentPageOffset.animateTo(
            targetValue = targetValue,
            animationSpec = tween(durationMillis = 200)
        ) {
            coroutineScope.launch {
                selectPage()
            }
        }
    }

//    fun fling(velocity: Float) {
//        Timber.d("ViewPager#fling($velocity)")
//        if (velocity < 0 && currentPage == maxPage) return
//        if (velocity > 0 && currentPage == minPage) return
//
//        currentPageOffsetAnim.fling(velocity) { reason, _, _ ->
//            Timber.d("_currentPageOffset.fling($velocity): reason = $reason")
//            if (reason != AnimationEndReason.Interrupted) {
//                currentPageOffsetAnim.animateTo(currentPageOffset.roundToInt().toFloat()) { _, _ ->
//                    selectPage()
//                }
//            }
//        }
//    }

    override fun toString(): String = "PagerState{minPage=$minPage, maxPage=$maxPage, " +
            "currentPage=$currentPage, currentPageOffset=$currentPageOffset}"
}

@Immutable
private data class PageData(val page: Int) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?): Any = this@PageData
}

private val Measurable.page: Int
    get() = (parentData as? PageData)?.page ?: error("no PageData for measurable $this")

/**
 * Scope for [Pager] content.
 */
class PagerScope(
    private val state: PagerState,
    val page: Int
) {
    /**
     * Returns the current selected page
     */
    val currentPage: Int
        get() = state.currentPage

    /**
     * Returns the current selected page offset
     */
    val currentPageOffset: Float
        get() = state.currentPageOffset

    /**
     * Returns the current selection state
     */
    val selectionState: PagerState.SelectionState
        get() = state.selectionState
}
