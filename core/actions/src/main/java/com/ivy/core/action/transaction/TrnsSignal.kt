package com.ivy.core.action.transaction

import com.ivy.core.action.Signal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrnsSignal @Inject constructor() : Signal<Unit>() {
    override fun initialSignal() = Unit
}