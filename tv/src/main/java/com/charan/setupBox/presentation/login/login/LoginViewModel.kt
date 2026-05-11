package com.charan.setupBox.presentation.login.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charan.shared.data.repository.SupabaseRepo
import com.charan.shared.utils.ProcessState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val supabaseRepo: SupabaseRepo
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<LoginEffect?>()
    val effect = _effect.asSharedFlow()

    init {
        getPairingCode()
    }

    private fun getPairingCode() = viewModelScope.launch {
        supabaseRepo.generatePairingCode().collectLatest {
            when (it) {
                is ProcessState.Error -> {
                    handleLoading(false)
                    handleEffects(LoginEffect.ShowToast(it.exception))
                }
                is ProcessState.Loading -> {
                    handleLoading(true)
                }
                ProcessState.NotDetermined -> {}
                is ProcessState.Success<String> -> {
                    handleLoading(false)
                    handleCode(it.data)
                    observeOTPStatus(it.data)
                }
            }
        }
    }

    private fun observeOTPStatus(code: String) = viewModelScope.launch {
        supabaseRepo.observePairingCodeStatus(code).collectLatest {
            when (it) {
                is ProcessState.Error -> {
                    handleEffects(LoginEffect.ShowToast(it.exception))
                }
                is ProcessState.Loading -> {}
                ProcessState.NotDetermined -> {}
                is ProcessState.Success<String> -> {
                    handleEffects(LoginEffect.NavigateToOTPScreen(it.data))
                }
            }
        }
    }

    private fun handleLoading(isLoading: Boolean) {
        _state.update {
            it.copy(
                isLoading = isLoading
            )
        }
    }

    private fun handleCode(code: String) {
        _state.update {
            it.copy(
                authenticationCode = code
            )
        }
    }

    private fun handleEffects(effect: LoginEffect?) = viewModelScope.launch {
        _effect.emit(effect)
    }
}
