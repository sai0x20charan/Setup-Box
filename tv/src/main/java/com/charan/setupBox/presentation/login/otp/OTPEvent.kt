package com.charan.setupBox.presentation.login.otp

sealed class OTPEvent {
    data class OnOTPCodeChange(val code: String) : OTPEvent()

    data object OnOTPCodeVerifyClick : OTPEvent()
}
