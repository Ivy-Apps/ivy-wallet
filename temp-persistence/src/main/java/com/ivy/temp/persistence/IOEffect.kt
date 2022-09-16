package com.ivy.temp.persistence

@Deprecated("will be deleted")
sealed class IOEffect<T>(val item: T) {
    class Save<T>(item: T) : IOEffect<T>(item)
    class Delete<T>(item: T) : IOEffect<T>(item)
}