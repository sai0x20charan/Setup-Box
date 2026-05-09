package com.charan.shared.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class ChannelContentDto(
    val id:Long?=null,
    val channelLink:String?=null,
    val channelName:String?=null,
    val channelPhoto:String?=null,
    val category:String?=null,
    val app_Package:String?=null,
    val uuid : String? = null,
    val email : String? = null
)
