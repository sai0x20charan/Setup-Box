package com.charan.setupBox.presentation.mapper

import com.charan.setupBox.presentation.home.ChannelData
import com.charan.shared.data.local.entity.ChannelEntity


fun ChannelEntity.toChannelData() : ChannelData {
    return ChannelData(
        channelName = this.name,
        channelCategory = this.category,
        channelImage = this.thumbnailURL,
        channelURL = this.url,
        channelAppPackage = this.appPackage,
    )
}

fun List<ChannelEntity>.toChannelDataList() : List<ChannelData> {
    return this.map { it.toChannelData() }
}