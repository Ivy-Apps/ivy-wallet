package com.ivy.core.persistence.entity.attachment.converter

import androidx.room.TypeConverter
import com.ivy.data.attachment.AttachmentSource
import com.ivy.data.attachment.AttachmentType

class AttachmentTypeConverters {

    // region AttachmentType
    @TypeConverter
    fun ser(attachmentType: AttachmentType?): Int? = attachmentType?.code

    @TypeConverter
    fun attachmentType(code: Int?): AttachmentType? = code?.let(AttachmentType::fromCode)
    // endregion

    // region AttachmentSource
    @TypeConverter
    fun ser(source: AttachmentSource): Int = source.code

    @TypeConverter
    fun attachmentSource(code: Int): AttachmentSource = AttachmentSource.fromCode(code)!!
    // endregion
}