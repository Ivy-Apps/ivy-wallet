package com.ivy.sync.base

import com.ivy.frp.monad.Res

interface SyncItem<T> {

    /**
     * Provides an instance of the SyncItem only if it's **enabled**.
     * @return **enabled SyncItem** instance or **null**.
     */
    suspend fun enabled(): SyncItem<T>?

    /**
     * Saves a list of items.
     * @return a list of the **successfully saved items**.
     */
    suspend fun save(items: List<T>): List<T>

    /**
     * Deletes a list of items.
     * @return a list of the **successfully deleted items**.
     */
    suspend fun delete(items: List<T>): List<T>

    /**
     * #WIP
     */
    suspend fun get(): Res<Unit, List<T>>

}