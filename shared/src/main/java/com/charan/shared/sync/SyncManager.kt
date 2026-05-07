package com.charan.shared.sync

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncManager @Inject constructor() {

    data class SyncResult(
        val insertedCount: Int,
        val updatedCount: Int,
        val removedCount: Int
    )

    suspend fun <T> sync(
        fetchRemote: suspend () -> List<T>,
        fetchLocal: suspend () -> List<T>,
        remoteKey: (T) -> String?,
        localKey: (T) -> String?,
        isSame: (local: T, remote: T) -> Boolean = { local, remote -> local == remote },
        insertLocal: suspend (T) -> Unit,
        removeLocal: suspend (T) -> Unit,
        updateLocal: suspend (T) -> Unit
    ): SyncResult {
        val remoteData = fetchRemote()
        val localData = fetchLocal()

        val localKeys = localData.map(localKey).toSet()
        val remoteKeys = remoteData.map(remoteKey).toSet()

        val itemsToInsert = remoteData.filter { remoteKey(it) !in localKeys }
        val itemsToRemove = localData.filter { localKey(it) !in remoteKeys }
        val itemsToUpdate = remoteData.filter { remoteItem ->
            localData.any { localItem ->
                localKey(localItem) == remoteKey(remoteItem) && !isSame(localItem, remoteItem)
            }
        }

        itemsToInsert.forEach { insertLocal(it) }
        itemsToRemove.forEach { removeLocal(it) }
        itemsToUpdate.forEach { updateLocal(it) }

        return SyncResult(
            insertedCount = itemsToInsert.size,
            updatedCount = itemsToUpdate.size,
            removedCount = itemsToRemove.size
        )
    }
}
