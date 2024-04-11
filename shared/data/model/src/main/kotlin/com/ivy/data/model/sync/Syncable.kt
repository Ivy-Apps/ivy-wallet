package com.ivy.data.model.sync

import java.time.Instant

interface Syncable<ID : UniqueId> {
    val id: ID
    val lastUpdated: Instant

    /**
     * Tombstone flag
     */
    val removed: Boolean
}
