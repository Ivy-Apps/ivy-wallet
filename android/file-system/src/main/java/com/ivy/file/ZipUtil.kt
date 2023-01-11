package com.ivy.file

import android.content.Context
import android.net.Uri
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

private const val MODE_WRITE = "w"
private const val MODE_READ = "r"

fun zip(zipFile: File, files: List<File>) {
    ZipOutputStream(BufferedOutputStream(FileOutputStream(zipFile))).use { outStream ->
        zip(outStream, files)
    }
}

fun zip(context: Context, zipFile: Uri, files: List<File>) {
    context.contentResolver.openFileDescriptor(zipFile, MODE_WRITE).use { descriptor ->
        descriptor?.fileDescriptor?.let {
            ZipOutputStream(BufferedOutputStream(FileOutputStream(it))).use { outStream ->
                zip(outStream, files)
            }
        }
    }
}

private fun zip(
    outStream: ZipOutputStream,
    files: List<File>,
    includeParentFolder: Boolean = false
) {
    files.forEach { file ->
        if (file.isDirectory) {
            file.mkdir()
            zip(outStream, file.listFiles()?.toList() ?: emptyList(), includeParentFolder = true)
        } else {
            val fileLoc: String =
                if (file.parent.isNullOrEmpty() || !includeParentFolder) file.name else (file.parent!!).substring(
                    file.parent!!.lastIndexOf("/")
                ) + "/" + file.name

            outStream.putNextEntry(ZipEntry(fileLoc))
            BufferedInputStream(FileInputStream(file)).use { inStream ->
                inStream.copyTo(outStream)
            }
        }

    }
}

fun unzip(zipFile: File, location: File) {
    ZipInputStream(BufferedInputStream(FileInputStream(zipFile))).use { inStream ->
        unzip(inStream, location)
    }
}

fun unzip(context: Context, zipFilePath: Uri, unzipLocation: File) {
    context.contentResolver.openFileDescriptor(zipFilePath, MODE_READ).use { descriptor ->
        descriptor?.fileDescriptor?.let {
            ZipInputStream(BufferedInputStream(FileInputStream(it))).use { inStream ->
                unzip(inStream, unzipLocation)
            }
        }
    }
}

private fun unzip(inStream: ZipInputStream, location: File) {
    if (location.exists() && !location.isDirectory)
        throw IllegalStateException("Location file must be directory or not exist")

    if (!location.isDirectory) location.mkdirs()

    val locationPath = location.absolutePath.let {
        if (!it.endsWith(File.separator)) "$it${File.separator}"
        else it
    }

    var zipEntry: ZipEntry?
    var unzipFile: File
    var unzipParentDir: File?

    while (inStream.nextEntry.also { zipEntry = it } != null) {
        unzipFile = File(locationPath + zipEntry!!.name)
        if (zipEntry!!.isDirectory) {
            if (!unzipFile.isDirectory) unzipFile.mkdirs()
        } else {
            unzipParentDir = unzipFile.parentFile
            if (unzipParentDir != null && !unzipParentDir.isDirectory) {
                unzipParentDir.mkdirs()
            }
            BufferedOutputStream(FileOutputStream(unzipFile)).use { outStream ->
                inStream.copyTo(outStream)
            }
        }
    }
}