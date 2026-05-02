package com.charan.setupBox.data.repository.impl

import android.se.omapi.Session
import android.util.Log
import com.charan.setupBox.data.local.entity.SetupBoxContent
import com.charan.setupBox.data.remote.responsedto.TVAuthentication
import com.charan.setupBox.data.remote.supabaseClient
import com.charan.setupBox.data.repository.SupabaseRepo
import com.charan.setupBox.repository.SetUpBoxContentRepository
import com.charan.setupBox.utils.AppConstants
import com.charan.setupBox.utils.AppUtils
import com.charan.setupBox.utils.LoginState
import com.charan.setupBox.utils.ProcessState
import com.charan.setupBox.utils.SupabaseUtils
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.auth.providers.builtin.OTP
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.realtime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.json.Json
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

class SupabaseRepoImp @Inject constructor(private val setUpBoxRepo: SetUpBoxContentRepository) : SupabaseRepo {
    override suspend fun getDataFromSupabase() {
        try {
            val remoteData = supabaseClient.client.from(AppConstants.SETUPBOXCONTENT).select()
                .decodeList<SetupBoxContent>()
            val localData = setUpBoxRepo.getAllDataNonLiveData()
            val localDataIds = localData.map { it.uuid }.toSet()
            val remoteDataIds = remoteData.map { it.uuid }.toSet()
            val itemsToInsert = remoteData.filter { it.uuid !in localDataIds }
            val itemsToRemove = localData.filter { it.uuid !in remoteDataIds }
            val itemsToUpdate = remoteData.filter { remoteItem ->
                localData.any { localItem ->
                    localItem.uuid == remoteItem.uuid && localItem != remoteItem
                }
            }
            itemsToInsert.forEach {
                setUpBoxRepo.insert(it)
            }
            itemsToRemove.forEach {
                setUpBoxRepo.deleteById(it.id!!)
            }
            itemsToUpdate.forEach { updatedItem ->
                setUpBoxRepo.update(updatedItem)
            }
        }  catch (e: Exception) {
            Log.e("SupabaseError", e.message.toString())
        }
    }

    override suspend fun addCodeToTVTable() : Flow<LoginState> {
        val codeState = MutableStateFlow<LoginState>(LoginState.Loading)
        try{
            val code = AppUtils.generateRandomString()
            supabaseClient.client.from(AppConstants.TVAUTHENTICATION).insert(
                TVAuthentication(
                    tv_code = code,
                    email = null,
                    isAuthenticated = false,
                    created_at = System.currentTimeMillis().toString()
                )


            )
            codeState.tryEmit(LoginState.CodeGenerated(code))
        } catch (e:Exception){
            Log.d("TAG", "addCodeToTVTable: $e")
            codeState.tryEmit(LoginState.CodeGeneratedError(e.message.toString()))
        }
        return codeState
    }

    @OptIn(SupabaseExperimental::class)
    override suspend fun observeData(code: String): Flow<TVAuthentication?> {
        val tvAuthentication = MutableStateFlow<TVAuthentication?>(null)
        val channel = supabaseClient.client.channel(AppConstants.TVAuthenticationChannelId) {
        }
        val changeFlow = channel.postgresChangeFlow<PostgresAction>(schema = "public") {
            table = AppConstants.TVAUTHENTICATION
            filter("tv_code", FilterOperator.EQ, code)
            filter("isAuthenticated",FilterOperator.EQ,false)
        }
        changeFlow.onEach {
            when(it){
                is PostgresAction.Update -> {
                    val data = Json.decodeFromString<TVAuthentication>(it.record.toString())
                    tvAuthentication.tryEmit(data)

                }
                else -> Unit
            }
        }.launchIn(CoroutineScope(coroutineContext))
        channel.subscribe()
        return tvAuthentication



    }


    override suspend fun authenticationBySessionId(code: String) {
        supabaseClient.client.auth.signInWith(IDToken){



        }
    }

    override suspend fun sentOTPLogin(mailId: String): Flow<LoginState> {
        val otpStatus = MutableStateFlow<LoginState>(LoginState.Loading)
        try {
            supabaseClient.client.auth.signInWith(OTP){
                email = mailId
            }
            otpStatus.tryEmit(LoginState.OTPSentTo(mailId))

        }catch (e:Exception){
            Log.d("TAG", "sentOTPLogin: $e")
            otpStatus.tryEmit(LoginState.OTPError(e.message.toString()))
        }
        return otpStatus

    }

    override suspend fun verifyOTP(mainId: String, otp: String) : Flow<LoginState> {
        val verficationStatus = MutableStateFlow<LoginState>(LoginState.Loading)
        try {
            supabaseClient.client.auth.verifyEmailOtp(type = OtpType.Email.EMAIL,email = mainId, token = otp)
            verficationStatus.tryEmit(LoginState.OTPVerified)
            supabaseClient.client.realtime.removeAllChannels()
        } catch (e:Exception){
            verficationStatus.tryEmit(LoginState.OTPVerificationError(e.message.toString()))
        }
        return verficationStatus

    }

    override suspend fun checkAuthenticationStatus(): Flow<ProcessState> {
        return supabaseClient.client.auth.sessionStatus.map { sessionStatus ->
            when (sessionStatus) {
                is SessionStatus.Authenticated -> {
                    println("Received new authenticated session.")
                    ProcessState.Success()
                }
                is SessionStatus.NotAuthenticated -> {
                    if (sessionStatus.isSignOut) {
                        println("User signed out")
                    } else {
                        println("User not signed in")
                    }
                    ProcessState.Error("User not authenticated")
                }
                is SessionStatus.Initializing -> {
                    println("Initializing authentication status...")
                    ProcessState.Loading
                }
                is SessionStatus.RefreshFailure -> {
                    ProcessState.Error("Session refresh failed: ${sessionStatus.cause}")
                }
            }
        }
    }

    override suspend fun updateAuthenticationStatus(code : String) {
        try {
            supabaseClient.client.from("TVAuthentication").update(
                {
                    set("isAuthenticated",true)
                }
            ){
                filter {
                    eq("tv_code",code)
                    eq("isAuthenticated",false)
                }
            }

        } catch (e:Exception){
            Log.d("TAG", "attachSessionId: $e")

        }
    }

    override suspend fun logout() : Flow<ProcessState> {
        val logoutState = MutableStateFlow<ProcessState>(ProcessState.Loading)
        try {
            supabaseClient.client.auth.signOut()
            setUpBoxRepo.clearAllData()
            logoutState.tryEmit(ProcessState.Success())


        } catch (e:Exception){
            logoutState.tryEmit(ProcessState.Error(e.message.toString()))

        }
        return logoutState
    }

}