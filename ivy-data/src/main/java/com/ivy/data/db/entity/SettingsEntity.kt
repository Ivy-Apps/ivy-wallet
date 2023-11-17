package com.ivy.data.db.entity

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ivy.base.kotlinxserilzation.KSerializerUUID
import com.ivy.base.legacy.Theme
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*

@Keep
@Serializable
@Entity(tableName = "settings")
data class SettingsEntity(
    @SerialName("theme")
    val theme: Theme,
    @SerialName("currency")
    val currency: String,
    @SerialName("bufferAmount")
    val bufferAmount: Double,
    @SerialName("name")
    val name: String,

    @SerialName("isSynced")
    val isSynced: Boolean = false,
    @SerialName("isDeleted")
    val isDeleted: Boolean = false,

    @PrimaryKey
    @SerialName("id")
    @Serializable(with = KSerializerUUID::class)
    val id: UUID = UUID.randomUUID()
)
