package com.ivy.temp.persistence

sealed class Operation<T>(val item: T) {
    class Save<T>(item: T) : Operation<T>(item)
    class Delete<T>(item: T) : Operation<T>(item)
}