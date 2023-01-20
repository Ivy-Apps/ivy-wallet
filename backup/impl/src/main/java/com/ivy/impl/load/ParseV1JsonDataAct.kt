package com.ivy.impl.load

import arrow.core.Either
import arrow.core.computations.either
import com.ivy.backup.base.ImportBackupError
import com.ivy.backup.base.data.BackupData
import com.ivy.common.time.provider.TimeProvider
import com.ivy.core.domain.action.Action
import kotlinx.coroutines.Dispatchers
import org.json.JSONObject
import javax.inject.Inject

class ParseV1JsonDataAct @Inject constructor(
    private val timeProvider: TimeProvider,
) : Action<JSONObject, Either<ImportBackupError, BackupData>>() {
    override fun dispatcher() = Dispatchers.Default

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override suspend fun action(json: JSONObject): Either<ImportBackupError, BackupData> =
        either {
            val now = timeProvider.timeNow()
            TODO()
        }

}