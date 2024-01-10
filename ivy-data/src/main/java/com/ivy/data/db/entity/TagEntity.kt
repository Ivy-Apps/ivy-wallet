package com.ivy.data.db.entity

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ivy.base.kotlinxserilzation.KSerializerLocalDateTime
import com.ivy.base.kotlinxserilzation.KSerializerUUID
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.*

@Keep
@Serializable
@Entity(tableName = "tags")
data class TagEntity(
    @PrimaryKey
    @SerialName("id")
    @Serializable(with = KSerializerUUID::class)
    val id: UUID = UUID.randomUUID(),

    @SerialName("name")
    val name: String,
    @SerialName("description")
    val description: String? = null,

    @SerialName("color")
    val color: Int,
    @SerialName("icon")
    val icon: String? = null,
    @SerialName("orderNum")
    val orderNum: Double = 0.0,

    @SerialName("isSynced")
    val isSynced: Boolean = false,
    @SerialName("isDeleted")
    val isDeleted: Boolean = false,

    @SerialName("creationTime")
    @Serializable(with = KSerializerLocalDateTime::class)
    val dateTime: LocalDateTime? = null,

    //Optional bit manipulation fields to support future features
    @SerialName("featureBit")
    val featureBit:Int,
    @SerialName("featureBitValue")
    val featureBitValue:String? = null
)
