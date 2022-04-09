package com.ivy.wallet.utils

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext

fun <T> MutableLiveData<T>.asLiveData(): LiveData<T> {
    return this
}

fun <T> MutableStateFlow<T>.readOnly(): StateFlow<T> {
    return this
}

fun Fragment.args(putArgs: Bundle.() -> Unit): Fragment {
    arguments = Bundle().apply { putArgs() }
    return this
}

fun Fragment.stringArg(key: String): String? {
    return arguments?.getString(key, null)
}

suspend fun <T> ioThread(action: suspend () -> T): T = withContext(Dispatchers.IO) {
    return@withContext action()
}

suspend fun <T> scopedIOThread(action: suspend (scope: CoroutineScope) -> T): T =
    withContext(Dispatchers.IO) {
        return@withContext action(this)
    }

suspend fun <T> computationThread(action: suspend () -> T): T = withContext(Dispatchers.Default) {
    return@withContext action()
}

suspend fun <T> uiThread(action: suspend () -> T): T = withContext(Dispatchers.Main) {
    return@withContext action()
}