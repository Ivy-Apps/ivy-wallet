package com.ivy.old.parse

import com.ivy.backup.base.optional
import com.ivy.common.time.beginningOfIvyTime
import com.ivy.common.time.provider.TimeProvider
import com.ivy.common.time.toLocal
import com.ivy.common.toUUID
import com.ivy.data.transaction.TrnTime
import org.json.JSONObject
import java.time.Instant
import java.time.LocalDateTime
import java.util.*

fun parseTrnTime(
    trnJson: JSONObject,
    timeProvider: TimeProvider,
): TrnTime {
    fun parseDateTime(field: String): LocalDateTime? =
        optional { trnJson.getLong(field) }
            ?.let { epochMillis ->
                Instant.ofEpochMilli(epochMillis)
                    .toLocal(timeProvider)
            }

    return parseDateTime("dateTime")
        ?.let(TrnTime::Actual) ?: parseDateTime("dueDate")
        ?.let(TrnTime::Due) ?: TrnTime.Actual(
        beginningOfIvyTime()
    )
}

fun JSONObject.optionalUUID(field: String): UUID? =
    optional { getString(field).toUUID() }