package com.charan.setupBox.presentation.common.mappers

import com.charan.setupBox.presentation.common.model.ChannelData
import com.charan.shared.data.local.entity.ChannelEntity
import java.util.UUID

fun ChannelEntity.toChannelData() : ChannelData {
    return ChannelData(
        id = this.id,
        channelLink = this.channelLink,
        channelName = this.channelName,
        channelPhoto = this.channelPhoto,
        category = this.category,
        appPackage = this.appPackage,
    )
}

fun List<ChannelEntity>.toChannelDataList() : List<ChannelData> {
    return this.map { it.toChannelData() }
}

fun ChannelData.toChannelEntity(
    isEdit : Boolean = false
) : ChannelEntity {
    return ChannelEntity(
        id = if(isEdit) this.id else 0L,
        channelName = this.channelName,
        appPackage = this.appPackage,
        category = this.category,
        isSynced = false,
        channelLink = this.channelLink,
        channelPhoto = this.channelPhoto,
        uuid = if(isEdit) this.uuid else UUID.randomUUID().toString()
    )
}

