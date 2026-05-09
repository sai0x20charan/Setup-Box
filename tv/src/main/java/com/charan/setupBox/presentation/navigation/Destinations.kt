package com.charan.setupBox.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
object HomeScreenNav

@Serializable
object LoginScreenNav

@Serializable
data class OTPScreenNav(val email: String)

@Serializable
object SettingsScreenNav
