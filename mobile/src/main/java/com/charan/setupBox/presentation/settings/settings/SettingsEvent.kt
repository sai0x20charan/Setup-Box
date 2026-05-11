package com.charan.setupBox.presentation.settings.settings

sealed class SettingsEvent {
    object OnLogoutClick : SettingsEvent()
    object OnAccountClick : SettingsEvent()
    object OnAboutAppClick : SettingsEvent()
    object OnToggleAuthenticationSheet : SettingsEvent()

    object OnBackClick : SettingsEvent()

    data class OnAuthenticateCodeChange(val code : String) : SettingsEvent()

    data object OnAuthenticateClick : SettingsEvent()

    data object OnNavigateBack : SettingsEvent()

    data object OnOpenSourceLicensesClick : SettingsEvent()

    data object OnOpenGitHubClick : SettingsEvent()

    data object OnToggleLogoutDialog : SettingsEvent()
}
