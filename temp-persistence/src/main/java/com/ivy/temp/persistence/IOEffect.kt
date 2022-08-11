package com.ivy.temp.persistence

sealed class IOEffect<T>(val item: T) {
    class Save<T>(item: T) : IOEffect<T>(item)
    class Delete<T>(item: T) : IOEffect<T>(item)
}