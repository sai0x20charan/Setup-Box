package com.charan.shared.data.mappers

import com.charan.shared.data.local.entity.ChannelEntity
import com.charan.shared.data.remote.model.ChannelContentDto

fun ChannelContentDto.toChannelEntity() : ChannelEntity {
    return ChannelEntity(
        id = this.id ?: 0L,
        uuid = this.uuid ?: "",
        channelName = this.channelName ?: "",
        channelPhoto = this.channelPhoto ?: "",
        channelLink = this.channelLink ?: "",
        isSynced = true
    )
}

fun List<ChannelContentDto>.toChannelEntityList() : List<ChannelEntity> {
    return this.map { it.toChannelEntity() }
}

fun ChannelEntity.toChannelContentDto() : ChannelContentDto {
    return ChannelContentDto(
        id = this.id,
        uuid = this.uuid,
        channelName = this.channelName,
        channelPhoto = this.channelPhoto,
        channelLink = this.channelLink
    )
}

fun List<ChannelEntity>.toChannelContentDtoList() : List<ChannelContentDto> {
    return this.map { it.toChannelContentDto() }
}