package com.charan.setupBox.presentation.addChannel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.charan.setupBox.presentation.common.mappers.toChannelData
import com.charan.setupBox.presentation.common.mappers.toChannelEntity
import com.charan.setupBox.presentation.navigation.AddNewChannelScreenNav
import com.charan.shared.data.repository.ChannelLocalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddChannelViewModel @Inject constructor(
    private val channelLocalRepository: ChannelLocalRepository,
    private val savedStateHandle: SavedStateHandle

) : ViewModel() {

    private val _state = MutableStateFlow(AddChannelState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<AddChannelEffect?>()
    val effect = _effect.asSharedFlow()

    val args = savedStateHandle.toRoute<AddNewChannelScreenNav>()

    init {
        if(args.id !=null){
            fetchChannelData()
        }

        if(!(args.channelLink.isNullOrEmpty())){
            handleChannelLinkChange(args.channelLink)
        }
        getPackageNames()
    }

    fun onEvent(event : AddChannelEvent) {
        when(event){
            is AddChannelEvent.InitializeChannel -> {}
            is AddChannelEvent.OnCategoryChange -> {
                handleCategoryChange(event.category)
            }

            is AddChannelEvent.OnChannelLinkChange -> {
                handleChannelLinkChange(event.link)
            }
            is AddChannelEvent.OnChannelNameChange -> {
                handleChannelNameChange(event.name)
            }
            is AddChannelEvent.OnChannelPhotoChange -> {
                handleChannelPhotoChange(event.photo)
            }
            is AddChannelEvent.OnDelete -> {
                deleteChannel(state.value.channelData.id)
            }
            is AddChannelEvent.OnPackageChange -> {
                handlePackageNameChange(event.packageName)
            }
            AddChannelEvent.OnSave -> {
                saveChannel()
            }
            AddChannelEvent.OnTogglePreviewBox -> {
                togglePreviewBox()
            }

            AddChannelEvent.OnToggleCategoryDropDown -> {
                handleCategoryDropDown()

            }
            AddChannelEvent.OnToggleDeleteConfirmation -> {
                handleDeletePopup()
            }

                AddChannelEvent.OnNavigateBack -> {
                    sendEffect(AddChannelEffect.NavigateBack)
                }

        }
    }

    private fun handleCategoryDropDown(){
        _state.update {
            it.copy(
                showCategoryDropDown = !it.showCategoryDropDown
            )
        }
    }

    private fun fetchChannelData() = viewModelScope.launch(Dispatchers.IO){
        channelLocalRepository.getById(args.id!!).toChannelData()?.let { data->
            _state.update {
                it.copy(
                    channelData = data,
                    isEdit = true
                )
            }

        }


    }

    private fun handleCategoryChange(category: String) {
        _state.update {
            it.copy(
                channelData = it.channelData.copy(
                    category = category
                )
            )
        }
    }
    private fun handleChannelLinkChange(link: String) {
        _state.update {
            it.copy(
                channelData = it.channelData.copy(
                    channelLink = link
                )
            )
        }
    }

     private fun handleChannelNameChange(name: String) {
        _state.update {
            it.copy(
                channelData = it.channelData.copy(
                    channelName = name
                )
            )
        }
    }

     private fun handleChannelPhotoChange(photo: String) {
        _state.update {
            it.copy(
                channelData = it.channelData.copy(
                    channelPhoto = photo
                )
            )
        }
    }

     private fun handlePackageNameChange(packageName: String) {
        _state.update {
            it.copy(
                channelData = it.channelData.copy(
                    appPackage = packageName
                )
            )
        }
    }


     private fun togglePreviewBox() {
        _state.update {
            it.copy(
                showPreviewBox = !it.showPreviewBox
            )
        }


    }

    private fun handleDeletePopup() {
        _state.update {
            it.copy(
                showDeleteConfirmation = !it.showDeleteConfirmation
            )
        }
    }

     private fun saveChannel() =viewModelScope.launch(Dispatchers.IO) {
         if(isDataValid()) {
             val channelData = state.value.channelData.toChannelEntity(state.value.isEdit)
             channelLocalRepository.upsert(channelData)
             sendEffect(AddChannelEffect.NavigateBack)
         }
     }

    private fun isDataValid(): Boolean {
        val channelData = state.value.channelData

        if(channelData.channelName.isEmpty()){
            sendEffect(AddChannelEffect.ShowToast("Channel name cannot be empty"))
            return false
        }

        if(channelData.channelLink.isEmpty()){
            sendEffect(AddChannelEffect.ShowToast("Channel link cannot be empty"))
            return false
        }

        if(channelData.appPackage.isEmpty()){
            sendEffect(AddChannelEffect.ShowToast("App package cannot be empty"))
            return false
        }

        if(channelData.category.isEmpty()){
            sendEffect(AddChannelEffect.ShowToast("Category cannot be empty"))
            return false
        }

        return true


    }

     private fun deleteChannel(uuid: String) = viewModelScope.launch(Dispatchers.IO) {
         channelLocalRepository.deleteByUUID(uuid)
         sendEffect(AddChannelEffect.NavigateBack)
     }

    private fun sendEffect(effect: AddChannelEffect) = viewModelScope.launch{
        _effect.emit(effect)
    }

    private fun getPackageNames() = viewModelScope.launch(Dispatchers.IO){
        val packageNames = channelLocalRepository.getDistinctPackages()
        _state.update {
            it.copy(
                distinctAppPackages = packageNames
            )
        }
    }

}
