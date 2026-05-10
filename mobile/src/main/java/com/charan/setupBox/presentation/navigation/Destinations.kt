package com.charan.setupBox.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
data class HomeScreenNav(
    val channelLink: String?
)

@Serializable
data class AddNewChannelScreenNav(
    val id : String? = null,
    val channelLink : String? = null
)

@Serializable
object LoginScreenNav

@Serializable
object TVAuthenticationNav

@Serializable
object AccountScreenNav

@Serializable
object AboutAppNav

@Serializable
object SettingsScreenNav

@Serializable
object LicenseScreenNav
