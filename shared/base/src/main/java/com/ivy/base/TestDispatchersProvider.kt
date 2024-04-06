package com.ivy.base

import com.ivy.base.threading.DispatchersProvider
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.jetbrains.annotations.VisibleForTesting
import kotlin.coroutines.CoroutineContext

@VisibleForTesting
object TestDispatchersProvider : DispatchersProvider {
    override val main: CoroutineContext = Dispatchers.Unconfined
    override val io: CoroutineContext = Dispatchers.Unconfined
    override val default: CoroutineContext = Dispatchers.Unconfined
}

@VisibleForTesting
val TestCoroutineScope = CoroutineScope(
    Dispatchers.Unconfined + CoroutineName("test")
)