package com.ivy.data.repository

import com.ivy.base.threading.DispatchersProvider
import com.ivy.data.DataObserver
import com.ivy.data.DataWriteEvent
import com.ivy.data.DeleteOperation
import com.ivy.data.model.sync.Identifiable
import com.ivy.data.model.sync.UniqueId
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RepositoryMemoFactory @Inject constructor(
    private val dataObserver: DataObserver,
    private val dispatchers: DispatchersProvider,
) {
    fun <T : Identifiable<TID>, TID : UniqueId> createMemo(
        getDataWriteSaveEvent: (List<T>) -> DataWriteEvent,
        getDateWriteDeleteEvent: (DeleteOperation<TID>) -> DataWriteEvent
    ): RepositoryMemo<T, TID> = RepositoryMemo(
        dataObserver = dataObserver,
        dispatchers = dispatchers,
        getDataWriteSaveEvent = getDataWriteSaveEvent,
        getDataWriteDeleteEvent = getDateWriteDeleteEvent,
    )
}

class RepositoryMemo<T : Identifiable<TID>, TID : UniqueId> internal constructor(
    private val dataObserver: DataObserver,
    private val dispatchers: DispatchersProvider,
    private val getDataWriteSaveEvent: (List<T>) -> DataWriteEvent,
    private val getDataWriteDeleteEvent: (DeleteOperation<TID>) -> DataWriteEvent,
) {

    private val _memo = mutableMapOf<TID, T>()
    val items: Map<TID, T> = _memo
    var findAllMemoized = false
        private set

    suspend fun findAll(
        findAllOperation: suspend () -> List<T>,
        sortMemo: Collection<T>.() -> List<T>,
    ): List<T> {
        return if (findAllMemoized) {
            sortMemo(_memo.values)
        } else {
            withContext(dispatchers.io) {
                findAllOperation().also {
                    memoize(it)
                    findAllMemoized = true
                }
            }
        }
    }

    suspend fun findById(
        id: TID,
        findByIdOperation: suspend (TID) -> T?
    ): T? {
        return items[id] ?: withContext(dispatchers.io) {
            findByIdOperation(id)?.also(::memoize)
        }
    }

    suspend fun findByIds(
        ids: List<TID>,
        findByIdOperation: suspend (TID) -> T?
    ): List<T> = ids.mapNotNull { id -> findById(id, findByIdOperation) }

    suspend fun save(
        value: T,
        writeOperation: suspend (T) -> Unit,
    ) {
        withContext(dispatchers.io) {
            writeOperation(value)
            memoize(value)
            dataObserver.post(getDataWriteSaveEvent(listOf(value)))
        }
    }

    suspend fun saveMany(
        values: List<T>,
        writeOperation: suspend (List<T>) -> Unit,
    ) {
        withContext(dispatchers.io) {
            writeOperation(values)
            memoize(values)
            dataObserver.post(getDataWriteSaveEvent(values))
        }
    }

    suspend fun deleteById(
        id: TID,
        deleteByIdOperation: suspend (TID) -> Unit,
    ) {
        withContext(dispatchers.io) {
            _memo.remove(id)
            deleteByIdOperation(id)
            dataObserver.post(
                getDataWriteDeleteEvent(DeleteOperation.Just(listOf(id)))
            )
        }
    }

    suspend fun deleteAll(
        deleteAllOperation: suspend () -> Unit,
    ) {
        withContext(dispatchers.io) {
            _memo.clear()
            deleteAllOperation()
            dataObserver.post(getDataWriteDeleteEvent(DeleteOperation.All))
        }
    }

    private fun memoize(items: List<T>) {
        items.forEach(::memoize)
    }

    private fun memoize(item: T) {
        _memo[item.id] = item
    }
}