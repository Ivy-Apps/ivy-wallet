package com.ivy.core.persistence

import androidx.room.TypeConverter
import com.ivy.data.SyncState
import java.time.Instant


class GeneralTypeConverters {
    // region Instant
    @TypeConverter
    fun ser(instant: Instant): Long = instant.epochSecond

    @TypeConverter
    fun instant(epochSecond: Long): Instant = Instant.ofEpochSecond(epochSecond)
    // endregion

    // region SyncState
    @TypeConverter
    fun ser(syncState: SyncState): Int = syncState.code

    @TypeConverter
    fun syncState(code: Int): SyncState = SyncState.fromCode(code) ?: SyncState.Syncing
    // endregion
}