package com.ivy.wallet.model.bankintegrations

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

@Entity(tableName = "se_connections")
data class SEConnection(
    @SerializedName("id")
    @PrimaryKey @ColumnInfo(name = "id")
    val id: String = "",

    @SerializedName("provider_code")
    @ColumnInfo(name = "provider_code")
    val provider_code: String = "",

    @SerializedName("provider_name")
    @ColumnInfo(name = "provider_name")
    val provider_name: String = "",

    @SerializedName("customer_id")
    @ColumnInfo(name = "customer_id")
    val customer_id: String = "",

    @SerializedName("next_refresh_possible_at")
    @ColumnInfo(name = "next_refresh_possible_at")
    var next_refresh_possible_at: LocalDateTime? = null,
)