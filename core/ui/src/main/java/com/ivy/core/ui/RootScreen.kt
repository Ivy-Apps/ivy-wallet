package com.ivy.core.ui

import android.net.Uri
import com.ivy.data.file.FileType
import java.time.LocalDate
import java.time.LocalTime

interface RootScreen {
    fun shareIvyWallet()

    fun openUrlInBrowser(url: String)

    fun openGooglePlayAppPage(appId: String)

    fun reviewIvyWallet(dismissReviewCard: Boolean)

    fun <T> pinWidget(widget: Class<T>)

    fun shareCSVFile(fileUri: Uri)

    fun fileChooser(fileType: FileType = FileType.Everything, onFileChosen: (Uri) -> Unit)

    fun createFile(fileName: String, onFileCreated: (Uri) -> Unit)

    fun shareZipFile(fileUri: Uri)

    fun datePicker(
        minDate: LocalDate?,
        maxDate: LocalDate?,
        initialDate: LocalDate?,
        onDatePicked: (LocalDate) -> Unit
    )

    fun timePicker(onTimePicked: (LocalTime) -> Unit)
}