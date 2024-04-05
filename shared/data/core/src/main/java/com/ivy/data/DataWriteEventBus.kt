package com.ivy.data

import com.ivy.data.model.Account
import com.ivy.data.model.AccountId
import com.ivy.data.model.Category
import com.ivy.data.model.CategoryId
import com.ivy.data.model.sync.UniqueId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataWriteEventBus @Inject constructor() {
    private val internalEvents = MutableSharedFlow<DataWriteEvent>()
    val events: Flow<DataWriteEvent> = internalEvents

    suspend fun post(event: DataWriteEvent) {
        internalEvents.emit(event)
    }
}

sealed interface DataWriteEvent {
    data class SaveAccounts(val accounts: List<com.ivy.data.model.Account>) : DataWriteEvent
    data class DeleteAccounts(val operation: DeleteOperation<com.ivy.data.model.AccountId>) : DataWriteEvent

    data class SaveCategories(val categories: List<com.ivy.data.model.Category>) : DataWriteEvent
    data class DeleteCategories(val operation: DeleteOperation<com.ivy.data.model.CategoryId>) : DataWriteEvent
}

sealed interface DeleteOperation<out Id : UniqueId> {
    data object All : DeleteOperation<Nothing>
    data class Just<Id : UniqueId>(val ids: List<Id>) : DeleteOperation<Id>
}
