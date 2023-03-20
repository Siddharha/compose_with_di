package `in`.creativelizard.composedemo.di

import `in`.creativelizard.composedemo.data.repo.LoginRepoImpl
import `in`.creativelizard.composedemo.domain.repo.LoginRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun provideLoginRepository():LoginRepo = LoginRepoImpl()
}