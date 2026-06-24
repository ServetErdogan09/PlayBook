package com.serveterdogan.playbook.di

import android.content.Context
import androidx.room.Room
import com.serveterdogan.playbook.data.local.PlaybookDao
import com.serveterdogan.playbook.data.local.PlaybookDatabase
import com.serveterdogan.playbook.data.repo.PlaybookRepository
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // Tüm uygulama boyunca 1 kere üretilecek
object AppModule {

    @Provides
    @Singleton
    fun providePlaybookDatabase(@ApplicationContext context: Context): PlaybookDatabase {
        return Room.databaseBuilder(
            context,
            PlaybookDatabase::class.java,
            "playbook_database"
        ).build()
    }

    @Provides
    @Singleton
    fun providePlayBookDao(database: PlaybookDatabase) : PlaybookDao{
        return database.playbookDao()
    }


    fun providePlaybookRepository(dao: PlaybookDao) : PlaybookRepository{
        return PlaybookRepository(dao = dao)
    }

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile("settings") }
        )
    }


}