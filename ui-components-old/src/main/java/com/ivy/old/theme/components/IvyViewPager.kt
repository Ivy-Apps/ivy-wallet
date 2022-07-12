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
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.unit.Density
import com.ivy.base.ivyWalletCtx
import com.ivy.wallet.utils.densityScope
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.math.roundToInt

private const val SWIPE_THRESHOLD = 0.2f

@Composable
fun IvyPager(
    state: IvyPagerState,
    pageContent: @Composable BoxScope.(page: Int) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var dragDistance by remember { mutableStateOf(0f) }
    val screenWidth = ivyWalletCtx().screenWidth

    Layout(
        modifier = Modifier
            .fillMaxSize()
            .draggable(
                orientation = Orientation.Horizontal,
                onDragStarted = { startedPosition ->
                    Timber.i("onDragStarted = $startedPosition")
                },
                state = rememberDraggableState { delta ->
                    dragDistance += delta
                    state.setOffset(-dragDistance / screenWidth)
                    val offset = state.offset.value
                    when {
                        offset >= 1f -> {
                            dragDistance = 0f
                            state.selectPage(state.currentPage + 1)
                        }
                        offset <= -1f -> {
                            dragDistance = 0f
                            state.selectPage(state.currentPage - 1)
                        }
                    }
                    Timber.i("offset = $offset (dragDistance = $dragDistance)")
                },
                onDragStopped = { veolicity ->
                    coroutineScope.launch {
                        dragDistance = 0f
                        val offset = state.offset.value
                        when {
                            offset > SWIPE_THRESHOLD -> {
                                //next page
                                Animatable(offset).animateTo(1f) {
                                    state.setOffset(value)
                                    if (value == 1f) {
                                        state.selectPage(state.currentPage + 1)
                                    }
                                }
                            }
                            offset < -SWIPE_THRESHOLD -> {
                                //previous page
                                Animatable(offset).animateTo(-1f) {
                                    state.setOffset(value)
                                    if (value == -1f) {
                                        state.selectPage(state.currentPage - 1)
                                    }
                                }
                            }
                            else -> {
                                Animatable(offset).animateTo(0f) {
                                    state.setOffset(value)
                                }
                            }
                        }
                    }

                }
            ),
        content = {
            //Pages to pre-load
            val firstVisibleIndex = (state.currentPage - 1).coerceAtLeast(state.minPage)
            val lastVisibleIndex = (state.currentPage + 1).coerceAtMost(state.maxPage)

            for (index in firstVisibleIndex..lastVisibleIndex) {
                val pageData = IvyPageData(index)
                key(pageData) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = pageData
                            .width(densityScope {
                                screenWidth.toDp()
                            })
                            .fillMaxHeight()
                    ) {
                        pageContent(index)
                    }
                }
            }
        },
        measurePolicy = { measurables, constraints ->
            val offset = state.offset.value //(-1f to 1f)
            Timber.i("measurePolicy(): offset = $offset")

            layout(screenWidth, constraints.minHeight) {
                val childConstraints = constraints.copy(minWidth = 0, minHeight = 0)

                measurables
                    .map {
                        it.measure(childConstraints) to it.page
                    }
                    .forEach { (placeable, page) ->
                        when (page) {
                            state.currentPage -> {
                                //current page
                                placeable.place(
                                    (screenWidth * -offset).roundToInt(),
                                    0
                                )
                            }
                            state.currentPage - 1 -> {
                                //previous page (offset = -1f)
                                placeable.place(
                                    (-screenWidth - (screenWidth * offset)).roundToInt(),
                                    0
                                )
                            }
                            state.currentPage + 1 -> {
                                //next page (offset = 1f)
                                val x = (screenWidth - (screenWidth * offset)).roundToInt()
                                Timber.i("nextPage: x = $x, (offset = $offset, sw = $screenWidth)")
                                placeable.place(
                                    x,
                                    0
                                )
                            }
                        }
                    }
            }
        }
    )
}

@Immutable
private data class IvyPageData(val page: Int) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?): Any = this@IvyPageData
}

private val Measurable.page: Int
    get() = (parentData as? IvyPageData)?.page ?: error("no PageData for measurable $this")

class IvyPagerState(
    currentPage: Int = 0,
    maxPage: Int = 0
) {
    val minPage = 0

    val offset: State<Float>
        get() = _offset

    private var _offset = mutableStateOf(0f)

    internal fun setOffset(offset: Float) {
        when {
            currentPage == minPage && offset < 0 -> {
                _offset.value = 0f
                return
            }
            currentPage == maxPage && offset > 0 -> {
                _offset.value = 0f
                return
            }
        }
        _offset.value = offset.coerceIn(-1f, 1f)

    }

    private var _maxPage by mutableStateOf(maxPage)
    var maxPage: Int
        get() = _maxPage
        set(value) {
            _maxPage = value.coerceAtLeast(minPage)
            _currentPage = _currentPage.coerceIn(minPage, maxPage)
        }

    private var _currentPage by mutableStateOf(currentPage.coerceIn(minPage, maxPage))
    val currentPage: Int
        get() = _currentPage

    fun selectPage(page: Int) {
        _currentPage = page.coerceIn(minPage, maxPage)
        setOffset(0f)
    }
}