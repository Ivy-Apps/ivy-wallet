package com.ivy.core.domain.algorithm.trnhistory

import com.ivy.core.domain.action.SharedFlowAction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

const val UpcomingSectionKey = "sec_upcoming"
const val OverdueSectionKey = "sec_overdue"

private val collapsedTrnListKeys = MutableStateFlow(
    // Upcoming & Overdue must be collapsed by default
    setOf(
        UpcomingSectionKey,
        OverdueSectionKey
    )
)

fun toggleCollapseExpandTrnListKey(keyId: String) {
    collapsedTrnListKeys.value = collapsedTrnListKeys.value
        .toMutableSet()
        .apply {
            if (keyId in this) {
                remove(keyId)
            } else {
                add(keyId)
            }
        }
}

@Singleton
class CollapsedTrnListKeysFlow @Inject constructor() : SharedFlowAction<Set<String>>() {
    override fun initialValue(): Set<String> = setOf(
        // Upcoming & Overdue must be collapsed by default
        UpcomingSectionKey, OverdueSectionKey
    )

    override fun createFlow(): Flow<Set<String>> = collapsedTrnListKeys
}