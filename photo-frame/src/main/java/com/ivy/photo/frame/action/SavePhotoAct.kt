package com.ivy.photo.frame.action

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import arrow.core.Either
import arrow.core.computations.either
import com.ivy.core.domain.action.Action
import com.ivy.file.writeToFileUnsafe
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.ByteArrayOutputStream
import javax.inject.Inject


class SavePhotoAct @Inject constructor(
    @ApplicationContext
    private val appContext: Context,
) : Action<SavePhotoAct.Input, Either<SavePhotoError, Unit>>() {
    data class Input(
        val photo: Bitmap,
        val location: Uri
    )

    override suspend fun action(input: Input): Either<SavePhotoError, Unit> = either {
        val pngBytes = bitmapToPng(input.photo).bind()
        writePNGtoFile(pngBytes, input.location).bind()
    }

    private fun bitmapToPng(bitmap: Bitmap): Either<SavePhotoError, ByteArray> =
        Either.catch(SavePhotoError::BitmapToPng) {
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream)
            outputStream.toByteArray()
        }

    private fun writePNGtoFile(
        pngBytes: ByteArray,
        location: Uri
    ): Either<SavePhotoError, Unit> = Either.catch(SavePhotoError::WriteToFile) {
        writeToFileUnsafe(appContext, location, pngBytes)
    }
}

sealed interface SavePhotoError {
    data class BitmapToPng(val reason: Throwable) : SavePhotoError
    data class WriteToFile(val reason: Throwable) : SavePhotoError
}