package com.ivy.core.domain.action.data

sealed interface Modify<out T> {
    companion object {
        fun <T> save(item: T) = Save(listOf(item))
        fun <T> saveMany(items: Iterable<T>) = Save(items.toList())

        fun delete(itemId: String) = Delete(listOf(itemId))
        fun deleteMany(itemIds: Iterable<String>) = Delete(itemIds.toList())
    }

    data class Save<T> internal constructor(val items: List<T>) : Modify<T>
    data class Delete internal constructor(val itemIds: List<String>) : Modify<Nothing>
}