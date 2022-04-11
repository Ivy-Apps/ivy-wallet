package com.ivy.wallet.domain.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ivy.design.l0_system.Theme
import java.util.*

@Entity(tableName = "settings")
data class Settings(
    val theme: Theme,
    val currency: String,
    val bufferAmount: Double,
    val name: String,

    val isSynced: Boolean = false,
    val isDeleted: Boolean = false,

    @PrimaryKey
    val id: UUID = UUID.randomUUID()
)