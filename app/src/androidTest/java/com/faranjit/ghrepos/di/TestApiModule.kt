package com.faranjit.ghrepos.di

import com.faranjit.ghrepos.com.faranjit.ghrepos.data.api.FakeGithubApi
import com.faranjit.ghrepos.data.api.GithubApi
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [ApiModule::class]
)
object TestApiModule {

    @Provides
    @Singleton
    fun provideGithubApi(): GithubApi = FakeGithubApi()
}
