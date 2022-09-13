package com.ivy.core.persistence.entity.trn.converter

import androidx.room.TypeConverter
import com.ivy.core.persistence.entity.trn.TrnTimeType
import com.ivy.data.transaction.AttachmentType
import com.ivy.data.transaction.TrnPurpose
import com.ivy.data.transaction.TrnType

class TrnTypeConverters {
    companion object {
        const val NONE = 0
    }

    // region BatchPurpose
    @TypeConverter
    fun ser(purpose: TrnPurpose?): Int = purpose?.code ?: NONE

    @TypeConverter
    fun purpose(code: Int): TrnPurpose? =
        code.takeIf { it != NONE }?.let(TrnPurpose::fromCode)
    // endregion

    // region TrnType
    @TypeConverter
    fun ser(type: TrnType): Int = type.code

    @TypeConverter
    fun trnType(code: Int): TrnType = TrnType.fromCode(code)
    // endregion

    // region TrnTimeType
    @TypeConverter
    fun ser(timeType: TrnTimeType): Int = timeType.code

    @TypeConverter
    fun trnTimeType(code: Int): TrnTimeType = TrnTimeType.fromCode(code)
    // endregion

    // region AttachmentType
    @TypeConverter
    fun ser(attachmentType: AttachmentType): Int = attachmentType.code

    @TypeConverter
    fun attachmentType(code: Int): AttachmentType = AttachmentType.fromCode(code)
    // endregion
}