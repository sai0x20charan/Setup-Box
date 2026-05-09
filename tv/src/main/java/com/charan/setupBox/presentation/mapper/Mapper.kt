package com.charan.setupBox.presentation.mapper

import com.charan.setupBox.presentation.home.ChannelData
import com.charan.shared.data.local.entity.ChannelEntity


fun ChannelEntity.toChannelData() : ChannelData {
    return ChannelData(
        channelName = this.channelName,
        channelCategory = this.category,
        channelImage = this.channelPhoto,
        channelURL = this.channelLink,
        channelAppPackage = this.appPackage,
        channelId = this.id ?: 0L
    )
}

fun List<ChannelEntity>.toChannelDataList() : List<ChannelData> {
    return this.map { it.toChannelData() }
}