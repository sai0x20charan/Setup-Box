package com.charan.setupBox.presentation.addChannel

import com.charan.setupBox.presentation.common.model.ChannelData
import com.charan.shared.data.enums.Categories

data class AddChannelState(
    val channelData : ChannelData = ChannelData(),
    val isSaving : Boolean = false,
    val showPreviewBox : Boolean = false,
    val showDeleteConfirmation : Boolean = false,
    val isEdit : Boolean = false,
    val distinctAppPackages : List<String> = emptyList(),
    val categories : List<String> = Categories.entries.map { it.name },
    val showCategoryDropDown : Boolean = false,
)


