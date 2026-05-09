package com.charan.shared.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "channels",
    indices = [Index(value = ["uuid"], unique = true)]
)
data class ChannelEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val channelLink: String = "",
    val channelName: String = "",
    val channelPhoto: String = "",
    val category: String = "",
    val appPackage: String = "",
    val uuid: String = "",
    val isSynced: Boolean = false,
    val isDeleted : Boolean = false
)
