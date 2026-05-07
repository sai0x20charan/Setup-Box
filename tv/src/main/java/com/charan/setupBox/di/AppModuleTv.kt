package com.charan.setupBox.di

import android.content.Context
import com.charan.setupBox.data.local.AppDatabase
import com.charan.setupBox.data.local.dao.SetUpBoxContentDAO
import com.charan.setupBox.data.repository.SupabaseRepo
import com.charan.setupBox.data.repository.impl.SetUpBoxRepoImp
import com.charan.setupBox.data.repository.impl.SupabaseRepoImp
import com.charan.setupBox.repository.SetUpBoxContentRepository
import com.charan.shared.sync.SyncManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModuleTv {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context) : AppDatabase{
        return AppDatabase.getDatabase(context)
    }

    @Provides
    fun provideSetUpBoxDAO(database: AppDatabase) : SetUpBoxContentDAO {
        return database.setupBoxDAO()
    }

    @Provides
    fun provideSetupBoxRepo(
        dao: SetUpBoxContentDAO
    ) : SetUpBoxContentRepository {
        return SetUpBoxRepoImp(dao)
    }

    @Provides
    fun provideSupabaseRepo(
        setUpBoxContentRepository: SetUpBoxContentRepository,
        syncManager: SyncManager
    ) : SupabaseRepo {
        return SupabaseRepoImp(setUpBoxContentRepository, syncManager)
    }
}
