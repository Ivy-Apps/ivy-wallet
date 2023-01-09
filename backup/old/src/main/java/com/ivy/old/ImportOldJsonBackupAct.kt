package com.ivy.old

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import arrow.core.Either
import arrow.core.computations.either
import arrow.core.left
import arrow.core.right
import com.ivy.base.readFile
import com.ivy.base.unzip
import com.ivy.core.domain.action.Action
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.io.File
import javax.inject.Inject

@Suppress("IMPLICIT_NOTHING_TYPE_ARGUMENT_IN_RETURN_POSITION")
class ImportOldJsonBackupAct @Inject constructor(
    @ApplicationContext
    private val context: Context
) : Action<Uri, Either<String, String>>() {
    override suspend fun Uri.willDo(): Either<String, String> = either.eager {
        // regin Unzip & read
        val folderName = "backup" + System.currentTimeMillis()
        val cacheFolderPath = File(context.cacheDir, folderName)

        unzip(
            context = context,
            zipFile = this@willDo,
            location = cacheFolderPath
        )

        val filesArray = cacheFolderPath.listFiles()

        if (filesArray == null || filesArray.isEmpty())
            "No files found in the backup".left().bind()

        val filesList = filesArray.toList().filter {
            hasJsonExtension(it)
        }

        if (filesList.size != 1)
            "failed!".left().bind()

        val jsonString = readFile(context, filesList.first().toUri(), Charsets.UTF_16)
        // endregion

        Timber.d("BACKUP_JSON: $jsonString")

        "Success".right().bind()
    }

    private fun hasJsonExtension(file: File): Boolean {
        val name = file.name
        val lastIndexOf = name.lastIndexOf(".")
        if (lastIndexOf == -1)
            return false

        return (name.substring(lastIndexOf).equals(".json", true))
    }

    private fun clearCacheDir(context: Context) {
        context.cacheDir.deleteRecursively()
    }
}