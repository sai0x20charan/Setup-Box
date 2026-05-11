package com.charan.shared.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class DevicePairingSessionDTO (
    val id : String? = null,
    val code : String? = null,
    val email : String? = null,
    val status : String = PairingSessionStatus.PENDING.status,
)

enum class PairingSessionStatus(val status : String ){
    PENDING("pending"),
    OTP_SENT("otp_sent"),
    VERIFIED("verified"),

}
