package com.charan.shared.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChannelDTO(
    val url : String? = null,
    val name : String? = null,
    @SerialName("app_package")
    val appPackage : String? = null,
    @SerialName("thumbnail_url")
    val thumbnailURL : String? = null,
    val category : String? = null,
    val email : String? = null,
    val id : String? = null

)