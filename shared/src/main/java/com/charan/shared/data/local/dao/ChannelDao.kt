package com.charan.shared.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.charan.shared.data.local.entity.ChannelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChannelDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(channel: ChannelEntity)

    @Update
    suspend fun update(channel: ChannelEntity)

    @Query("DELETE FROM channels WHERE id = :id")
    suspend fun deleteByUuid(id: String)

    @Query("SELECT * FROM channels WHERE isDeleted = 0")
    fun getAllActiveData(): Flow<List<ChannelEntity>>

    @Query("SELECT * FROM channels WHERE id = :id")
    suspend fun getAllData(id: String): ChannelEntity

    @Query("SELECT * FROM channels WHERE isSynced = 0")
    fun getUnSyncedData(): Flow<List<ChannelEntity>>

    @Query("UPDATE channels SET isSynced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: String)

    @Query("UPDATE channels SET isDeleted = 1 , isSynced = 0 WHERE id = :id")
    suspend fun markAsDeleted(id: String)

    @Query("SELECT COUNT(*) FROM channels WHERE isSynced = 0")
    fun getUnSyncedDataCount(): Flow<Int>

    @Query("SELECT DISTINCT appPackage FROM channels")
    fun getDistinctPackages(): List<String>

        @Query("DELETE FROM channels")
    fun clearAllData()

}
