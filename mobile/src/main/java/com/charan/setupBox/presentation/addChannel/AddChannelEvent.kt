package com.charan.setupBox.presentation.addChannel



sealed class AddChannelEvent {
    data class InitializeChannel(val id: Int?, val sharedUrl: String?) : AddChannelEvent()
    data class OnChannelNameChange(val name: String) : AddChannelEvent()
    data class OnChannelLinkChange(val link: String) : AddChannelEvent()
    data class OnChannelPhotoChange(val photo: String) : AddChannelEvent()
    data class OnCategoryChange(val category: String) : AddChannelEvent()
    data class OnPackageChange(val packageName: String) : AddChannelEvent()
    data object OnToggleCategoryDropDown : AddChannelEvent()
    data object OnTogglePreviewBox : AddChannelEvent()
    data object OnToggleDeleteConfirmation : AddChannelEvent()
    data object OnSave : AddChannelEvent()
    data object OnDelete : AddChannelEvent()

    data object OnNavigateBack : AddChannelEvent()
}
