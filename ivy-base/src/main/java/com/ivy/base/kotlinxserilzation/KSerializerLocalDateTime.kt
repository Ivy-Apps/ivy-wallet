package com.ivy.base.kotlinxserilzation

import androidx.annotation.Keep
import com.ivy.base.legacy.epochMilliToDateTime
import com.ivy.base.legacy.toEpochMilli
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDateTime

// TODO: Migrate to Instant
@Keep
object KSerializerLocalDateTime : KSerializer<LocalDateTime> {
    override val descriptor = PrimitiveSerialDescriptor(
        "LocalDateTime",
        PrimitiveKind.LONG
    )

    override fun deserialize(decoder: Decoder): LocalDateTime {
        return decoder.decodeLong().epochMilliToDateTime()
    }

    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        encoder.encodeLong(value.toEpochMilli())
    }
}
