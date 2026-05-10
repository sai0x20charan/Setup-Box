package com.charan.setupBox.presentation.settings.settings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charan.shared.data.model.AppInfo
import com.charan.shared.data.repository.ChannelLocalRepository
import com.charan.shared.data.repository.SupabaseRepo
import com.charan.shared.utils.AppConstants.GITHUB_URL
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
    private val channelLocalRepository: ChannelLocalRepository,
    private val appInfo : AppInfo
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState(
        appInfo = AppDisplayInfo(
            versionName = appInfo.versionName,
            versionCode = appInfo.versionCode,
            isDebug = appInfo.isDebug
        )
    ))
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<SettingsEffect?>()
    val effect = _effect.asSharedFlow()

    init {
        accountDetails()
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

            SettingsEvent.OnNavigateBack -> {
                sendEffect(SettingsEffect.NavigateBack)
            }

            SettingsEvent.OnOpenGitHubClick -> {
                sendEffect(SettingsEffect.OpenLink(GITHUB_URL))
            }
            SettingsEvent.OnOpenSourceLicensesClick -> {
                sendEffect(SettingsEffect.NavigateToAboutAppScreen)
            }

            SettingsEvent.OnToggleLogoutDialog -> {
                handleLogoutDialogToggle()
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
                    handleAuthenticationLoading(false)
                    sendEffect(SettingsEffect.ShowMessage(it.exception))

                }
                is ProcessState.Loading -> {
                    handleAuthenticationLoading(true)
                }
                ProcessState.NotDetermined -> {}
                is ProcessState.Success<*> -> {
                    sendEffect(SettingsEffect.ShowMessage("TV Authenticated Successfully"))
                    handleAuthenticationSheetToggle()
                    handleAuthenticateCodeChange("")
                    handleAuthenticationLoading(false)
                }
            }

        }

    }
    private fun handleAuthenticationLoading(isLoading: Boolean){
        _state.update { currentState->
            currentState.copy(
                isAuthenticating = isLoading
            )
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
        val accountInfo = supabaseRepo.getAccountInfo()
        _state.update {currentState->
            currentState.copy(
                userInfo = UserInfo(
                    profilePic = accountInfo.profilePicUrl,
                    userName = accountInfo.userName,
                    email = accountInfo.email
                ),
            )
        }
    }

    private fun handleLogoutDialogToggle() = viewModelScope.launch {
        _state.update { currentState->
            currentState.copy(
                showLogoutDialog = !currentState.showLogoutDialog
            )
        }
    }
}
