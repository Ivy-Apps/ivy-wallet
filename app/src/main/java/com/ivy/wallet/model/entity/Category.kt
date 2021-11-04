package com.ivy.wallet.model.entity

import androidx.compose.ui.graphics.toArgb
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ivy.wallet.ui.theme.Ivy
import java.util.*

@Entity(tableName = "categories")
data class Category(
    val name: String,
    val color: Int = Ivy.toArgb(),
    val icon: String? = null,
    val orderNum: Double = 0.0,

    //SaltEdge integration -------
    val seCategoryName: String? = null,
    //SaltEdge integration -------

    val isSynced: Boolean = false,
    val isDeleted: Boolean = false,

    @PrimaryKey
    val id: UUID = UUID.randomUUID()
)