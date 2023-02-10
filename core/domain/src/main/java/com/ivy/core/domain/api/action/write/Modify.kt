package com.ivy.core.domain.api.action.write

import arrow.core.NonEmptyList
import com.ivy.core.data.sync.Syncable
import com.ivy.core.data.sync.UniqueId

sealed interface Modify<T : Syncable, TID : UniqueId> {
    data class Save<T : Syncable>(
        val item: T,
    ) : Modify<T, Nothing>

    data class SaveMany<T : Syncable>(
        val items: NonEmptyList<T>
    ) : Modify<T, Nothing>

    data class Delete<TID : UniqueId>(
        val id: TID,
    ) : Modify<Nothing, TID>

    data class DeleteMany<TID : UniqueId>(
        val ids: Set<TID>
    ) : Modify<Nothing, TID>
}