package com.charan.shared.utils

data class SyncStatus(
    val isSyncing: Boolean = false,
    val hasError: Boolean = false,
    val errorMessage: String? = null
)

