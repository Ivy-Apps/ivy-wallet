package com.ivy.core.domain.action.transaction

import com.ivy.core.domain.action.SignalFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Notifies of new or modified transactions.
 * Called whenever a transaction is written via [WriteTrnsAct]
 */
@Singleton
class TrnsSignal @Inject constructor() : SignalFlow<Unit>() {
    override fun initialSignal() = Unit
}