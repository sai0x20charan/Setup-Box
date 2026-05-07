package com.charan.shared.data.repository.impl

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.charan.shared.BuildConfig
import com.charan.shared.data.remote.SupabaseClient
import com.charan.shared.data.remote.model.ChannelContentDto
import com.charan.shared.data.remote.model.TVAuthenticationDTO
import com.charan.shared.data.repository.SupabaseRepo
import com.charan.shared.utils.AppConstants
import com.charan.shared.utils.AppConstants.SETUPBOXCONTENT
import com.charan.shared.utils.AppUtils
import com.charan.shared.utils.LoginState
import com.charan.shared.utils.ProcessState
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.auth.providers.builtin.OTP
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.functions.functions
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.realtime
import io.ktor.client.call.body
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.json.Json
import java.security.MessageDigest
import java.util.UUID
import kotlin.coroutines.coroutineContext

class SupabaseRepoImpl(
    private val supabaseClient: SupabaseClient,
    private val context : Context
) : SupabaseRepo {
    private val client = supabaseClient.client
    override suspend fun insertChannelData(channelContentDto: List<ChannelContentDto>): Flow<ProcessState<List<ChannelContentDto>>> =
        flow{
            emit(ProcessState.Loading())
            try {
                val data = client.from(SETUPBOXCONTENT).upsert(
                    values = channelContentDto

                ) {
                    onConflict = "uuid"
                }.decodeList<ChannelContentDto>()
                emit(ProcessState.Success(data))
            } catch (e: Exception) {
                Log.d("SupabaseRepoImpl", "Error inserting channel data: ${e.message}")
                emit(ProcessState.Error(e.message.toString()))
            }

        }

    override suspend fun getData(): Flow<ProcessState<ChannelContentDto>> =flow{
        emit(ProcessState.Loading())
        try {
        } catch (e: Exception) {
            Log.d("SupabaseRepoImpl", "Error fetching channel data: ${e.message}")
            emit(ProcessState.Error(e.message.toString()))
        }

    }

    override suspend fun authenticateGoogleIdToken(
    ): Flow<ProcessState<Boolean>> =flow{
        emit(ProcessState.Loading())
        try {
            val credentialManager = CredentialManager.create(context)
            val rowNonce = UUID.randomUUID().toString()
            val bytes = rowNonce.toByteArray()
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(bytes)
            val hashedNonce = digest.fold("") { str, it ->
                str + "%02x".format(it)

            }
            val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(BuildConfig.GOOGLE_SERVER_CLIENT_ID)
                .setNonce(hashedNonce)
                .build()
            val request: GetCredentialRequest = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(
                request = request,
                context = context
            )

            val credentaial = result.credential
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credentaial.data)

            val googleIdToken = googleIdTokenCredential.idToken
            supabaseClient.client.auth.signInWith(IDToken) {
                idToken = googleIdToken
                provider = Google
                nonce = rowNonce
            }
            emit(ProcessState.Success(true))


        } catch (e: Exception) {
            Log.d("SupabaseRepoImpl", "Error authenticating with Google ID token: ${e.message}")
            emit(ProcessState.Error(e.message.toString()))
        }
    }

    override suspend fun attachEmailIdToCode(code: String): Flow<ProcessState<Boolean>> =flow{
        emit(ProcessState.Loading())
        try {
            client.from("TVAuthentication").update(
                {
                    set("email", "")
                }
            )
                {
                    filter {
                        eq("tv_code",code)
                        eq("isAuthenticated",false)
                    }
                }


        } catch (e: Exception) {
            Log.d("SupabaseRepoImpl", "Error attaching email ID to code: ${e.message}")
            emit(ProcessState.Error(e.message.toString()))
        }

    }

    override suspend fun generateAuthenticationCode(): Flow<ProcessState<String>> =flow{
        emit(ProcessState.Loading())
        try {
            val response = client.functions.invoke(AppConstants.GENERATE_AUTHENTICATION_CODE_FUNCTION)
            val code = response.body<TVAuthenticationDTO>()
            emit(ProcessState.Success(code.tv_code ?: ""))

        } catch (e: Exception) {
            Log.d("SupabaseRepoImpl", "Error adding code to TV table: ${e.message}")
            emit(ProcessState.Error(e.message.toString()))
        }

    }

    override suspend fun observeCodeAuthenticationStatus(code: String): Flow<ProcessState<Boolean>> =flow{
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
                    val data = Json.decodeFromString<TVAuthenticationDTO>(it.record.toString())
                    if(data.isAuthenticated == true){
                        emit(ProcessState.Success(true))
                        channel.unsubscribe()
                    }

                }
                else -> Unit
            }
        }.launchIn(CoroutineScope(coroutineContext))
        channel.subscribe()
    }

    override suspend fun authenticationBySessionId(code: String) {
        TODO("Not yet implemented")
    }

    override suspend fun sentOTPLogin(mailId: String): Flow<ProcessState<String>> =flow{
        emit(ProcessState.Loading())
        try {
            supabaseClient.client.auth.signInWith(OTP){
                email = mailId
            }
            emit(ProcessState.Success("OTP sent successfully to $mailId"))


        } catch (e: Exception) {
            Log.d("SupabaseRepoImpl", "Error sending OTP for login: ${e.message}")
            emit(ProcessState.Error(e.message.toString()))
        }

    }

    override suspend fun verifyOTP(
        mainId: String,
        otp: String
    ): Flow<ProcessState<Boolean>> =flow{
        emit(ProcessState.Loading())
        try {
            supabaseClient.client.auth.verifyEmailOtp(type = OtpType.Email.EMAIL,email = mainId, token = otp)
            emit(ProcessState.Success(true))
            supabaseClient.client.realtime.removeAllChannels()

        } catch (e: Exception) {
            Log.d("SupabaseRepoImpl", "Error verifying OTP: ${e.message}")
            emit(ProcessState.Error(e.message.toString()))
        }

    }

    override suspend fun checkAuthenticationStatus(): Flow<ProcessState<Boolean>> {
        TODO("Not yet implemented")
    }

    override suspend fun updateAuthenticationStatus(code: String) {
        try{
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

    } catch (e:Exception)
    {
        Log.d("TAG", "attachSessionId: $e")

    }
    }

    override suspend fun logout(): Flow<ProcessState<Boolean>> =flow{
        emit(ProcessState.Loading())
        try {
            supabaseClient.client.auth.signOut()
            emit(ProcessState.Success(true))

        } catch (e: Exception) {
            Log.d("SupabaseRepoImpl", "Error during logout: ${e.message}")
            emit(ProcessState.Error(e.message.toString()))
        }

    }

    override suspend fun loadSession(): Boolean {
        return supabaseClient.client.auth.loadFromStorage()
    }
}
