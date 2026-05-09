package com.charan.setupBox.presentation.home

sealed class HomeEvent {
    data object OnRefreshClick : HomeEvent()

    data class OnChannelClick(val link : String, val appPackage : String) : HomeEvent()

    data object OnToggleModalSheet : HomeEvent()

    data object OnLogoutClick : HomeEvent()
}
