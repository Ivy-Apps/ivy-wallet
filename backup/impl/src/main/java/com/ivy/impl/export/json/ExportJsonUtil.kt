package com.ivy.impl.export.json

import com.ivy.data.SyncState
import org.json.JSONArray
import org.json.JSONObject
import java.time.Instant

internal suspend fun <T> exportJson(
    findAll: suspend () -> List<T>,
    json: JSONObject.(T) -> Unit,
): JSONArray {
    val jsonArr = JSONArray()
    findAll().forEach {
        jsonArr.put(JSONObject().apply { json(it) })
    }
    return jsonArr
}

internal fun JSONObject.putSync(
    syncState: SyncState,
    lastUpdated: Instant
) {
    put("syncState", syncState.code)
    put("lastUpdated", lastUpdated.epochSecond)
}