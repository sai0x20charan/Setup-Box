package com.charan.setupBox.presentation.home

import com.charan.setupBox.presentation.common.model.ChannelData

data class HomeState(
    val allChannelData : List<ChannelData> = emptyList(),
    val loading : Boolean = false,
    val showDropDown : Boolean = false
)
