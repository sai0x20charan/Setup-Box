package com.charan.shared.data.repository


import com.charan.shared.data.remote.model.ChannelDTO
import com.charan.shared.data.model.AccountInfo
import com.charan.shared.utils.ProcessState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow

interface SupabaseRepo {
    suspend fun insertChannelData(channelContentDto: List<ChannelDTO>) : Flow<ProcessState<List<ChannelDTO>>>
    suspend fun getData():Flow<ProcessState<List<ChannelDTO>>>
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
    suspend fun getAccountInfo(): AccountInfo
}
