package com.ivy.core.data.common

import java.time.LocalDateTime

interface Syncable {
    val lastUpdated: LocalDateTime
}