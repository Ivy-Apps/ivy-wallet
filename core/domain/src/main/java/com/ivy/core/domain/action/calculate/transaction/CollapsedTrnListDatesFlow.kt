package com.ivy.core.domain.action.calculate.transaction

import com.ivy.core.domain.action.SharedFlowAction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

private val collapsedTrnListDates = MutableStateFlow(emptySet<String>())

fun toggleTrnListDate(dateId: String) {
    collapsedTrnListDates.value = collapsedTrnListDates.value
        .toMutableSet()
        .apply {
            if (dateId in this) {
                remove(dateId)
            } else {
                add(dateId)
            }
        }
}

@Singleton
class CollapsedTrnListDatesFlow @Inject constructor() : SharedFlowAction<Set<String>>() {
    override fun initialValue(): Set<String> = emptySet()

    override fun createFlow(): Flow<Set<String>> = collapsedTrnListDates
}