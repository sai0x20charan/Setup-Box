package com.charan.shared.utils

sealed class LoginState {
    data class CodeGenerated(val code : String) : LoginState()
    data class CodeGeneratedError(val error : String)  : LoginState()
    data class OTPSentTo(val email : String) : LoginState()
    data class OTPError(val error : String) : LoginState()
    data object  OTPVerified : LoginState()
    data class OTPVerificationError(val error : String) : LoginState()
    data object Loading : LoginState()
}