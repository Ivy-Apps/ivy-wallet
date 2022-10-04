package com.ivy.wallet.io.network

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.ivy.common.time.deviceTimeProvider
import com.ivy.common.time.toEpochSeconds
import com.ivy.common.time.toLocal
import com.ivy.wallet.io.network.error.ErrorCode
import java.time.Instant
import java.time.LocalDateTime

class LocalDateTimeTypeAdapter : TypeAdapter<LocalDateTime>() {
    override fun write(out: JsonWriter, date: LocalDateTime?) {
        date?.let {
            out.value(it.toEpochSeconds(deviceTimeProvider()))
        } ?: out.nullValue()
    }

    override fun read(jsonIn: JsonReader): LocalDateTime? {
        return try {
            val timestampSeconds = jsonIn.nextLong()
            Instant.ofEpochSecond(timestampSeconds).toLocal(deviceTimeProvider())
        } catch (e: Exception) {
            jsonIn.nextNull()
            null
        }
    }
}

class ErrorCodeTypeAdapter : TypeAdapter<ErrorCode>() {
    override fun write(out: JsonWriter, value: ErrorCode?) {
        value?.let {
            out.value(value.code)
        } ?: out.nullValue()
    }

    override fun read(jsonIn: JsonReader): ErrorCode {
        return try {
            val code = jsonIn.nextInt()
            ErrorCode.values().find { it.code == code } ?: ErrorCode.UNKNOWN
        } catch (e: Exception) {
            jsonIn.nextNull()
            ErrorCode.UNKNOWN
        }
    }

}