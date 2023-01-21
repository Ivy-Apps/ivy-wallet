package com.ivy.impl.load

import com.ivy.backup.base.parseDateTime
import com.ivy.common.time.provider.TimeProvider
import com.ivy.data.Sync
import com.ivy.data.SyncState
import org.json.JSONObject
import java.time.LocalDateTime

internal fun JSONObject.parseSync(
    timeProvider: TimeProvider
): Sync = Sync(
    state = getInt("syncState").let(SyncState::fromCode) ?: SyncState.Syncing,
    lastUpdated = parseLastUpdated(timeProvider)
)

internal fun JSONObject.parseLastUpdated(
    timeProvider: TimeProvider
): LocalDateTime =
    parseDateTime("lastUpdated", timeProvider) ?: timeProvider.timeNow()