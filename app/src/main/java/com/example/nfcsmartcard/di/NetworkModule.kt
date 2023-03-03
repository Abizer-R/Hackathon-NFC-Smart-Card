package com.example.nfcsmartcard.di

import com.example.nfcsmartcard.data.network.api.AuthInterceptor
import com.example.nfcsmartcard.data.network.api.StudentApi
import com.example.nfcsmartcard.data.network.api.UserApi
import com.example.nfcsmartcard.utils.Constants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient() : OkHttpClient.Builder {
        return OkHttpClient.Builder()
//            .addInterceptor(authInterceptor)  // We will add this in providesStudentApi()
            .readTimeout(30, TimeUnit.SECONDS)  // default is 10 sec
            .writeTimeout(30, TimeUnit.SECONDS) // default is 10 sec
    }

    @Provides
    @Singleton
    fun provideRetrofit() : Retrofit.Builder {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
//            .build()
    }

    @Provides
    @Singleton
    fun providesUserApi(retrofitBuilder: Retrofit.Builder, okHttpClientBuilder: OkHttpClient.Builder) : UserApi {
        return retrofitBuilder
            .client(okHttpClientBuilder.build())
            .build()
            .create(UserApi::class.java)
    }

    @Provides
    @Singleton
    fun providesStudentApi(retrofitBuilder: Retrofit.Builder, okHttpClientBuilder: OkHttpClient.Builder, authInterceptor: AuthInterceptor) : StudentApi {
        val okHttpClient = okHttpClientBuilder.addInterceptor(authInterceptor).build()
        return retrofitBuilder
            .client(okHttpClient)
            .build()
            .create(StudentApi::class.java)
    }

}