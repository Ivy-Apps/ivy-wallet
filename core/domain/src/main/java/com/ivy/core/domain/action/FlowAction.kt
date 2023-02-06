package com.ivy.core.domain.action

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * Creates a flow that doesn't cache values and **executes the computation every time**
 * for every terminal operator (collector).
 * If you want to execute the computation only once for multiple subscribers use [SharedFlowAction].
 *
 * By default the created flow will NOT emit only distinct values.
 * To change that behavior override [emitDistinctValues].
 */
abstract class FlowAction<I, T> {
    protected abstract fun createFlow(input: I): Flow<T>

    /**
     * @return true if you want to emit only distinct (different from the last emitted) values.
     * By default will NOT emit only distinct values, to change that return true.
     */
    protected open fun emitDistinctValues(): Boolean = false

    operator fun invoke(input: I): Flow<T> {
        val flow = createFlow(input)
        return if (emitDistinctValues()) flow.distinctUntilChanged() else flow
    }
}

