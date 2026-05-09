package com.charan.setupBox.presentation.settings.settings

sealed class SettingsEffect {
        object NavigateToLoginScreen : SettingsEffect()
        object NavigateToAccountScreen : SettingsEffect()
        object NavigateToAboutAppScreen : SettingsEffect()

        object NavigateBack : SettingsEffect()

        data class ShowMessage(val message : String) : SettingsEffect()
}
