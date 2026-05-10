package com.charan.setupBox.presentation.home
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charan.setupBox.presentation.mapper.toChannelDataList
import com.charan.shared.data.repository.ChannelLocalRepository
import com.charan.shared.data.repository.SupabaseRepo
import com.charan.shared.data.repository.SyncManager
import com.charan.shared.utils.AppLauncher
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
    private val supabaseRepo: SupabaseRepo,
    private val syncManager: SyncManager,
    private val channelLocalRepository: ChannelLocalRepository,
    private val appLauncher: AppLauncher
) : ViewModel() {
    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<HomeEffect?>()
    val effect = _effect.asSharedFlow()

    init {
        observeData()
        loadData()
        initAccountData()
    }

    fun onEvent(event : HomeEvent){
        when(event){
            is HomeEvent.OnChannelClick -> {
                handleChannelClick(event.link, event.appPackage)

            }
            HomeEvent.OnRefreshClick -> {
                loadData()
            }

            HomeEvent.OnToggleModalSheet -> {
                handleModalSheetToggle()

            }

            HomeEvent.OnLogoutClick -> {
                handleLogout()

            }
        }
    }

    private fun handleLogout() = viewModelScope.launch(Dispatchers.IO) {
        supabaseRepo.logout().collectLatest {
            when(it){
                is ProcessState.Error -> {}
                is ProcessState.Loading -> {}
                ProcessState.NotDetermined -> {}
                is ProcessState.Success<*> -> {
                    channelLocalRepository.clearAllData()
                    handleEffects(HomeEffect.NavigateToLoginScreen)

                }
            }
        }
    }

    private fun handleChannelClick(link: String, packageName:String){
        appLauncher.openLink(
            appPackage = packageName,
            link = link
        )
    }

    private fun handleModalSheetToggle(){
        _state.update {
            it.copy(
                showModelSheet = !it.showModelSheet
            )
        }
    }

    private fun observeData() = viewModelScope.launch(Dispatchers.IO) {
        channelLocalRepository.getAllActiveData().collectLatest {
            val channels = it.toChannelDataList()
            val categories = channels.groupBy { it.channelCategory }.map {
                Log.d("TAG", "observeData: ${it.key}")
                ChannelCategory(
                    categoryName = it.key,
                    channels = it.value
                )
            }
            _state.update { it.copy(categories = categories) }
        }

    }

    private fun loadData() = viewModelScope.launch{
        syncManager.fetchAndUpdateData().collectLatest {
            when(it){
                is ProcessState.Error -> {}
                is ProcessState.Loading -> {}
                ProcessState.NotDetermined -> {}
                is ProcessState.Success<*> -> {

                }
            }

        }
    }

    private fun initAccountData() = viewModelScope.launch {
        val accountInfo = supabaseRepo.getAccountInfo()
        _state.update {
            it.copy(
                email = accountInfo.email,
                profileURL = accountInfo.profilePicUrl,
                userName = accountInfo.userName
            )
        }
    }

    private fun handleEffects(effect: HomeEffect) = viewModelScope.launch {
        _effect.emit(effect)
    }
}
