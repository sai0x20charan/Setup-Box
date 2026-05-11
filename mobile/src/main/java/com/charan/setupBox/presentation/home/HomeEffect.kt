package com.charan.setupBox.presentation.home

sealed class HomeEffect {
    data class NavigateToAddChannelScreen(val id : String?) : HomeEffect()
    object NavigateToSettingsScreen : HomeEffect()

    data class ShowError(val message : String) : HomeEffect()
}
