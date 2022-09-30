package com.ivy.core.ui.action.mapping

import com.ivy.core.domain.action.Action
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class MapUiAction<Domain, Ui> : Action<Domain, Ui>() {

    abstract suspend fun transform(domain: Domain): Ui

    override suspend fun Domain.willDo(): Ui = withContext(Dispatchers.Default) {
        transform(this@willDo)
    }
}