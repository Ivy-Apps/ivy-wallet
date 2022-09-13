package com.ivy.core.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ivy.core.persistence.dao.TrnAttachmentDao
import com.ivy.core.persistence.dao.TrnDao
import com.ivy.core.persistence.dao.TrnLinkRecordDao
import com.ivy.core.persistence.dao.TrnMetadataDao
import com.ivy.core.persistence.entity.trn.TrnEntity
import com.ivy.core.persistence.entity.trn.TrnLinkRecordEntity
import com.ivy.core.persistence.entity.trn.TrnMetadataEntity
import com.ivy.core.persistence.entity.trn.attachment.TrnAttachmentEntity
import com.ivy.core.persistence.entity.trn.converter.TrnTypeConverters

@Database(
    entities = [
        TrnEntity::class, TrnLinkRecordEntity::class,
        TrnMetadataEntity::class, TrnAttachmentEntity::class
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(GeneralTypeConverters::class, TrnTypeConverters::class)
abstract class IvyWalletDb : RoomDatabase() {
    abstract fun trnDao(): TrnDao

    abstract fun trnLinkRecordDao(): TrnLinkRecordDao

    abstract fun trnMetadataDao(): TrnMetadataDao

    abstract fun trnAttachmentDao(): TrnAttachmentDao
}