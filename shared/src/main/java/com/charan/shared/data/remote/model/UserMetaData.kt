package com.charan.shared.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserMetaData(
    val iss: String = "",
    val sub: String = "",
    val name: String = "",
    val email: String = "",
    val picture: String = "",
    @SerialName("full_name")
    val fullName: String = "",
    @SerialName("avatar_url")
    val avatarUrl: String = "",
    @SerialName("provider_id")
    val providerId: String = "",
    @SerialName("email_verified")
    val emailVerified: Boolean = false,
    @SerialName("phone_verified")
    val phoneVerified: Boolean = false
)
