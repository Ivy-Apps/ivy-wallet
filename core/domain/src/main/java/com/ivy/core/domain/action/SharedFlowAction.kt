package com.ivy.core.domain.action

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

/**
 * Creates a singleton flow which caches the last produced value.
 * Use it when you want to **execute the computation only once** for multiple collectors.
 * ## ATTENTION: For this to work, annotate the Action that extends it with "@Singleton".
 * Don't forget to annotate your class with [javax.inject.Singleton].
 *
 * By default the created flow will start eagerly (immediately). To change that behavior
 * override [startType]. When the flow is started will return [initialValue].
 */
abstract class SharedFlowAction<T> {
    private var cachedFlow: StateFlow<T>? = null

    /**
     * @return this initial value immediately after the flow is started.
     */
    protected abstract fun initialValue(): T

    protected abstract fun createFlow(): Flow<T>

    /**
     * @return the mode in which the created flow will start.
     * By default [SharingStarted.Eagerly] immediately after creation (even with no collectors)
     * and you change that to [SharingStarted.Lazily].
     */
    open fun startType(): SharingStarted = SharingStarted.Eagerly

    operator fun invoke(): Flow<T> = cachedFlow ?: run {
        val flowInstance = createFlow()
            .stateIn(
                scope = MainScope(),
                started = startType(),
                initialValue = initialValue()
            )
        cachedFlow = flowInstance
        flowInstance
    }
}