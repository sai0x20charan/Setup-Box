package com.charan.setupBox.presentation.settings.settings

sealed class SettingsEvent {
    object OnLogoutClick : SettingsEvent()
    object OnAccountClick : SettingsEvent()
    object OnAboutAppClick : SettingsEvent()
    object OnToggleAuthenticationSheet : SettingsEvent()

    data class OnAuthenticateCodeChange(val code : String) : SettingsEvent()

    data object OnAuthenticateClick : SettingsEvent()
}
