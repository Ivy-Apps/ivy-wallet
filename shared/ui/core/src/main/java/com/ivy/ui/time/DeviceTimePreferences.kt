package com.ivy.ui.time

import java.util.Locale

interface DeviceTimePreferences {
    fun is24HourFormat(): Boolean
    fun locale(): Locale
}