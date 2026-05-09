package com.charan.setupBox.presentation.login.otp

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.charan.setupBox.presentation.navigation.OTPScreenNav
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
class OTPViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val supabaseRepo: SupabaseRepo
) : ViewModel() {

    private val otpArgs = savedStateHandle.toRoute<OTPScreenNav>()

    private val _state = MutableStateFlow(OTPState(email = otpArgs.email))
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<OTPEffect?>()
    val effect = _effect.asSharedFlow()

    fun onEvent(event: OTPEvent) {
        when (event) {
            is OTPEvent.OnOTPCodeChange -> {
                handleOTPCodeChange(event.code)
            }

            OTPEvent.OnOTPCodeVerifyClick -> {
                handleOTPVerify()
            }
        }
    }

    private fun handleOTPCodeChange(code: String) {
        _state.update {
            it.copy(
                otpCode = code
            )
        }
    }

    private fun handleOTPVerify() = viewModelScope.launch {
        supabaseRepo.verifyOTP(
            mailId = state.value.email,
            otp = state.value.otpCode
        ).collectLatest {
            when (it) {
                is ProcessState.Error -> {
                    handleLoading(false)
                    handleEffects(OTPEffect.ShowToast(it.exception))
                }

                is ProcessState.Loading -> {
                    handleLoading(true)
                }

                ProcessState.NotDetermined -> {}
                is ProcessState.Success<*> -> {
                    handleLoading(false)
                    handleEffects(OTPEffect.NavigateToHomeScreen)
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

    private fun handleEffects(effect: OTPEffect?) = viewModelScope.launch {
        _effect.emit(effect)
    }
}
