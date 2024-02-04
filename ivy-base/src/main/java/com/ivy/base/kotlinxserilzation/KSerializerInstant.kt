package com.ivy.base.kotlinxserilzation

import androidx.annotation.Keep
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Instant

@Keep
object KSerializerInstant : KSerializer<Instant> {
    override val descriptor = PrimitiveSerialDescriptor(
        "Instant",
        PrimitiveKind.LONG
    )

    override fun deserialize(decoder: Decoder): Instant {
        return Instant.ofEpochMilli(decoder.decodeLong())
    }

    override fun serialize(encoder: Encoder, value: Instant) {
        encoder.encodeLong(value.toEpochMilli())
    }
}
