package com.charan.setupBox.presentation.settings.settings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.charan.shared.data.repository.ChannelLocalRepository
import com.charan.shared.data.repository.SupabaseRepo
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
class SettingsViewModel @Inject constructor(
    private val supabaseRepo: SupabaseRepo,
    private val channelLocalRepository: ChannelLocalRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<SettingsEffect?>()
    val effect = _effect.asSharedFlow()

    init {
        accountDetails()
        println("SettingsViewModel initialized}")
    }

    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.OnLogoutClick -> {
                handleLogout()
            }

            is SettingsEvent.OnAccountClick -> {
                sendEffect(SettingsEffect.NavigateToAccountScreen)
            }

            is SettingsEvent.OnAboutAppClick -> {
                sendEffect(SettingsEffect.NavigateToAboutAppScreen)
            }

            SettingsEvent.OnBackClick -> {
                sendEffect(SettingsEffect.NavigateBack)
            }

            SettingsEvent.OnAuthenticateClick -> {
                handleAuthenticateClick()
            }
            is SettingsEvent.OnAuthenticateCodeChange -> {
                handleAuthenticateCodeChange(event.code)
            }
            SettingsEvent.OnToggleAuthenticationSheet -> {
                handleAuthenticationSheetToggle()
            }
        }
    }

    private fun handleAuthenticationSheetToggle() {
        _state.update {
            currentState->
            currentState.copy(
                showEnterCodeDialog = !currentState.showEnterCodeDialog
            )
        }
    }

    private fun handleAuthenticateCodeChange(code: String) {
        _state.update { currentState->
            currentState.copy(
                code = code
            )
        }
    }

    private fun handleAuthenticateClick() = viewModelScope.launch{
        supabaseRepo.attachEmailIdToCode(state.value.code).collectLatest {
            when(it){
                is ProcessState.Error -> {

                }
                is ProcessState.Loading -> {}
                ProcessState.NotDetermined -> {}
                is ProcessState.Success<*> -> {
                    sendEffect(SettingsEffect.ShowMessage("TV Authenticated Successfully"))
                    handleAuthenticationSheetToggle()
                    handleAuthenticateCodeChange("")
                }
            }

        }

    }

    private fun handleLogout() = viewModelScope.launch(Dispatchers.IO){
        supabaseRepo.logout().collectLatest {
            when(it){
                is ProcessState.Error -> {

                }
                is ProcessState.Loading -> {

                }
                ProcessState.NotDetermined -> {}
                is ProcessState.Success<*> -> {
                    sendEffect(SettingsEffect.NavigateToLoginScreen)
                    channelLocalRepository.clearAllData()
                }
            }
        }

    }

    private fun sendEffect(effect: SettingsEffect?) = viewModelScope.launch{
        _effect.emit(effect)
    }

    private fun accountDetails() = viewModelScope.launch {
        _state.update {
            it.copy(
                profilePic = supabaseRepo.getProfileImageUrl() ?: "",
                email = supabaseRepo.getEmail() ?: "",
            )
        }
    }
}
