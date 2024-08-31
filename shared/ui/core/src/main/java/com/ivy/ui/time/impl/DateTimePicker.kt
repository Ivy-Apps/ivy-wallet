package com.ivy.ui.time.impl

import androidx.compose.runtime.Composable
import androidx.fragment.app.FragmentActivity
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime

interface DateTimePicker {
    @Composable
    fun Content()

    fun pickDate(
        initialDate: Instant?,
        onDatePick: (LocalDate) -> Unit
    )
    fun pickTime(
        initialTime: LocalTime?,
        onTimePick: (LocalTime) -> Unit
    )
}