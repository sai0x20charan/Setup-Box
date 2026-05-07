package com.charan.setupBox.presentation.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charan.setupBox.data.repository.SupabaseRepo
import com.charan.setupBox.utils.LoginState
import com.charan.setupBox.utils.ProcessState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val supabaseRepo: SupabaseRepo
) : ViewModel() {

    data class UiState(
        val authenticationStatus: ProcessState? = null,
        val otpTextField: String = "",
        val loginState: LoginState? = null
    )

    sealed interface Intent {
        data object GetAuthenticationCode : Intent
        data class ObserveOtpStatus(val code: String) : Intent
        data class VerifyOtpStatus(val email: String) : Intent
        data object CheckAuthenticationStatus : Intent
        data object ConsumeLoginState : Intent
        data object ConsumeAuthenticationStatus : Intent
        data class UpdateOtpText(val code: String) : Intent
        data object ResetOtpText : Intent
        data class ChangeAuthenticationStatus(val code: String) : Intent
    }

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    fun onIntent(intent: Intent) {
        when (intent) {
            Intent.GetAuthenticationCode -> getAuthenticationCode()
            is Intent.ObserveOtpStatus -> observerOTPStatus(intent.code)
            is Intent.VerifyOtpStatus -> verifyOTPStatus(intent.email)
            Intent.CheckAuthenticationStatus -> authenticationStatus()
            Intent.ConsumeLoginState -> _uiState.update { it.copy(loginState = null) }
            Intent.ConsumeAuthenticationStatus -> _uiState.update { it.copy(authenticationStatus = null) }
            is Intent.UpdateOtpText -> _uiState.update { it.copy(otpTextField = intent.code) }
            Intent.ResetOtpText -> _uiState.update { it.copy(otpTextField = "") }
            is Intent.ChangeAuthenticationStatus -> changeAuthenticationStatus(intent.code)
        }
    }

    private fun getAuthenticationCode() {
        _uiState.update { it.copy(loginState = LoginState.Loading) }
        viewModelScope.launch(Dispatchers.IO) {
            supabaseRepo.addCodeToTVTable().collectLatest {
                _uiState.update { state -> state.copy(loginState = it) }
            }
        }
    }

    private fun observerOTPStatus(code: String) {
        viewModelScope.launch(Dispatchers.IO) {
            supabaseRepo.observeData(code).collectLatest {
                Log.d("TAG", "observerOTPStatus: $it")
                if (it?.email.isNullOrEmpty().not()) {
                    supabaseRepo.sentOTPLogin(it!!.email!!).collectLatest { loginState ->
                        _uiState.update { state -> state.copy(loginState = loginState) }
                    }
                }
            }
        }
    }

    private fun verifyOTPStatus(email: String) {
        _uiState.update { it.copy(loginState = LoginState.Loading) }
        viewModelScope.launch(Dispatchers.IO) {
            supabaseRepo.verifyOTP(email, _uiState.value.otpTextField).collectLatest {
                _uiState.update { state -> state.copy(loginState = it) }
            }
        }
    }

    private fun authenticationStatus() {
        viewModelScope.launch {
            supabaseRepo.checkAuthenticationStatus().collectLatest {
                _uiState.update { state -> state.copy(authenticationStatus = it) }
            }
        }
    }

    private fun changeAuthenticationStatus(code: String) {
        viewModelScope.launch {
            supabaseRepo.updateAuthenticationStatus(code)
        }
    }
}
