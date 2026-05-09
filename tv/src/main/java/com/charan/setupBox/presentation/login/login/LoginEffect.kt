package com.charan.setupBox.presentation.login.login

sealed class LoginEffect {
    data class ShowToast(val message : String) : LoginEffect()

    data class NavigateToOTPScreen(val email: String) : LoginEffect()

    data object NavigateToHomeScreen : LoginEffect()
}
