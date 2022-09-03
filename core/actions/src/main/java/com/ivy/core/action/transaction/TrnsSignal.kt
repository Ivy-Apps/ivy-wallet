package com.ivy.core.action.transaction

import com.ivy.core.action.SignalFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrnsSignal @Inject constructor() : SignalFlow<Unit>() {
    override fun initialSignal() = Unit
}