package com.ivy.impl.export.json

import org.json.JSONArray
import org.json.JSONObject
import java.time.Instant

internal suspend fun <T> exportJson(
    findAll: suspend () -> List<T>,
    toJson: JSONObject.(T) -> Unit,
): JSONArray {
    val json = JSONArray()
    findAll().forEach {
        json.put(JSONObject().toJson(it))
    }
    return json
}

internal fun JSONObject.putLastUpdated(
    instant: Instant
) {
    put("lastUpdated", instant.epochSecond)
}