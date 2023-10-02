package com.ivy.data.model.sync

import java.time.Instant

interface Syncable {
    val id: UniqueId
    val lastUpdated: Instant

    /**
     * Tombstone flag
     */
    val removed: Boolean
}