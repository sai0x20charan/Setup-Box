package com.charan.setupBox.presentation.addChannel

sealed class AddChannelEffect {

    data object NavigateBack : AddChannelEffect()
    data class ShowToast(val message : String) : AddChannelEffect()

}
