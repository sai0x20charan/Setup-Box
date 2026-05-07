package com.charan.setupBox.data.repository.impl

import android.util.Log
import com.charan.setupBox.data.local.entity.SetupBoxContent
import com.charan.setupBox.data.remote.responsedto.TVAuthentication
import com.charan.setupBox.data.repository.SupabaseRepo
import com.charan.setupBox.repository.SetUpBoxContentRepository
import com.charan.setupBox.utils.LoginState
import com.charan.setupBox.utils.ProcessState
import com.charan.shared.data.remote.model.ChannelContentDto
import com.charan.shared.data.remote.model.TVAuthenticationDTO
import com.charan.shared.data.repository.SupabaseRepo as SharedSupabaseRepo
import com.charan.shared.data.repository.impl.SupabaseRepoImpl
import com.charan.shared.sync.SyncManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SupabaseRepoImp @Inject constructor(
    private val setUpBoxRepo: SetUpBoxContentRepository,
    private val syncManager: SyncManager,
    private val sharedSupabaseRepo: SharedSupabaseRepo
) : SupabaseRepo {

    override suspend fun getDataFromSupabase() {
        try {
            val backendRepo = sharedSupabaseRepo as SupabaseRepoImpl
            syncManager.sync(
                fetchRemote = { backendRepo.getChannelContentList().map { it.toTvEntity() } },
                fetchLocal = { setUpBoxRepo.getAllDataNonLiveData() },
                remoteKey = { it.uuid },
                localKey = { it.uuid },
                insertLocal = { setUpBoxRepo.insert(it) },
                removeLocal = { localItem -> localItem.id?.let { setUpBoxRepo.deleteById(it) } },
                updateLocal = { setUpBoxRepo.update(it) }
            )
        } catch (e: Exception) {
            Log.e("SupabaseError", e.message.toString())
        }
    }

    override suspend fun addCodeToTVTable(): Flow<LoginState> {
        return sharedSupabaseRepo.addCodeToTVTable().mapToTvLoginState()
    }

    override suspend fun observeData(code: String): Flow<TVAuthentication?> {
        return flow {
            sharedSupabaseRepo.observeData(code).collect {
                emit(it?.toTvAuth())
            }
        }
    }

    override suspend fun authenticationBySessionId(code: String) {
        sharedSupabaseRepo.authenticationBySessionId(code)
    }

    override suspend fun sentOTPLogin(mailId: String): Flow<LoginState> {
        return sharedSupabaseRepo.sentOTPLogin(mailId).mapToTvLoginState()
    }

    override suspend fun verifyOTP(mainId: String, otp: String): Flow<LoginState> {
        return sharedSupabaseRepo.verifyOTP(mainId, otp).mapToTvLoginState()
    }

    override suspend fun checkAuthenticationStatus(): Flow<ProcessState> {
        return sharedSupabaseRepo.checkAuthenticationStatus().mapToTvProcessState()
    }

    override suspend fun updateAuthenticationStatus(code: String) {
        sharedSupabaseRepo.updateAuthenticationStatus(code)
    }

    override suspend fun logout(): Flow<ProcessState> {
        return flow {
            sharedSupabaseRepo.logout().collect { state ->
                when (state) {
                    is com.charan.shared.utils.ProcessState.Success -> {
                        setUpBoxRepo.clearAllData()
                        emit(ProcessState.Success())
                    }
                    is com.charan.shared.utils.ProcessState.Loading -> emit(ProcessState.Loading)
                    is com.charan.shared.utils.ProcessState.Error -> emit(ProcessState.Error(state.error))
                }
            }
        }
    }

    private fun ChannelContentDto.toTvEntity(): SetupBoxContent = SetupBoxContent(
        id = id,
        channelLink = channelLink,
        channelName = channelName,
        channelPhoto = channelPhoto,
        Category = Category,
        app_Package = app_Package,
        uuid = uuid,
        email = email
    )

    private fun TVAuthenticationDTO.toTvAuth(): TVAuthentication = TVAuthentication(
        id = id,
        tv_code = tv_code,
        email = email,
        created_at = created_at,
        isAuthenticated = isAuthenticated
    )

    private fun Flow<com.charan.shared.utils.ProcessState>.mapToTvProcessState(): Flow<ProcessState> = flow {
        collect {
            when (it) {
                is com.charan.shared.utils.ProcessState.Success -> emit(ProcessState.Success())
                is com.charan.shared.utils.ProcessState.Loading -> emit(ProcessState.Loading)
                is com.charan.shared.utils.ProcessState.Error -> emit(ProcessState.Error(it.error))
            }
        }
    }

    private fun Flow<com.charan.shared.utils.LoginState>.mapToTvLoginState(): Flow<LoginState> = flow {
        collect {
            when (it) {
                is com.charan.shared.utils.LoginState.CodeGenerated -> emit(LoginState.CodeGenerated(it.code))
                is com.charan.shared.utils.LoginState.CodeGeneratedError -> emit(LoginState.CodeGeneratedError(it.error))
                is com.charan.shared.utils.LoginState.OTPSentTo -> emit(LoginState.OTPSentTo(it.email))
                is com.charan.shared.utils.LoginState.OTPError -> emit(LoginState.OTPError(it.error))
                is com.charan.shared.utils.LoginState.OTPVerified -> emit(LoginState.OTPVerified)
                is com.charan.shared.utils.LoginState.OTPVerificationError -> emit(LoginState.OTPVerificationError(it.error))
                is com.charan.shared.utils.LoginState.Loading -> emit(LoginState.Loading)
            }
        }
    }
}
