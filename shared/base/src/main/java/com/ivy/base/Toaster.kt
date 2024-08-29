package com.ivy.base

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import com.ivy.base.resource.ResourceProvider
import com.ivy.base.threading.DispatchersProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.withContext
import javax.inject.Inject

class Toaster @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val resourceProvider: ResourceProvider,
    private val dispatchers: DispatchersProvider,
) {
    suspend fun show(@StringRes messageId: Int, duration: Int = Toast.LENGTH_LONG) {
        show(resourceProvider.getString(messageId), duration)
    }

    suspend fun show(message: String, duration: Int = Toast.LENGTH_LONG) {
        withContext(dispatchers.main) {
            Toast.makeText(context, message, duration).show()
        }
    }
}