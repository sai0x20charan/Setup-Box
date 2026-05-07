package com.charan.setupBox.presentation.login

sealed class LoginEvent {
    data object OnLoginWithGoogleClick : LoginEvent()
}
