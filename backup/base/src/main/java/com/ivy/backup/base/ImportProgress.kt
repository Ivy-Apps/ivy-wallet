package com.ivy.backup.base

interface OnImportProgress {
    fun onProgress(percent: Float, message: String)
}