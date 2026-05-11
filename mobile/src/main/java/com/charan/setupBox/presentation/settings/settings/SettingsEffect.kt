package com.charan.setupBox.presentation.settings.settings

sealed class SettingsEffect {
        object NavigateToLoginScreen : SettingsEffect()
        object NavigateToAccountScreen : SettingsEffect()
        object NavigateToAboutAppScreen : SettingsEffect()

        object NavigateBack : SettingsEffect()

        data class ShowToast(val message : String) : SettingsEffect()

        data class OpenLink(val url : String) : SettingsEffect()
}
