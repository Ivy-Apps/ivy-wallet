package com.ivy.core.domain.action.calculate.transaction

import com.ivy.core.domain.action.SharedFlowAction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

const val UpcomingSectionKey = "section_upcoming"
const val OverdueSectionKey = "section_overdue"

private val collapsedTrnListKeys = MutableStateFlow(emptySet<String>())

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
        UpcomingSectionKey, OverdueSectionKey
    )

    override fun createFlow(): Flow<Set<String>> = collapsedTrnListKeys
}