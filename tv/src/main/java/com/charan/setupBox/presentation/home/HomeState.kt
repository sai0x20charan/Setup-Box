package com.charan.setupBox.presentation.home

data class HomeState(
    val isLoading: Boolean = false,
    val categories: List<ChannelCategory> = emptyList(),
    val showModelSheet : Boolean = false,
    val profileURL : String = "",
    val email : String = "",
    val appVersion : String ="",
    val userName : String = ""
)

data class ChannelCategory(
    val categoryName: String,
    val channels: List<ChannelData>
)

data class ChannelData(
    val channelName: String = "",
    val channelCategory: String = "",
    val channelImage: String = "",
    val channelURL: String = "",
    val channelAppPackage: String = "",
    val channelId : Long = 0L
)
