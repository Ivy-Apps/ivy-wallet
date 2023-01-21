package com.ivy.backup.base

import arrow.core.Either
import org.json.JSONObject
import java.time.LocalDateTime

fun <E : ImportBackupError.Parse, T> parseItems(
    json: JSONObject,
    now: LocalDateTime,
    key: String,
    error: (Throwable) -> E,
    parse: JSONObject.(now: LocalDateTime) -> T
): Either<ImportBackupError, List<T>> =
    Either.catch(ImportBackupError.Parse::Categories) {
        val itemsJson = json.getJSONArray(key)
        val items = mutableListOf<T>()
        for (i in 0 until itemsJson.length()) {
            val itemJson = itemsJson.getJSONObject(i)
            items.add(itemJson.parse(now))
        }
        items
    }
