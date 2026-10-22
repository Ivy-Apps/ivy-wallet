package com.ivy.widget.transaction

import android.app.PendingIntent
import android.content.Context
import android.widget.RemoteViews
import androidx.annotation.IdRes
import com.ivy.base.model.TransactionType
import com.ivy.domain.AppStarter
import javax.inject.Inject

class AddTransactionWidgetClick @Inject constructor(
    private val appStarter: AppStarter
) {
    // Opens RootActivity directly: since Android 15, a broadcast that then
    // starts an activity (a trampoline) may be blocked as a background launch.
    fun clickListener(
        context: Context,
        rv: RemoteViews,
        @IdRes viewId: Int,
        type: TransactionType,
    ) {
        val pendingIntent = PendingIntent.getActivity(
            context,
            type.ordinal,
            appStarter.addTransactionStartIntent(type),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        rv.setOnClickPendingIntent(viewId, pendingIntent)
    }
}
