package com.faranjit.ghrepos.di

import android.content.Context
import com.faranjit.ghrepos.AndroidConnectivityChecker
import com.faranjit.ghrepos.BuildConfig
import com.faranjit.ghrepos.ConnectivityChecker
import com.faranjit.ghrepos.data.api.GithubApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private val json = Json { ignoreUnknownKeys = true }

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl("https://api.github.com/")
        .client(
            OkHttpClient.Builder()
                .addInterceptor(
                    HttpLoggingInterceptor().setLevel(
                        if (BuildConfig.ENABLE_LOGGING) {
                            HttpLoggingInterceptor.Level.BASIC
                        } else {
                            HttpLoggingInterceptor.Level.NONE
                        }
                    )
                )
                .build()
        )
        .addConverterFactory(
            json.asConverterFactory("application/json; charset=UTF8".toMediaType())
        ).build()

    @Provides
    @Singleton
    fun provideGithubApi(retrofit: Retrofit): GithubApi =
        retrofit.create(GithubApi::class.java)

    @Provides
    @Singleton
    fun provideConnectivityChecker(@ApplicationContext context: Context): ConnectivityChecker =
        AndroidConnectivityChecker(context)
}
