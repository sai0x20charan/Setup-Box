package com.charan.setupBox.presentation.home

sealed class HomeEvent {
    object OnRefresh : HomeEvent()
    object ToggleShowDropDown : HomeEvent()
    data class OnChannelClick(val id : Long? = null) : HomeEvent()
    object OnSettingsClick : HomeEvent()

}
