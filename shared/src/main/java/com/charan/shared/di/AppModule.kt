package com.charan.shared.di

import android.content.Context
import com.charan.shared.data.local.AppDatabase
import com.charan.shared.data.local.AppDatabase.Companion.getDatabase
import com.charan.shared.data.local.dao.ChannelDao
import com.charan.shared.data.remote.SupabaseClient
import com.charan.shared.data.repository.ChannelLocalRepository
import com.charan.shared.data.repository.SupabaseRepo
import com.charan.shared.data.repository.SyncManager
import com.charan.shared.data.repository.impl.ChannelLocalRepositoryImpl
import com.charan.shared.data.repository.impl.SupabaseRepoImpl
import com.charan.shared.data.repository.impl.SyncManagerRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideSupabaseRepo(
        supabaseClient: SupabaseClient,
        @ApplicationContext context : Context
    ) : SupabaseRepo = SupabaseRepoImpl(
        supabaseClient,
        context

    )

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ) = getDatabase(context)

    @Provides
    @Singleton
    fun provideChannelDao(appDatabase: AppDatabase) = appDatabase.channelDao()

    @Provides
    @Singleton
    fun provideSupabaseClient() = SupabaseClient()

    @Provides
    @Singleton
    fun provideChannelLocalRepo(
        channelDao: ChannelDao
    ): ChannelLocalRepository = ChannelLocalRepositoryImpl(channelDao)

    @Provides
    @Singleton
    fun provideSyncManager(
        supabaseRepo: SupabaseRepo,
        channelLocalRepository: ChannelLocalRepository
    ) : SyncManager = SyncManagerRepositoryImpl(channelLocalRepository, supabaseRepo)

}
