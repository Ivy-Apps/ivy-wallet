package com.ivy.core.ui

import android.net.Uri
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import com.ivy.data.file.FileType
import com.ivy.design.util.isInPreview
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun rootView(): View = LocalView.current

@Composable
fun rootScreen(): RootScreen = if (!isInPreview())
    LocalContext.current as RootScreen else dummyRootScreen()

private fun dummyRootScreen(): RootScreen = object : RootScreen {
    override fun shareIvyWallet() {}

    override fun openUrlInBrowser(url: String) {}

    override fun openGooglePlayAppPage(appId: String) {}

    override fun reviewIvyWallet(dismissReviewCard: Boolean) {}

    override fun <T> pinWidget(widget: Class<T>) {}

    override fun shareCSVFile(fileUri: Uri) {}

    override fun fileChooser(fileType: FileType, onFileChosen: (Uri) -> Unit) {}

    override fun createFile(fileName: String, onFileCreated: (Uri) -> Unit) {}

    override fun shareZipFile(fileUri: Uri) {}

    override fun datePicker(
        minDate: LocalDate?,
        maxDate: LocalDate?,
        initialDate: LocalDate?,
        onDatePicked: (LocalDate) -> Unit
    ) {
    }

    override fun timePicker(onTimePicked: (LocalTime) -> Unit) {}
}