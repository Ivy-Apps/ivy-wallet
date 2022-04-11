package com.ivy.wallet.utils

sealed class OpResult<out T> {
    object Loading : OpResult<Nothing>()
    data class Success<out T>(val data: T) : OpResult<T>()
    data class Failure(val exception: Exception) : OpResult<Nothing>() {
        fun error() = exception.message ?: exception.cause?.message ?: "unknown"
    }

    companion object {
        fun <T> success(data: T) = Success(data)
        fun loading() = Loading
        fun failure(e: Exception) = Failure(e)
        fun faliure(errMsg: String) = Failure(Exception(errMsg))
    }
}