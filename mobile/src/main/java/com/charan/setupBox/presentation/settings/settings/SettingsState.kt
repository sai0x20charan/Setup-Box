package com.charan.setupBox.presentation.settings.settings

data class SettingsState(
    val showEnterCodeDialog : Boolean = false,
    val code : String = "",
    val isLoading : Boolean = false,
)
