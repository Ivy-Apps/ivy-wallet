package com.ivy.core.data.sync

import java.time.LocalDateTime

interface Syncable {
    val id: UniqueId
    val lastUpdated: LocalDateTime

    /**
     * Tombstone flag
     */
    val removed: Boolean
}