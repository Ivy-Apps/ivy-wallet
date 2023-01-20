package com.ivy.core.persistence.entity.trn

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ivy.core.persistence.entity.trn.data.TrnTimeType
import com.ivy.data.CurrencyCode
import com.ivy.data.SyncState
import com.ivy.data.transaction.TransactionType
import com.ivy.data.transaction.TrnPurpose
import com.ivy.data.transaction.TrnState
import java.time.Instant

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey
    @ColumnInfo(name = "id", index = true)
    val id: String,

    // region Mandatory
    @ColumnInfo(name = "accountId", index = true)
    val accountId: String,
    @ColumnInfo(name = "type", index = true)
    val type: TransactionType,
    @ColumnInfo(name = "amount")
    val amount: Double,
    @ColumnInfo(name = "currency")
    val currency: CurrencyCode,
    @ColumnInfo(name = "time", index = true)
    val time: Instant,
    /**
     * actual (happened) or due (planned)
     */
    @ColumnInfo(name = "timeType", index = true)
    val timeType: TrnTimeType,
    // endregion

    // region Optional
    @ColumnInfo(name = "title")
    val title: String?,
    @ColumnInfo(name = "description")
    val description: String?,
    @ColumnInfo(name = "categoryId", index = true)
    val categoryId: String?,
    /**
     * attachments are handled via
     * [com.ivy.core.persistence.entity.attachment.AttachmentEntity]
     */
    // endregion

    // region Metadata
    /**
     * transactions are linked together (batched) via
     * [TrnLinkRecordEntity]
     */

    /**
     * additional transaction metadata is stored in
     * [TrnMetadataEntity]
     */

    @ColumnInfo(name = "state")
    val state: TrnState,
    @ColumnInfo(name = "purpose")
    val purpose: TrnPurpose?,
    @ColumnInfo(name = "sync", index = true)
    val sync: SyncState,
    @ColumnInfo(name = "last_updated")
    val lastUpdated: Instant,
    // endregion
)