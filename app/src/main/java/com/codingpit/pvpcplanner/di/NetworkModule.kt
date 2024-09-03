package com.codingpit.pvpcplanner.di

import com.codingpit.pvpcplanner.data.remote.PVPCApi
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val BASE_URL = "https://api.com/"


    @Provides
    fun providesOkHttpClient(): OkHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                val newRequest =
                    originalRequest
                        .newBuilder()
                        .build()
                chain.proceed(newRequest)
            }.build()

    @Provides
    fun providesMoshi(): Moshi =
        Moshi
            .Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()

    @Provides
    fun providesRetrofit(
        okHttpClient: OkHttpClient,
        moshi: Moshi,
    ): Retrofit =
        Retrofit
            .Builder()
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    @Provides
    fun provideBantuApi(retrofit: Retrofit): PVPCApi = retrofit.create(PVPCApi::class.java)
}
