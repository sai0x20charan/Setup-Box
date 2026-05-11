package com.charan.shared.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "channels",
)
data class ChannelEntity(
    @PrimaryKey
    val id: String = "",
    val url: String = "",
    val name: String = "",
    val thumbnailURL: String = "",
    val category: String = "",
    val appPackage: String = "",
    val isSynced: Boolean = false,
    val isDeleted : Boolean = false
)
