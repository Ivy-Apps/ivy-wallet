package com.ivy.design.util

import android.os.Handler
import android.os.Looper

fun postDelayed(delayMs: Long, run: () -> Unit) {
    Handler(Looper.getMainLooper()).postDelayed({ run() }, delayMs)
}

fun post(run: () -> Unit) {
    Handler(Looper.getMainLooper()).post { run() }
}