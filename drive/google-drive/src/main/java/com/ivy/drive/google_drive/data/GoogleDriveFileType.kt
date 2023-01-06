package com.ivy.drive.google_drive.data

sealed class GoogleDriveFileType {
    class Backup(val type: GoogleDriveType = GoogleDriveType.BackupTypeCSV): GoogleDriveFileType(){
        companion object {
            const val FOLDER_NAME = "Backup"
        }
    }
    class Image(val type: GoogleDriveType = GoogleDriveType.ImageTypeJPEG): GoogleDriveFileType(){
        companion object {
            const val FOLDER_NAME = "Image"
        }
    }
    class Video(val type: GoogleDriveType = GoogleDriveType.VideoType): GoogleDriveFileType(){
        companion object {
            const val FOLDER_NAME = "Video"
        }
    }

}