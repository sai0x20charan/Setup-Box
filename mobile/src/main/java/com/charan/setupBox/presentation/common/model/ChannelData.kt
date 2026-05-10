package com.charan.setupBox.presentation.common.model

data class ChannelData(
    val channelLink : String = "",
    val channelName : String = "",
    val channelPhoto : String = "",
    val category : String = "",
    val appPackage : String = "",
    val id : String = "",
    val isSynced : Boolean = false
)
