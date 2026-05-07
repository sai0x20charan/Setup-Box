package com.charan.setupBox.presentation.settings.settings
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<SettingsEffect?>()
    val effect = _effect.asSharedFlow()

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
            else ->{}
        }
    }

    private fun handleLogout(){

    }

    private fun sendEffect(effect: SettingsEffect?){
        _effect.tryEmit(effect)
    }
}
