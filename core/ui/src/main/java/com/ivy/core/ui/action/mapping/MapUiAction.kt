package com.ivy.core.ui.action.mapping

import com.ivy.core.domain.action.Action
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class MapUiAction<Domain, Ui> : Action<Domain, Ui>() {

    abstract suspend fun transform(domain: Domain): Ui

    override suspend fun action(input: Domain): Ui = withContext(Dispatchers.Default) {
        transform(input)
    }
}