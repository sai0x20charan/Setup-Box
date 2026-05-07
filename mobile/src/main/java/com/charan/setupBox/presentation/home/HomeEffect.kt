package com.charan.setupBox.presentation.home

sealed class HomeEffect {
    data class NavigateToAddChannelScreen(val id : Long?) : HomeEffect()
    object NavigateToSettingsScreen : HomeEffect()
}
