package com.charan.shared.data.repository.impl

import com.charan.shared.data.mappers.toChannelContentDtoList
import com.charan.shared.data.mappers.toChannelEntity
import com.charan.shared.data.repository.ChannelLocalRepository
import com.charan.shared.data.repository.SupabaseRepo
import com.charan.shared.data.repository.SyncManager
import com.charan.shared.utils.ProcessState
import com.charan.shared.utils.SyncStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class SyncManagerRepositoryImpl(
    private val channelLocalRepository: ChannelLocalRepository,
    private val supabaseRepo: SupabaseRepo
) : SyncManager {

    private val syncStatus = MutableStateFlow(SyncStatus())

    private val syncMutex = Mutex()

    override suspend fun syncData() {
        if (syncMutex.isLocked) return

        syncMutex.withLock {
            try {
                val unSyncedList = channelLocalRepository
                    .getUnSyncedData()
                    .first()
                if (unSyncedList.isEmpty()) {
                    syncStatus.value = SyncStatus()
                    return
                }
                syncStatus.value = SyncStatus(isSyncing = true)
                supabaseRepo.insertChannelData(unSyncedList
                    .toChannelContentDtoList(supabaseRepo.getEmail() ?: "")
                ).collectLatest { result ->
                    when (result) {
                        is ProcessState.Success -> {
                            result.data.forEach { data ->
                                val syncedEntity = data
                                    .toChannelEntity()
                                    .copy(isSynced = true)
                                channelLocalRepository.upsert(syncedEntity)
                            }
                            syncStatus.value = SyncStatus()
                        }
                        is ProcessState.Error -> {
                            syncStatus.value = SyncStatus(hasError = true,errorMessage = result.exception)
                        }
                        else -> Unit
                    }
                }

            } catch (e: Exception) {
                syncStatus.value = SyncStatus(hasError = true,errorMessage = e.message)
                println("Sync Error: ${e.message}")
            }
        }
    }

    override suspend fun syncListener() {
        channelLocalRepository
            .getUnSyncedDataCount()
            .distinctUntilChanged()
            .collectLatest { count ->
                println("Unsynced Count: $count")
                if (count > 0) {
                    syncData()
                }
            }
    }

    override suspend fun fetchAndUpdateData(): Flow<ProcessState<Boolean>> =
        channelFlow {
            send(ProcessState.Loading())
            try {
                supabaseRepo.getData().collectLatest { result ->
                    when (result) {
                        is ProcessState.Success -> {
                            val channelList = result.data.map { data ->
                                data.toChannelEntity()
                                    .copy(isSynced = true)
                            }
                            channelList.forEach { channel ->
                                channelLocalRepository.upsert(channel)
                            }
                            send(ProcessState.Success(true))
                        }
                        is ProcessState.Error -> {
                            send(ProcessState.Error( result.exception))
                        }
                        else -> Unit
                    }
                }

            } catch (e: Exception) {
                send(ProcessState.Error(e.message.toString()))
            }
        }

    override fun observeSyncStatus(): StateFlow<SyncStatus> =
        syncStatus
}