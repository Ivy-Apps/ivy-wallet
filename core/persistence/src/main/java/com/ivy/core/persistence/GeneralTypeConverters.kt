package com.ivy.core.persistence

import androidx.room.TypeConverter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.Instant


class GeneralTypeConverters {
    // region Instant
    @TypeConverter
    fun ser(instant: Instant): Long = instant.epochSecond

    @TypeConverter
    fun instant(epochSecond: Long): Instant = Instant.ofEpochSecond(epochSecond)
    // endregion


    // region Map<String,String>
    //TODO: Make sure that this works
    @TypeConverter
    fun ser(metadata: Map<String, String>): String = Json.encodeToString(metadata)

    @TypeConverter
    fun map(json: String): Map<String, String> = Json.decodeFromString(json)
    // endregion
}