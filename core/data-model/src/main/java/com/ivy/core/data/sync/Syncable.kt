package com.ivy.core.data.sync

import java.time.LocalDateTime
import java.util.*

interface Syncable {
    val id: UUID
    val lastUpdated: LocalDateTime

    /**
     * Tombstone flag
     */
    val removed: Boolean
}