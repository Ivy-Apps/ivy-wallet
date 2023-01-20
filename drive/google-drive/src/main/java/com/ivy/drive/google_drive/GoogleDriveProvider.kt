package com.ivy.drive.google_drive

import arrow.core.Either
import com.google.api.services.drive.Drive
import com.ivy.drive.google_drive.data.GoogleDriveError

internal interface GoogleDriveProvider {
    val errorOrDrive: Either<GoogleDriveError, Drive>
}