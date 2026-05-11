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
        observeLocalChannelData()
        observeSyncStatus()
        fetchRemoteChannelData()
    }

    private fun observeLocalChannelData() = viewModelScope.launch(Dispatchers.IO) {
        channelLocalRepository.getAllActiveData().collectLatest { data ->
            _homeState.update {
                it.copy(
                    allChannelData = data.toChannelDataList()
                )
            }
        }
    }

    private fun fetchRemoteChannelData() = viewModelScope.launch(Dispatchers.IO) {
        syncManager.fetchAndUpdateData().collectLatest {
            when (it) {
                is ProcessState.Error -> {
                    handleLoading(false)
                    sendEffect(HomeEffect.ShowError(it.exception))
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

    private fun observeSyncStatus() = viewModelScope.launch {
        syncManager.observeSyncStatus().collectLatest { status ->
            _homeState.update {
                it.copy(
                    syncState = it.syncState.copy(
                        isSyncing = status.isSyncing,
                        hasError = status.hasError,
                        errorMessage = status.errorMessage
                    )
                )
            }
        }
    }

    private fun syncData() = viewModelScope.launch (Dispatchers.IO){
        syncManager.syncData()
    }

    fun onEvent(event : HomeEvent){
        when(event){
            is HomeEvent.OnChannelClick -> {
                sendEffect(HomeEffect.NavigateToAddChannelScreen(event.id))
            }
            HomeEvent.OnRefresh -> {
                fetchRemoteChannelData()
            }
            HomeEvent.OnSettingsClick -> {
                sendEffect(HomeEffect.NavigateToSettingsScreen)
            }
            HomeEvent.ToggleShowDropDown -> {
                handleShowDropDown()

            }

                HomeEvent.OnSyncClick -> {
                   syncData()
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
                isFetchingData = isLoading
            )
        }
    }


    private fun sendEffect(effect: HomeEffect) = viewModelScope.launch{
        _homeEffects.emit(effect)


    }



}
