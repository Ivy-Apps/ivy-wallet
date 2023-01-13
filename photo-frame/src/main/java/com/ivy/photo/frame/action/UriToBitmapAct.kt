package com.ivy.photo.frame.action

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import arrow.core.Either
import arrow.core.computations.either
import com.ivy.core.domain.action.Action
import com.ivy.file.FDMode
import com.ivy.file.inputStream
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.InputStream
import javax.inject.Inject


class UriToBitmapAct @Inject constructor(
    @ApplicationContext
    private val appContext: Context
) : Action<Uri, Either<Throwable, Bitmap>>() {
    override suspend fun action(input: Uri): Either<Throwable, Bitmap> = either {
        inputStream(appContext, input, FDMode.Read) {
            decodeBitmap(it)
        }.bind().bind()
    }

    private fun decodeBitmap(inputStream: InputStream): Either<Throwable, Bitmap> =
        Either.catch({ it }) {
            BitmapFactory.decodeStream(inputStream)!!
        }
}