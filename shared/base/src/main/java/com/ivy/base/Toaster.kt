package com.ivy.base

import android.content.Context
import android.widget.Toast
import dagger.hilt.android.qualifiers.ApplicationContext

class Toaster(
    @ApplicationContext private val context: Context,
) {
    fun show(messageId: Int, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, context.getString(messageId), duration).show()
    }
}