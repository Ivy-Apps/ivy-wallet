package com.ivy.data.db.entity

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ivy.base.kotlinxserilzation.KSerializerInstant
import com.ivy.base.kotlinxserilzation.KSerializerUUID
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.UUID

@Keep
@Serializable
@Entity(tableName = "tags")
data class TagEntity(
    @PrimaryKey
    @SerialName("id")
    @Serializable(with = KSerializerUUID::class)
    val id: UUID,

    @SerialName("name")
    val name: String,
    @SerialName("description")
    val description: String?,

    @SerialName("color")
    val color: Int,
    @SerialName("icon")
    val icon: String?,
    @SerialName("orderNum")
    val orderNum: Double,

    @SerialName("isDeleted")
    val isDeleted: Boolean,

    @SerialName("creationTime")
    @Serializable(with = KSerializerInstant::class)
    val dateTime: Instant,

    @SerialName("lastSyncTime")
    @Serializable(with = KSerializerInstant::class)
    val lastSyncedTime: Instant
)
