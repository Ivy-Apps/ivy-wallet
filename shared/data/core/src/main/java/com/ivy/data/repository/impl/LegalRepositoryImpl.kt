package com.ivy.data.repository.impl

import com.ivy.base.threading.DispatchersProvider
import com.ivy.data.datasource.LocalLegalDataSource
import com.ivy.data.repository.LegalRepository
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LegalRepositoryImpl @Inject constructor(
    private val localLegalDataSource: LocalLegalDataSource,
    private val dispatchers: DispatchersProvider
) : LegalRepository {
    override suspend fun isDisclaimerAccepted(): Boolean = withContext(dispatchers.io) {
        localLegalDataSource.getIsDisclaimerAccepted() ?: false
    }

    override suspend fun setDisclaimerAccepted(
        accepted: Boolean
    ): Unit = withContext(dispatchers.io) {
        localLegalDataSource.setDisclaimerAccepted(accepted)
    }
}