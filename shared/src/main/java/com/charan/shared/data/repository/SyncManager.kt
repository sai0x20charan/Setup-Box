package com.charan.shared.data.repository

import com.charan.shared.utils.ProcessState
import com.charan.shared.utils.SyncStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface SyncManager {

    suspend fun syncData()

    suspend fun syncListener()

    suspend fun fetchAndUpdateData() : Flow<ProcessState<Boolean>>

    fun observeSyncStatus(): StateFlow<SyncStatus>

}
