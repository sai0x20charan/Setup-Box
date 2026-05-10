package com.charan.setupBox.presentation.settings.settings

data class SettingsState(
    val showEnterCodeDialog : Boolean = false,
    val code : String = "",
    val isAuthenticating : Boolean = false,
    val userInfo : UserInfo = UserInfo(),
    val appInfo : AppDisplayInfo = AppDisplayInfo(),
    val showLogoutDialog : Boolean = false
)

data class UserInfo(
    val profilePic : String="",
    val userName : String="",
    val email : String =""
)

data class AppDisplayInfo(
    val versionName: String = "",
    val versionCode: String = "",
    val isDebug: Boolean = false
)
