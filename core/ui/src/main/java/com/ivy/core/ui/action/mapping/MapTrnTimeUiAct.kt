package com.ivy.core.ui.action.mapping

import android.content.Context
import com.ivy.common.time.provider.TimeProvider
import com.ivy.core.ui.R
import com.ivy.core.ui.data.transaction.TrnTimeUi
import com.ivy.core.ui.time.formatNicely
import com.ivy.data.transaction.TrnTime
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDateTime
import javax.inject.Inject

class MapTrnTimeUiAct @Inject constructor(
    @ApplicationContext
    private val appContext: Context,
    private val timeProvider: TimeProvider
) : MapUiAction<TrnTime, TrnTimeUi>() {
    override suspend fun transform(domain: TrnTime): TrnTimeUi = mapTrnTimeUi(domain)

    private fun mapTrnTimeUi(domain: TrnTime): TrnTimeUi {
        fun formatTime(time: LocalDateTime): String =
            time.formatNicely(context = appContext, includeWeekDay = true)

        return when (domain) {
            is TrnTime.Actual -> TrnTimeUi.Actual(
                actual = formatTime(domain.actual).uppercase()
            )
            is TrnTime.Due -> TrnTimeUi.Due(
                dueOn = appContext.getString(
                    R.string.due_on, formatTime(domain.due)
                ).uppercase(),
                upcoming = timeProvider.timeNow().isBefore(domain.due)
            )
        }
    }
}