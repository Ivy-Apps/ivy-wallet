package com.ivy.core.domain.action.settings

import com.ivy.wallet.io.persistence.dao.SettingsDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NameFlow @Inject constructor(
    private val settingsDao: SettingsDao
) : com.ivy.core.domain.action.FlowAction<Unit, String>() {
    override fun Unit.createFlow(): Flow<String> =
        settingsDao.findFirst().map {
            it.name
        }
}