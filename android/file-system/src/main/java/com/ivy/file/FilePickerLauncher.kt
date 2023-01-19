package com.ivy.file

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.ivy.android.common.ActivityLauncher
import com.ivy.data.file.FileType
import javax.inject.Inject

class FilePickerLauncher @Inject constructor(
) : ActivityLauncher<FileType, Uri?>() {
    override fun intent(context: Context, input: FileType): Intent = Intent(
        Intent.ACTION_OPEN_DOCUMENT
    ).apply {
        addCategory(Intent.CATEGORY_OPENABLE)
        type = when (input) {
            FileType.Everything -> "*/*"
            FileType.Zip -> "application/zip"
            FileType.CSV -> "application/csv"
        }
    }

    override fun onActivityResult(resultCode: Int, intent: Intent?): Uri? = intent?.data
}
