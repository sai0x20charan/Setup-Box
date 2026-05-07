package com.charan.shared.data.repository.impl

import com.charan.shared.data.mappers.toChannelContentDto
import com.charan.shared.data.mappers.toChannelContentDtoList
import com.charan.shared.data.mappers.toChannelEntity
import com.charan.shared.data.repository.ChannelLocalRepository
import com.charan.shared.data.repository.SupabaseRepo
import com.charan.shared.data.repository.SyncManager
import com.charan.shared.utils.ProcessState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class SyncManagerRepositoryImpl(
    private val channelLocalRepository: ChannelLocalRepository,
    private val supabaseRepo: SupabaseRepo
): SyncManager {
    override suspend fun syncData() =  coroutineScope{
        try {
            channelLocalRepository.getUnSyncedData().collectLatest { list->
                supabaseRepo.insertChannelData(
                    list.toChannelContentDtoList()
                ).collectLatest {
                    when(it){
                        is ProcessState.Success -> {
                            it.data.forEach { data->
                                val channelData = data.toChannelEntity().copy(isSynced = true)
                                channelLocalRepository.upsert(channelData)

                            }

                        }
                        is ProcessState.Error -> {

                        }
                        else -> {}
                    }

                }


            }

        } catch (e: Exception) {
                println("Error during sync: ${e.message}")

        }
    }
    override suspend fun syncListener() = coroutineScope{
        channelLocalRepository.getUnSyncedDataCount().collectLatest { value ->
            if(value > 0){
                println("There are $value unsynced items. Starting sync...")

                syncData()

            } else {
                println("All items are synced.")
            }
        }
    }


}