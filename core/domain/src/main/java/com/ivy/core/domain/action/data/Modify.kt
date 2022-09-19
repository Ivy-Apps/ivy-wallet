package com.ivy.core.domain.action.data

/**
 * Data type representing a save or delete operation.
 * Modify operations are usually used for "WriteAct"s like
 * [com.ivy.core.domain.action.transaction.WriteTrnsAct],
 * [com.ivy.core.domain.action.account.WriteAccountsAct],
 * [com.ivy.core.domain.action.category.WriteCategoriesAct].
 *
 * It supports:
 * - [save]: persists a single item.
 * - [saveMany]: persists multiple items.
 * - [delete]: deletes a single item by id.
 * - [deleteMany]: deletes multiple items by their ids.
 */
sealed interface Modify<out T> {
    companion object {
        /**
         * @param item the item to persist.
         * @return an operation for persisting a single item.
         */
        fun <T> save(item: T) = Save(listOf(item))

        /**
         * @param items a collection of items to persist,
         * will be converted to [List] under the hood.
         * @return an operation for persisting multiple items.
         */
        fun <T> saveMany(items: Iterable<T>) = Save(items.toList())

        /**
         * @param itemId the string id of the item to be deleted.
         * @return an operation to delete a single item
         */
        fun delete(itemId: String) = Delete(listOf(itemId))

        /**
         * @param itemIds collection of the string ids of the items to be deleted,
         * will be converted to [List] under the hood.
         * @return an operation to delete multiple items.
         */
        fun deleteMany(itemIds: Iterable<String>) = Delete(itemIds.toList())
    }

    data class Save<T> internal constructor(val items: List<T>) : Modify<T>
    data class Delete internal constructor(val itemIds: List<String>) : Modify<Nothing>
}