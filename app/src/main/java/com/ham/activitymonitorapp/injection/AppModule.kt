package com.ham.activitymonitorapp.injection

import android.content.Context
import com.ham.activitymonitorapp.data.database.HamDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext app: Context
    ) = HamDatabase.getInstance(app)

    @Singleton
    @Provides
    fun provideUserDao(db: HamDatabase) = db.userDao()

    @Singleton
    @Provides
    fun provideExerciseDao(db: HamDatabase) = db.exerciseDao()

    @Singleton
    @Provides
    fun provideHeartrateDao(db: HamDatabase) = db.heartrateDao()
}