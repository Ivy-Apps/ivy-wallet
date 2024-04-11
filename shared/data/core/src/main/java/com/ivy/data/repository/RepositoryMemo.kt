package com.ivy.data.repository

import com.ivy.base.threading.DispatchersProvider
import com.ivy.data.DataObserver
import com.ivy.data.DataWriteEvent
import com.ivy.data.DeleteOperation
import com.ivy.data.model.sync.Syncable
import com.ivy.data.model.sync.UniqueId
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RepositoryMemoFactory @Inject constructor(
    private val dataObserver: DataObserver,
    private val dispatchers: DispatchersProvider,
) {
    fun <T : Syncable<TID>, TID : UniqueId> createMemo(
        getSaveEvent: (List<T>) -> DataWriteEvent,
        getDeleteEvent: (DeleteOperation<TID>) -> DataWriteEvent
    ): RepositoryMemo<T, TID> = RepositoryMemo(
        dataObserver = dataObserver,
        dispatchers = dispatchers,
        getSaveEvent = getSaveEvent,
        getDeleteEvent = getDeleteEvent,
    )
}

class RepositoryMemo<T : Syncable<TID>, TID : UniqueId> internal constructor(
    private val dataObserver: DataObserver,
    private val dispatchers: DispatchersProvider,
    private val getSaveEvent: (List<T>) -> DataWriteEvent,
    private val getDeleteEvent: (DeleteOperation<TID>) -> DataWriteEvent,
) {

    private val _memo = mutableMapOf<TID, T>()
    val items: Map<TID, T> = _memo
    var findAllMemoized = false
        private set

    suspend fun findAll(
        findAllOperation: suspend () -> List<T>,
        sortMemo: (Collection<T>) -> List<T>,
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
        findByIdOperation: suspend (UniqueId) -> T?
    ): T? {
        return items[id] ?: withContext(dispatchers.io) {
            findByIdOperation(id)?.also(::memoize)
        }
    }

    suspend fun save(
        value: T,
        writeOperation: suspend (T) -> Unit,
    ) {
        withContext(dispatchers.io) {
            writeOperation(value)
            memoize(value)
            dataObserver.post(getSaveEvent(listOf(value)))
        }
    }

    suspend fun saveMany(
        values: List<T>,
        writeOperation: suspend (List<T>) -> Unit,
    ) {
        withContext(dispatchers.io) {
            writeOperation(values)
            memoize(values)
            dataObserver.post(getSaveEvent(values))
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
                getDeleteEvent(DeleteOperation.Just(listOf(id)))
            )
        }
    }

    suspend fun deleteAll(
        deleteAllOperation: suspend () -> Unit,
    ) {
        withContext(dispatchers.io) {
            _memo.clear()
            deleteAllOperation()
            dataObserver.post(getDeleteEvent(DeleteOperation.All))
        }
    }

    private fun memoize(items: List<T>) {
        items.forEach(::memoize)
    }

    private fun memoize(item: T) {
        _memo[item.id] = item
    }
}