package com.ivy.ui.time.impl

import android.content.Context
import android.text.format.DateFormat
import com.ivy.ui.time.DeviceTimePreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AndroidDeviceTimePreferences @Inject constructor(
    @ApplicationContext
    private val context: Context,
) : DeviceTimePreferences {
    override fun is24HourFormat(): Boolean = DateFormat.is24HourFormat(context)
}