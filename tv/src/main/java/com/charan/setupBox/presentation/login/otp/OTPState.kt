package com.charan.setupBox.presentation.login.otp

data class OTPState(
    val otpCode: String = "",
    val email: String = "",
    val isLoading: Boolean = false
)
