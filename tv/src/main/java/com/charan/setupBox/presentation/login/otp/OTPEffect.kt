package com.charan.setupBox.presentation.login.otp

sealed class OTPEffect {
    data class ShowToast(val message: String) : OTPEffect()

    data object NavigateToHomeScreen : OTPEffect()
}
