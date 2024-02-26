package com.ivy.data.db.dao.fake

import com.ivy.data.db.dao.read.SettingsDao
import com.ivy.data.db.dao.write.WriteSettingsDao
import com.ivy.data.db.entity.SettingsEntity
import org.jetbrains.annotations.VisibleForTesting
import java.util.UUID

@VisibleForTesting
class FakeSettingsDao : SettingsDao, WriteSettingsDao {
    private val items = mutableListOf<SettingsEntity>()

    override suspend fun findFirst(): SettingsEntity {
        return items.first()
    }

    override suspend fun findFirstOrNull(): SettingsEntity? {
        return items.firstOrNull()
    }

    override suspend fun findAll(): List<SettingsEntity> {
        return items
    }

    override suspend fun findById(id: UUID): SettingsEntity? {
        return items.firstOrNull { it.id == id }
    }

    override suspend fun save(value: SettingsEntity) {
        items.add(value)
    }

    override suspend fun saveMany(value: List<SettingsEntity>) {
        items.addAll(value)
    }

    override suspend fun deleteById(id: UUID) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAll() {
        items.clear()
    }
}