package com.charan.shared.data.repository.impl

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.charan.shared.BuildConfig
import com.charan.shared.data.model.AccountInfo
import com.charan.shared.data.remote.SupabaseClient
import com.charan.shared.data.remote.model.ChannelDTO
import com.charan.shared.data.remote.model.DevicePairingSessionDTO
import com.charan.shared.data.remote.model.PairingSessionStatus
import com.charan.shared.data.remote.model.UserMetaData
import com.charan.shared.data.remote.model.TVAuthenticationDTO
import com.charan.shared.data.repository.SupabaseRepo
import com.charan.shared.utils.AppConstants
import com.charan.shared.utils.ProcessState
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.auth.providers.builtin.OTP
import io.github.jan.supabase.functions.functions
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.filter.FilterOperation
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.realtime
import io.github.jan.supabase.realtime.selectAsFlow
import io.github.jan.supabase.realtime.selectSingleValueAsFlow
import io.ktor.client.call.body
import io.ktor.http.HttpMethod
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.security.MessageDigest
import java.util.UUID

class SupabaseRepoImpl(
    private val supabaseClient: SupabaseClient,
    private val context : Context
) : SupabaseRepo {

    companion object {
        private const val TABLE_CHANNELS = "channels"

        private const val TABLE_DEVICE_PAIRING_SESSION = "device_pairing_sessions"

        private const val EDGE_FUNCTION_GENERATE_PAIRING_CODE= "create-device-pairing-session"

        private const val EDGE_FUNCTION_HANDLE_DEVICE_PAIRING_CODE = "handle-device-pairing-code"

         const val TVAUTHENTICATION = "TVAuthentication"
    }
    private val jsonParser = Json { ignoreUnknownKeys = true }
    private val client = supabaseClient.client
    override suspend fun insertChannelData(channelContentDto: List<ChannelDTO>): Flow<ProcessState<List<ChannelDTO>>> =
        flow{
            emit(ProcessState.Loading())
            try {
                val data = client.from(TABLE_CHANNELS).upsert(
                    values = channelContentDto
                ) {
                    select()
                }.decodeList<ChannelDTO>()
                emit(ProcessState.Success(data))
            } catch (e: Exception) {
                Log.d("SupabaseRepoImpl", "Error inserting channel data: ${e.message}")
                emit(ProcessState.Error(e.message.toString()))
            }

        }

    override suspend fun getData(): Flow<ProcessState<List<ChannelDTO>>> =flow{
        emit(ProcessState.Loading())
        try {
            val data = client.from(TABLE_CHANNELS).select().decodeList<ChannelDTO>()
            if(data.isNotEmpty()){
                emit(ProcessState.Success(data))
            } else {
                emit(ProcessState.Error("No data found"))
            }
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
        val email = getEmail()
        try {
            client.functions.invoke(
                EDGE_FUNCTION_HANDLE_DEVICE_PAIRING_CODE,
                body = buildJsonObject {
                    put("code", code)
                    put("email", email ?: "")
                }
            )
            emit(ProcessState.Success(true))
        } catch (e: Exception) {
            Log.d("SupabaseRepoImpl", "Error attaching email ID to code: ${e.message}")
            emit(ProcessState.Error(e.message.toString()))
        }

    }

    override suspend fun generatePairingCode(): Flow<ProcessState<String>> =flow{
        emit(ProcessState.Loading())
        try {
            val response = client.functions.invoke(EDGE_FUNCTION_GENERATE_PAIRING_CODE)
            val code = response.body<DevicePairingSessionDTO>()
            emit(ProcessState.Success(code.code ?: ""))

        } catch (e: Exception) {
            Log.d("SupabaseRepoImpl", "Error adding code to TV table: ${e.message}")
            emit(ProcessState.Error(e.message.toString()))
        }

    }
    @OptIn(SupabaseExperimental::class)
    override suspend fun observePairingCodeStatus(code: String): Flow<ProcessState<String>> =
        channelFlow{
            val sessionStatus: Flow<DevicePairingSessionDTO> =
                client
                    .from(TABLE_DEVICE_PAIRING_SESSION)
                    .selectSingleValueAsFlow(DevicePairingSessionDTO::code){
                        DevicePairingSessionDTO::code eq code
                        DevicePairingSessionDTO::status neq PairingSessionStatus.VERIFIED.status
                    }
            sessionStatus.collect {
                if(it.status == PairingSessionStatus.OTP_SENT.status) {
                    trySend(ProcessState.Success(it.email ?: ""))

                }
            }
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
            Log.d("TAG", "sentOTPLogin: OTP sent successfully to $mailId")
            emit(ProcessState.Success("OTP sent successfully to $mailId"))


        } catch (e: Exception) {
            Log.d("SupabaseRepoImpl", "Error sending OTP for login: ${e.message}")
            emit(ProcessState.Error(e.message.toString()))
        }

    }

    override suspend fun verifyOTP(
        mailId: String,
        otp: String
    ): Flow<ProcessState<Boolean>> =flow{
        emit(ProcessState.Loading())
        try {
            supabaseClient.client.auth.verifyEmailOtp(type = OtpType.Email.EMAIL,email = mailId, token = otp)
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

    override suspend fun getEmail(): String? {
        return client.auth.currentUserOrNull()?.email
    }


    override suspend fun getAccountInfo(): AccountInfo {
        val userMetaDataJson = client.auth.currentUserOrNull()?.userMetadata?.toString() ?: "{}"
        val userMetaData = Json.decodeFromString<UserMetaData>(userMetaDataJson)
        return AccountInfo(
            userName = userMetaData.name.ifEmpty { userMetaData.fullName },
            email = getEmail() ?:"",
            profilePicUrl = userMetaData.avatarUrl
        )
    }
}
