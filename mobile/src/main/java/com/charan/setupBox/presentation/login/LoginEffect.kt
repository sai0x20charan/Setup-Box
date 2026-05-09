package com.charan.setupBox.presentation.login

sealed class LoginEffect {
        object NavigateToHomeScreen : LoginEffect()
    data class ShowError(val message : String) : LoginEffect()
}
