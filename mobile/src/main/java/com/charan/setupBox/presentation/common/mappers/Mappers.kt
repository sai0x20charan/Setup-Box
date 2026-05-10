package com.charan.setupBox.presentation.common.mappers

import com.charan.setupBox.presentation.common.model.ChannelData
import com.charan.shared.data.local.entity.ChannelEntity
import java.util.UUID

fun ChannelEntity.toChannelData() : ChannelData {
    return ChannelData(
        id = this.id,
        channelLink = this.url,
        channelName = this.name,
        channelPhoto = this.thumbnailURL,
        category = this.category,
        appPackage = this.appPackage,
        isSynced = this.isSynced,
    )
}

fun List<ChannelEntity>.toChannelDataList() : List<ChannelData> {
    return this.map { it.toChannelData() }
}

fun ChannelData.toChannelEntity(
    isEdit : Boolean = false
) : ChannelEntity {
    return ChannelEntity(
        id = if(isEdit) this.id else UUID.randomUUID().toString(),
        name = this.channelName,
        appPackage = this.appPackage,
        category = this.category,
        isSynced = false,
        url = this.channelLink,
        thumbnailURL = this.channelPhoto,
    )
}

