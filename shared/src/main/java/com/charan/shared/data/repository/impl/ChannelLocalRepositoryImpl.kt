package com.charan.shared.data.repository.impl

import android.util.Log
import com.charan.shared.data.local.dao.ChannelDao
import com.charan.shared.data.local.entity.ChannelEntity
import com.charan.shared.data.repository.ChannelLocalRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChannelLocalRepositoryImpl @Inject constructor(
    private val channelDao: ChannelDao
) : ChannelLocalRepository {
    override suspend fun upsert(channel: ChannelEntity) = channelDao.upsert(channel)

    override suspend fun update(channel: ChannelEntity) = channelDao.update(channel)

    override suspend fun deleteByUuid(uuid: String) = channelDao.deleteByUuid(uuid)

    override fun getAllActiveData(): Flow<List<ChannelEntity>> = channelDao.getAllActiveData()

    override suspend fun getById(id: String): ChannelEntity {
        return channelDao.getAllData(id)
    }

    override fun getUnSyncedData(): Flow<List<ChannelEntity>> {
        return channelDao.getUnSyncedData()
    }

        override suspend fun deleteByUUID(uuid: String) {
            channelDao.markAsDeleted(uuid)
        }

    override fun getUnSyncedDataCount(): Flow<Int> {
        return channelDao.getUnSyncedDataCount()
    }

    override fun getDistinctPackages(): List<String> {
        return channelDao.getDistinctPackages()
    }

    override fun clearAllData() {
        channelDao.clearAllData()

    }
}
