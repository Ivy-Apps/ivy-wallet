package com.ivy.core.kotlinxserilzation

import androidx.annotation.Keep
import com.ivy.core.util.epochMilliToDateTime
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDateTime

@Keep
object KSerializerLocalDateTime : KSerializer<LocalDateTime> {
    override val descriptor = PrimitiveSerialDescriptor(
        "LocalDateTime",
        PrimitiveKind.INT
    )

    override fun deserialize(decoder: Decoder): LocalDateTime {
        return decoder.decodeLong().epochMilliToDateTime()
    }

    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        encoder.encodeString(value.toString())
    }
}
