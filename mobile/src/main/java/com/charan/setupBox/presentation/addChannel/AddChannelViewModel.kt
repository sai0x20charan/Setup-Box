package com.charan.setupBox.presentation.addChannel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    val channelLink: String = checkNotNull(savedStateHandle[AddNewChannelScreenNav::channelLink.name])
    val id: Long? = savedStateHandle[AddNewChannelScreenNav::id.name]

    init {
        if(id !=null){
            fetchChannelData()
        }

        if(channelLink.isNotEmpty()){
            handleChannelLinkChange(channelLink)
        }
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
                deleteChannel(state.value.channelData.uuid)
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

                AddChannelEvent.OnNavigateBack -> {
                    handleEffects(AddChannelEffect.NavigateBack)
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
        channelLocalRepository.getById(id!!).toChannelData()?.let { data->
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

     private fun saveChannel() =viewModelScope.launch(Dispatchers.IO){
         val channelData = state.value.channelData.toChannelEntity(state.value.isEdit)
            channelLocalRepository.upsert(channelData)
         handleEffects(AddChannelEffect.NavigateBack)


    }

     private fun deleteChannel(uuid: String) = viewModelScope.launch(Dispatchers.IO) {
         channelLocalRepository.deleteByUUID(uuid)
         handleEffects(AddChannelEffect.NavigateBack)
     }

    private fun handleEffects(effect: AddChannelEffect) = viewModelScope.launch{
        _effect.emit(effect)
    }

}
