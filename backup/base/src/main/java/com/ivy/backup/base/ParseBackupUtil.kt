package com.ivy.backup.base

import arrow.core.Either
import com.ivy.common.time.beginningOfIvyTime
import com.ivy.common.time.provider.TimeProvider
import com.ivy.common.time.toLocal
import com.ivy.common.toUUID
import com.ivy.data.transaction.TrnTime
import org.json.JSONObject
import java.time.Instant
import java.time.LocalDateTime
import java.util.*


fun <T> maybe(block: () -> T): T? = try {
    block()
} catch (e: Exception) {
    null
}

fun <E : ImportBackupError.Parse, T> parseItems(
    json: JSONObject,
    key: String,
    error: (Throwable) -> E,
    parse: JSONObject.() -> T
): Either<ImportBackupError.Parse, List<T>> =
    Either.catch(error) {
        val itemsJson = json.getJSONArray(key)
        val items = mutableListOf<T>()
        for (i in 0 until itemsJson.length()) {
            val itemJson = itemsJson.getJSONObject(i)
            items.add(itemJson.parse())
        }
        items
    }


fun parseTrnTime(
    trnJson: JSONObject,
    timeProvider: TimeProvider,
): TrnTime {
    return trnJson.parseDateTime("dateTime", timeProvider)
        ?.let(TrnTime::Actual) ?: trnJson.parseDateTime("dueDate", timeProvider)
        ?.let(TrnTime::Due) ?: TrnTime.Actual(
        beginningOfIvyTime()
    )
}

fun JSONObject.parseDateTime(
    field: String,
    timeProvider: TimeProvider
): LocalDateTime? =
    maybe { getLong(field) }
        ?.let { epochMillis ->
            Instant.ofEpochMilli(epochMillis)
                .toLocal(timeProvider)
        }


fun JSONObject.optionalUUID(field: String): UUID? =
    maybe { getString(field).toUUID() }