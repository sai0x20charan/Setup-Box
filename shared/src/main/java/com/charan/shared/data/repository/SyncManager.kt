package com.charan.shared.data.repository

import com.charan.shared.utils.ProcessState
import kotlinx.coroutines.flow.Flow

interface SyncManager {

    suspend fun syncData()

    suspend fun syncListener()

}