package com.ivy.data.file

import android.content.Context
import android.net.Uri
import arrow.core.Either
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.Charset
import javax.inject.Inject

class FileSystem @Inject constructor(
    @ApplicationContext
    private val appContext: Context
) {
    fun writeToFile(uri: Uri, content: String): Either<Failure, Unit> = try {
        val contentResolver = appContext.contentResolver

        contentResolver.openFileDescriptor(uri, "w")?.use {
            FileOutputStream(it.fileDescriptor).use { fOut ->
                val writer = fOut.writer(charset = Charsets.UTF_16)
                writer.write(content)
                writer.close()
            }
        }
        Either.Right(Unit)
    } catch (e: FileNotFoundException) {
        Either.Left(Failure.FileNotFound(e))
    } catch (e: Exception) {
        Either.Left(Failure.IO(e))
    }

    fun read(
        uri: Uri,
        charset: Charset = Charsets.UTF_8
    ): Either<Failure, String> {
        return try {
            val contentResolver = appContext.contentResolver
            var fileContent: String? = null

            contentResolver.openFileDescriptor(uri, "r")?.use {
                FileInputStream(it.fileDescriptor).use { fileInputStream ->
                    fileContent = readFileContent(
                        fileInputStream = fileInputStream,
                        charset = charset
                    )
                }
            }

            Either.Right(fileContent!!)
        } catch (e: FileNotFoundException) {
            Either.Left(Failure.FileNotFound(e))
        } catch (e: Exception) {
            Either.Left(Failure.IO(e))
        }
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

    sealed interface Failure {
        val e: Throwable

        data class FileNotFound(override val e: Throwable) : Failure
        data class IO(override val e: Throwable) : Failure
    }
}
