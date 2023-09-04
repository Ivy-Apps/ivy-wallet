package com.ivy.wallet.io.persistence.data

import androidx.annotation.Keep
import androidx.compose.ui.graphics.toArgb
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.ivy.wallet.domain.data.core.Category
import com.ivy.wallet.ui.theme.Ivy
import java.util.*

@Keep
@Entity(tableName = "categories")
data class CategoryEntity(
    @SerializedName("name")
    val name: String,
    @SerializedName("color")
    val color: Int = Ivy.toArgb(),
    @SerializedName("icon")
    val icon: String? = null,
    @SerializedName("orderNum")
    val orderNum: Double = 0.0,

    @SerializedName("isSynced")
    val isSynced: Boolean = false,
    @SerializedName("isDeleted")
    val isDeleted: Boolean = false,

    @PrimaryKey
    @SerializedName("id")
    val id: UUID = UUID.randomUUID()
) {
    fun toDomain(): Category = Category(
        name = name,
        color = color,
        icon = icon,
        orderNum = orderNum,
        isSynced = isSynced,
        isDeleted = isDeleted,
        id = id
    )
}
