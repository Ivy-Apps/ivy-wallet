package com.ivy.file

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import arrow.core.Either
import java.io.*
import java.nio.charset.Charset

// TODO: Refactor and re-work! It's a fine mess...

fun writeToFile(context: Context, uri: Uri, content: String) {
    try {
        val contentResolver = context.contentResolver

        contentResolver.openFileDescriptor(uri, FDMode.Write.value)?.use {
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

fun writeToFileUnsafe(context: Context, uri: Uri, content: ByteArray) {
    val contentResolver = context.contentResolver

    contentResolver.openFileDescriptor(uri, FDMode.Write.value)?.use {
        FileOutputStream(it.fileDescriptor).use { fOut ->
            fOut.write(content)
        }
    }
}

fun readFileAsBytes(
    context: Context,
    uri: Uri,
): ByteArray? {
    return try {
        val contentResolver = context.contentResolver
        var fileContent: ByteArray? = null
        contentResolver.openFileDescriptor(uri, FDMode.Read.value)?.use {
            FileInputStream(it.fileDescriptor).use { fileInputStream ->
                fileContent = fileInputStream.readBytes()
            }
        }
        fileContent
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
        null
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}


fun readFile(
    context: Context,
    uri: Uri,
    charset: Charset
): String? {
    return try {
        val contentResolver = context.contentResolver

        var fileContent: String? = null

        contentResolver.openFileDescriptor(uri, FDMode.Read.value)?.use {
            FileInputStream(it.fileDescriptor).use { fileInputStream ->
                fileContent = readFileContent(
                    fileInputStream = fileInputStream,
                    charset = charset
                )
            }
        }

        fileContent
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
        null
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}

fun <T> inputStream(
    context: Context,
    uri: Uri,
    mode: FDMode,
    use: (InputStream) -> T
): Either<Throwable, T> = Either.catch({ it }) {
    val contentResolver = context.contentResolver
    contentResolver.openFileDescriptor(uri, mode.value)?.use {
        use(FileInputStream(it.fileDescriptor))
    } ?: error("contentResolver.openFileDescriptor($uri, $mode) returned null")
}

@Throws(IOException::class)
private fun readFileContent(
    fileInputStream: FileInputStream,
    charset: Charset
): String {
    BufferedReader(InputStreamReader(fileInputStream, charset)).use { br ->
        val sb = StringBuilder()
        var line: String?
        while (br.readLine().also { line = it } != null) {
            sb.append(line)
            sb.append('\n')
        }
        return sb.toString()
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