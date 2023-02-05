package com.ivy.core.data.sync

import java.time.LocalDateTime

interface Syncable {
    val lastUpdated: LocalDateTime
}