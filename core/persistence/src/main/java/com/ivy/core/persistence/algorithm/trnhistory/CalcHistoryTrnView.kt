package com.ivy.core.persistence.algorithm.trnhistory

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import com.ivy.core.persistence.entity.trn.data.TrnTimeType
import com.ivy.data.CurrencyCode
import com.ivy.data.DELETING
import com.ivy.data.transaction.TransactionType
import com.ivy.data.transaction.TrnPurpose
import com.ivy.data.transaction.TrnStateHidden
import java.time.Instant

@DatabaseView(
    "SELECT amount, currency, type, time," +
            "transactions.id, accountId, categoryId, title, description, timeType," +
            "purpose, trn_links.batchId" +
            " FROM transactions" +
            " LEFT JOIN trn_links ON trn_links.trnId = transactions.id" +
            " WHERE transactions.state != $TrnStateHidden AND transactions.sync != $DELETING",
    viewName = "CalcHistoryTrnView"
)
data class CalcHistoryTrnView(
    // region CalcTrn
    @ColumnInfo(name = "amount")
    val amount: Double,
    @ColumnInfo(name = "currency")
    val currency: CurrencyCode,
    @ColumnInfo(name = "type")
    val type: TransactionType,
    @ColumnInfo(name = "time")
    val time: Instant,
    // endregion

    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "accountId")
    val accountId: String,
    @ColumnInfo(name = "categoryId")
    val categoryId: String?,
    @ColumnInfo(name = "title")
    val title: String?,
    @ColumnInfo(name = "description")
    val description: String?,
    @ColumnInfo(name = "timeType")
    val timeType: TrnTimeType,
    @ColumnInfo(name = "purpose")
    val purpose: TrnPurpose?,

    // from "trn_links"
    @ColumnInfo(name = "batchId")
    val batchId: String?
)