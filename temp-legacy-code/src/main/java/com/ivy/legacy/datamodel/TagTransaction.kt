package com.ivy.legacy.datamodel

import com.ivy.data.db.entity.TagTransactionEntity
import java.util.*


data class TagTransaction(
    val tagId: UUID,
    val associatedId: UUID,
    val isSynced: Boolean = false,
    val isDeleted: Boolean = false
) {
    fun toEntity(): TagTransactionEntity {
        return TagTransactionEntity(tagId, associatedId, isSynced, isDeleted)
    }
}