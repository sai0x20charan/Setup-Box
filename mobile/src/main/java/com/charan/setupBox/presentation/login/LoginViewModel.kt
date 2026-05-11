package com.charan.setupBox.presentation.login
import android.util.Log
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

    private val _effect = MutableSharedFlow<LoginEffect>()
    val effect = _effect.asSharedFlow()

    fun onEvent(event : LoginEvent) {
        when(event){
            LoginEvent.OnLoginWithGoogleClick -> {
                loginWithGoogle()

            }
        }

    }

    private fun loginWithGoogle() = viewModelScope.launch{
        supabaseRepo.authenticateGoogleIdToken().collectLatest {
            when(it){
                is ProcessState.Error -> {
                    handleLoading(false)
                    sendEffect(LoginEffect.ShowError(it.exception))
                }
                is ProcessState.Loading -> {
                    handleLoading(true)
                }

                ProcessState.NotDetermined -> {}
                is ProcessState.Success<*> -> {
                    handleLoading(false)
                    sendEffect(LoginEffect.NavigateToHomeScreen)
                }
            }

        }


    }

    private fun handleLoading(isLoading: Boolean){
        _state.update { currentState->
            currentState.copy(
                isAuthenticating = isLoading
            )
        }
    }

    private fun sendEffect(effect: LoginEffect)= viewModelScope.launch{
        _effect.emit(effect)
    }
}
