package com.ivy.core.persistence.entity.trn

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ivy.data.CurrencyCode
import com.ivy.data.transaction.TrnPurpose
import com.ivy.data.transaction.TrnType
import java.time.Instant

@Entity(tableName = "transactions_v2")
data class TrnEntity(
    @PrimaryKey
    val id: String,

    // region Mandatory
    val accountId: String,
    val type: TrnType,
    val amount: Double,
    val currency: CurrencyCode,
    val dateTime: Instant,
    val dateTimeType: TrnTimeType,
    // endregion

    // region Optional
    val title: String?,
    val description: String?,
    val categoryId: String?,
    val tags: TrnTags?,
    val attachmentUrl: String?,
    // endregion


    // region Metadata
    val purpose: TrnPurpose?,
    val metadata: Map<String, String>,
    val isSynced: Boolean,
    val isDeleted: Boolean,
    // endregion
)