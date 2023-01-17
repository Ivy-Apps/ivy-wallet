package com.ivy.core.persistence.dao.trn

import androidx.room.ColumnInfo
import com.ivy.core.persistence.entity.trn.data.TrnTimeType
import java.time.Instant

data class AccountIdAndTrnTime(
    @ColumnInfo("accountId")
    val accountId: String,
    @ColumnInfo("time")
    val time: Instant,
    @ColumnInfo("timeType")
    val timeType: TrnTimeType,
)