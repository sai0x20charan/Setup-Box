package com.charan.setupBox.data.repository.impl

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.MutableLiveData
import com.charan.setupBox.BuildConfig
import com.charan.setupBox.data.local.entity.SetupBoxContent
import com.charan.setupBox.data.remote.supabaseClient
import com.charan.setupBox.data.repository.SetUpBoxRepo
import com.charan.setupBox.data.repository.SupabaseRepo
import com.charan.setupBox.utils.AppConstants
import com.charan.setupBox.utils.ProcessState
import com.charan.setupBox.utils.SupabaseUtils
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.security.MessageDigest
import java.util.UUID
import javax.inject.Inject

class SupabaseRepoImp @Inject constructor(private val setUpBoxRepo: SetUpBoxRepo) : SupabaseRepo {
    override suspend fun insertData(setupBoxContent: SetupBoxContent): Flow<ProcessState> {
        val processState= MutableStateFlow<ProcessState>(ProcessState.Loading)
        val uuid = UUID.randomUUID().toString()
        val email = SupabaseUtils.getEmail()

            try{
                supabaseClient.client.from(AppConstants.SETUPBOXCONTENT).insert(
                    SetupBoxContent(
                        channelLink = setupBoxContent.channelLink,
                        channelName = setupBoxContent.channelName,
                        channelPhoto = setupBoxContent.channelPhoto,
                        Category = setupBoxContent.Category,
                        app_Package = setupBoxContent.app_Package,
                        uuid = uuid,
                        email = email
                    )
                )
                setupBoxContent.uuid = uuid
                setupBoxContent.email = email
                setUpBoxRepo.insert(setupBoxContent)
                processState.tryEmit(ProcessState.Success)

            } catch (e:Exception){
                processState.tryEmit(ProcessState.Error(e.message.toString()))
                Log.e("SupabaseError",e.message.toString())
            }

        return processState
    }

    override suspend fun updateData(setupBoxContent: SetupBoxContent): Flow<ProcessState> {
        val processState= MutableStateFlow<ProcessState>(ProcessState.Loading)

            try{
                supabaseClient.client
                    .from(AppConstants.SETUPBOXCONTENT)
                    .update(
                        {
                            set("channelLink",setupBoxContent.channelLink)
                            set("channelName",setupBoxContent.channelName)
                            set("channelPhoto",setupBoxContent.channelPhoto)
                            set("Category",setupBoxContent.Category)
                            set("app_Package",setupBoxContent.app_Package)
                        }
                    ) {
                        filter {
                            eq("uuid",setupBoxContent.uuid!!)
                            eq("email",SupabaseUtils.getEmail()!!)
                        }
                    }
                val setupBoxData = setUpBoxRepo.getDataByUUID(setupBoxContent.uuid!!)
                setupBoxContent.id = setupBoxData.id
                setUpBoxRepo.update(setupBoxContent)
                processState.tryEmit(ProcessState.Success)

            } catch (e:Exception){
                processState.tryEmit(ProcessState.Error(e.message.toString()))
                Log.e("SupabaseError",e.message.toString())
            }


        return processState
    }

    override suspend fun deleteData(uuid: String): Flow<ProcessState> {
        val processState= MutableStateFlow<ProcessState>(ProcessState.Loading)

            try{
                supabaseClient.client
                    .from(AppConstants.SETUPBOXCONTENT)
                    .delete{
                        filter {
                            eq("uuid", uuid)
                            eq("email",SupabaseUtils.getEmail()!!)

                        }

                    }
                setUpBoxRepo.deleteByUUID(uuid)
                processState.tryEmit(ProcessState.Success)

            } catch (e:Exception){
                processState.tryEmit(ProcessState.Error(e.message.toString()))
                Log.e("SupabaseError",e.message.toString())
            }


        return processState
    }

    override suspend fun getData(): Flow<ProcessState> {
        val processState= MutableStateFlow<ProcessState>(ProcessState.Loading)

            try {
                val remoteData = supabaseClient.client.from(AppConstants.SETUPBOXCONTENT).select()
                    .decodeList<SetupBoxContent>()
                Log.d("TAG", "getSupabaseData: $remoteData")
                val localData = setUpBoxRepo.getAllDataNonLiveData()

                val localDataIds = localData.map { it.uuid }.toSet()
                val remoteDataIds = remoteData.map { it.uuid }.toSet()

                val itemsToInsert = remoteData.filter { it.uuid !in localDataIds }
                val itemsToRemove = localData.filter { it.uuid !in remoteDataIds }

                // Identify items to update
                val itemsToUpdate = remoteData.filter { remoteItem ->
                    localData.any { localItem ->
                        localItem.uuid == remoteItem.uuid && localItem != remoteItem
                    }
                }

                itemsToInsert.forEach {
                    setUpBoxRepo.insert(it)
                }

                itemsToRemove.forEach {
                    setUpBoxRepo.deleteByUUID(it.uuid!!)
                }

                // Update the local database with the changed items
                itemsToUpdate.forEach { updatedItem ->
                    setUpBoxRepo.update(updatedItem)
                }
                processState.tryEmit(ProcessState.Success)
            } catch (e:Exception){
                Log.e("SupabaseError",e.message.toString())
                processState.tryEmit(ProcessState.Error(e.message.toString()))
            }


        return processState
    }

    override suspend fun loginWithGoogle(context: Context): Flow<ProcessState> {
        val googleAuthProcess = MutableStateFlow<ProcessState>(ProcessState.Loading)

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

            try{


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
                googleAuthProcess.tryEmit(ProcessState.Success)

            }
            catch (e:Exception){
                Log.d("TAG", "LoginWithGoogle: $e")
                googleAuthProcess.tryEmit(ProcessState.Error(e.message.toString()))
            }

        return googleAuthProcess
    }

    override suspend fun attachEmailIdToCode(code: String, context:Context): Flow<ProcessState> {
        val attachStatus = MutableStateFlow<ProcessState>(ProcessState.Loading)
        try {
            supabaseClient.client.from("TVAuthentication").update(
                {
                    set("email",SupabaseUtils.getEmail())
                }
            ){
                filter {
                    eq("tv_code",code)
                    eq("isAuthenticated",false)
                }
            }
            attachStatus.tryEmit(ProcessState.Success)
        } catch (e:Exception){
            Log.d("TAG", "attachSessionId: $e")
            attachStatus.tryEmit(ProcessState.Error(e.message.toString()))
        }
        return attachStatus
    }

    override suspend fun logout(): Flow<ProcessState> {
        val logoutState = MutableStateFlow<ProcessState>(ProcessState.Loading)
        try {
            supabaseClient.client.auth.signOut()
            setUpBoxRepo.clearData()
            logoutState.tryEmit(ProcessState.Success)
        } catch (e:Exception){
            logoutState.tryEmit(ProcessState.Error(e.message.toString()))
        }
        return logoutState
    }
}