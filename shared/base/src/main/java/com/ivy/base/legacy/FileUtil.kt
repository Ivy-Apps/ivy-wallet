package com.ivy.base.legacy

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.OpenableColumns
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

@Deprecated("useless")
fun saveFile(
    context: Context,
    directoryType: String,
    fileName: String,
    content: String
) {
    val dirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        ?: return
    val newFile = File("$dirPath/$fileName")
    newFile.createNewFile()
    newFile.writeText(content)
}

fun writeToFile(context: Context, uri: Uri, content: String) {
    try {
        val contentResolver = context.contentResolver

        contentResolver.openFileDescriptor(uri, "w")?.use {
            FileOutputStream(it.fileDescriptor).use { fOut ->
                val writer = fOut.writer(charset = Charsets.UTF_16)
                writer.write(content)
                writer.close()
            }
        }
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

fun Context.getFileName(uri: Uri): String? = when (uri.scheme) {
    ContentResolver.SCHEME_CONTENT -> getContentFileName(uri)
    else -> uri.path?.let(::File)?.name
}

private fun Context.getContentFileName(uri: Uri): String? = runCatching {
    contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        cursor.moveToFirst()
        return@use cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME).let(cursor::getString)
    }
}.getOrNull()
