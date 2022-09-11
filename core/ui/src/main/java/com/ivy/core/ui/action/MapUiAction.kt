package com.ivy.core.ui.action

import com.ivy.frp.action.Action
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class MapUiAction<Domain, Ui> : Action<Domain, Ui>() {

    abstract fun transform(domain: Domain): Ui

    override suspend fun Domain.willDo(): Ui = withContext(Dispatchers.Default) {
        transform(this@willDo)
    }
}