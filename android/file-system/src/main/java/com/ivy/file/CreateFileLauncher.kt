package com.ivy.file

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import com.ivy.android.common.ActivityLauncher
import javax.inject.Inject

typealias FileName = String

class CreateFileLauncher @Inject constructor() : ActivityLauncher<FileName, Uri?>() {
    override fun intent(context: Context, input: FileName): Intent {
        return Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/csv"
            putExtra(Intent.EXTRA_TITLE, input)

            // Optionally, specify a URI for the directory that should be opened in
            // the system file picker before your app creates the document.
            putExtra(
                DocumentsContract.EXTRA_INITIAL_URI,
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    .toURI()
            )
        }
    }

    override fun onActivityResult(resultCode: Int, intent: Intent?): Uri? = intent?.data
}