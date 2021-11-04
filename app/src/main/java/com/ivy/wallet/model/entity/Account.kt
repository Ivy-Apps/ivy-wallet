package com.ivy.wallet.model.entity

import androidx.compose.ui.graphics.toArgb
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ivy.wallet.ui.theme.Green
import java.util.*

@Entity(tableName = "accounts")
data class Account(
    val name: String,
    val currency: String? = null,
    val color: Int = Green.toArgb(),
    val icon: String? = null,
    val orderNum: Double = 0.0,
    val includeInBalance: Boolean = true,

    //SaltEdge integration -------
    val seAccountId: String? = null,
    //SaltEdge integration -------

    val isSynced: Boolean = false,
    val isDeleted: Boolean = false,

    @PrimaryKey
    val id: UUID = UUID.randomUUID()
)