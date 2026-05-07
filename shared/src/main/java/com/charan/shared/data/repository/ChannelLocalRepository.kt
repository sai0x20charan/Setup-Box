package com.charan.shared.data.repository

import com.charan.shared.data.local.entity.ChannelEntity
import kotlinx.coroutines.flow.Flow

interface ChannelLocalRepository {
    suspend fun upsert(channel: ChannelEntity)
    suspend fun update(channel: ChannelEntity)
    suspend fun deleteByUuid(uuid: String)
    fun getAllData(): Flow<List<ChannelEntity>>

    suspend fun getById(id : Long) : ChannelEntity

    fun getUnSyncedData() : Flow<List<ChannelEntity>>

    suspend fun deleteByUUID(uuid : String)

    fun getUnSyncedDataCount() : Flow<Int>
}
