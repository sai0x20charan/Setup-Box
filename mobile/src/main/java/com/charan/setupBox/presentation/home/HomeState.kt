package com.charan.setupBox.presentation.home

import com.charan.setupBox.presentation.common.model.ChannelData

data class HomeState(
    val allChannelData : List<ChannelData> = emptyList(),
    val isFetchingData : Boolean = false,
    val showDropDown : Boolean = false,
    val syncState : SyncState = SyncState()
)

data class SyncState(
    val isSyncing : Boolean = false,
    val hasError : Boolean = false,
    val errorMessage : String? = null
)
