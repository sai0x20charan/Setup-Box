package com.charan.shared.data.repository

import com.charan.shared.data.remote.model.ChannelContentDto
import com.charan.shared.utils.ProcessState
import kotlinx.coroutines.flow.Flow

interface SupabaseRepo {
    suspend fun insertChannelData(channelContentDto: List<ChannelContentDto>) : Flow<ProcessState<List<ChannelContentDto>>>
    suspend fun getData():Flow<ProcessState<List<ChannelContentDto>>>
    suspend fun authenticateGoogleIdToken(): Flow<ProcessState<Boolean>>
    suspend fun attachEmailIdToCode(code : String) : Flow<ProcessState<Boolean>>
    suspend fun generateAuthenticationCode() : Flow<ProcessState<String>>

    suspend fun observeCodeAuthenticationStatus(code : String): Flow<ProcessState<String>>

    suspend fun authenticationBySessionId(code : String)

    suspend fun sentOTPLogin(mailId : String) : Flow<ProcessState<String>>

    suspend fun verifyOTP(mailId : String, otp : String) : Flow<ProcessState<Boolean>>

    suspend fun checkAuthenticationStatus() : Flow<ProcessState<Boolean>>

    suspend fun updateAuthenticationStatus(code : String)
    suspend fun logout() : Flow<ProcessState<Boolean>>

    suspend fun loadSession() : Boolean
    suspend fun getEmail() : String?

    suspend fun getProfileImageUrl() : String?
}
