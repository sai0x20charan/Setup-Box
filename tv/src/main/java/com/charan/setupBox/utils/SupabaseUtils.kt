package com.charan.setupBox.utils

import android.content.Context
import com.charan.setupBox.data.remote.supabaseClient
import io.github.jan.supabase.auth.auth

object SupabaseUtils {

    suspend fun getSessionToken(context: Context) :Boolean{
        try {
            return supabaseClient.client.auth.loadFromStorage()
        } catch (e:Exception){
            return false

        }
    }

    fun getProfilePic() : String? {
        try {
            return supabaseClient.client.auth.currentUserOrNull()?.identities?.get(0)?.identityData?.get(
                "avatar_url"
            ).toString().substringAfter("\"").substringBefore("\"")
        } catch (e:Exception){
            return null
        }
    }

    fun getUserName() : String? {
        try {
            return supabaseClient.client.auth.currentUserOrNull()?.identities?.get(0)?.identityData?.get(
                "full_name"
            ).toString().substringAfter("\"").substringBefore("\"")
        } catch (e:Exception){
            return null
        }
    }

    fun getEmail() : String? {
        try {
            return supabaseClient.client.auth.currentUserOrNull()?.email
        } catch (e:Exception){
            return null
        }
    }

}