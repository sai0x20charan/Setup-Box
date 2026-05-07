package com.charan.shared.data.local


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.charan.shared.data.local.dao.ChannelDao
import com.charan.shared.data.local.entity.ChannelEntity

@Database(entities = [ChannelEntity::class], version = 1, exportSchema = true)
abstract class AppDatabase: RoomDatabase() {

    abstract fun channelDao() : ChannelDao


        companion object {
            const val DATABASE_NAME = "stb_database"
            @Volatile
            private var INSTANCE: AppDatabase? = null
            fun getDatabase(context : Context) : AppDatabase {
                return INSTANCE ?: synchronized(this) {
                    val instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        DATABASE_NAME
                    )
                        .build()
                    INSTANCE = instance
                    instance
                }
            }
        }


}