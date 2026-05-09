package com.charan.setupBox.presentation.login.login

data class LoginState(
    val authenticationCode : String = "",
    val isLoading : Boolean = false
)
