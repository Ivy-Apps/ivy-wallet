package com.ivy.wallet.network

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.ivy.wallet.base.epochSecondToDateTime
import com.ivy.wallet.base.toEpochSeconds
import com.ivy.wallet.network.error.ErrorCode
import java.time.LocalDateTime

class LocalDateTimeTypeAdapter : TypeAdapter<LocalDateTime>() {
    override fun write(out: JsonWriter, date: LocalDateTime?) {
        date?.let {
            out.value(it.toEpochSeconds())
        } ?: out.nullValue()
    }

    override fun read(jsonIn: JsonReader): LocalDateTime? {
        return try {
            val timestampSeconds = jsonIn.nextLong()
            timestampSeconds.epochSecondToDateTime()
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