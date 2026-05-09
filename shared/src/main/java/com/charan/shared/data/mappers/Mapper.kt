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
        category = this.category ?: "",
        appPackage = this.app_Package ?: "",
        isSynced = true
    )
}

fun List<ChannelContentDto>.toChannelEntityList() : List<ChannelEntity> {
    return this.map { it.toChannelEntity() }
}

fun ChannelEntity.toChannelContentDto(email : String) : ChannelContentDto {
    return ChannelContentDto(
        id = this.id,
        uuid = this.uuid,
        channelName = this.channelName,
        channelPhoto = this.channelPhoto,
        channelLink = this.channelLink,
        app_Package = this.appPackage,
        category = this.category,
        email = email
    )
}

fun List<ChannelEntity>.toChannelContentDtoList(email : String) : List<ChannelContentDto> {
    return this.map { it.toChannelContentDto(email) }
}