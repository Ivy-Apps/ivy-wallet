package com.ivy.drive.google_drive.data

enum class DriveMimeType(val value: String) {
    PDF("application/pdf"),
    TXT("text/plain"),
    JPEG("image/jpeg"),
    PNG("image/png"),
    SVG("image/svg+xml"),
    CSV("text/csv"),
    ZIP("application/zip"),
    FOLDER("application/vnd.google-apps.folder"),
}