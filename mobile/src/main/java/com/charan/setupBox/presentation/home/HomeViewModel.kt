package com.charan.setupBox.presentation.home
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charan.setupBox.presentation.common.mappers.toChannelDataList
import com.charan.shared.data.repository.ChannelLocalRepository
import com.charan.shared.data.repository.SyncManager
import com.charan.shared.utils.ProcessState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val channelLocalRepository: ChannelLocalRepository,
    private val syncManager: SyncManager
) : ViewModel() {

    private val _homeState = MutableStateFlow(HomeState())
    val homeState = _homeState.asStateFlow()

    private val _homeEffects = MutableSharedFlow<HomeEffect?>()
    val homeEffect = _homeEffects.asSharedFlow()

    init {
        fetchDataFromRemote()
        loadData()
    }

    private fun loadData() = viewModelScope.launch(Dispatchers.IO){
        channelLocalRepository.getAllData().collectLatest { data->
            _homeState.update {
                it.copy(
                    allChannelData = data.toChannelDataList()
                )
            }
        }
    }

    private fun fetchDataFromRemote() = viewModelScope.launch(Dispatchers.IO) {
        syncManager.fetchAndUpdateData().collectLatest {
            when(it){
                is ProcessState.Error -> {
                    handleEffects(HomeEffect.ShowError(it.exception))
                }
                is ProcessState.Loading -> {
                        handleLoading(true)

                }
                ProcessState.NotDetermined -> {}
                is ProcessState.Success<*> -> {
                    handleLoading(false)
                }
            }
        }
    }

    fun onEvent(event : HomeEvent){
        when(event){
            is HomeEvent.OnChannelClick -> {
                handleEffects(HomeEffect.NavigateToAddChannelScreen(event.id))
            }
            HomeEvent.OnRefresh -> {
                fetchDataFromRemote()
            }
            HomeEvent.OnSettingsClick -> {
                handleEffects(HomeEffect.NavigateToSettingsScreen)
            }
            HomeEvent.ToggleShowDropDown -> {
                handleShowDropDown()

            }
        }
    }

    private fun handleShowDropDown(){
        _homeState.update {
            it.copy(
                showDropDown = !it.showDropDown
            )
        }
    }

    private fun handleLoading(isLoading: Boolean){
        _homeState.update { currentState->
            currentState.copy(
                loading = isLoading
            )
        }
    }


    private fun handleEffects(effect: HomeEffect) = viewModelScope.launch{
        _homeEffects.emit(effect)


    }



}
