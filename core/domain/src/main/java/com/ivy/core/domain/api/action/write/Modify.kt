package com.ivy.core.domain.api.action.write

import arrow.core.NonEmptyList
import com.ivy.core.data.sync.Syncable
import com.ivy.core.data.sync.UniqueId

sealed interface Modify<out T : Syncable, out TID : UniqueId> {
    data class Save<out T : Syncable>(
        val item: T,
    ) : Modify<T, Nothing>

    data class SaveMany<out T : Syncable>(
        val items: NonEmptyList<T>
    ) : Modify<T, Nothing>

    data class Delete<out TID : UniqueId>(
        val id: TID,
    ) : Modify<Nothing, TID>

    data class DeleteMany<out TID : UniqueId>(
        val ids: NonEmptyList<TID>
    ) : Modify<Nothing, TID>
}