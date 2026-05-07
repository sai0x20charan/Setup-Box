package com.charan.shared.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class TVAuthenticationDTO(
    val id : Int? = null,
    val tv_code : String? = null,
    val email : String? = null,
    val created_at : String? = null,
    val isAuthenticated : Boolean? = null
)
