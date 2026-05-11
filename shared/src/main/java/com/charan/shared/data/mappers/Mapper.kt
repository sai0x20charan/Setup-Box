package com.charan.shared.data.mappers

import com.charan.shared.data.local.entity.ChannelEntity
import com.charan.shared.data.remote.model.ChannelDTO

fun ChannelDTO.toChannelEntity() : ChannelEntity {
    return ChannelEntity(
        id = this.id ?: "",
        name = this.name ?: "",
        thumbnailURL = this.thumbnailURL ?: "",
        url = this.url ?: "",
        category = this.category ?: "",
        appPackage = this.appPackage ?: "",
        isSynced = true
    )
}

fun List<ChannelDTO>.toChannelEntityList() : List<ChannelEntity> {
    return this.map { it.toChannelEntity() }
}

fun ChannelEntity.toChannelDto(email : String) : ChannelDTO {
    return ChannelDTO(
        id = this.id,
        name = this.name,
        thumbnailURL = this.thumbnailURL,
        url = this.url,
        category = this.category,
        appPackage = this.appPackage,
        email = email
    )
}

fun List<ChannelEntity>.toChannelDtoList(email : String) : List<ChannelDTO> {
    return this.map { it.toChannelDto(email) }
}