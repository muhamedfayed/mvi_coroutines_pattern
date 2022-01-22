package com.example.mvi.di.modules

import android.content.Context
import com.example.mvi.BuildConfig
import com.example.mvi.utils.RequestInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val networkModule = module {
    factory { provideRequestInterceptor(get()) }
    single { provideLoggingInterceptor() }
    single { provideOkHttpClient(get() , get()) }
    single { provideRetrofit(get()) }
}

fun provideLoggingInterceptor(): HttpLoggingInterceptor {
    val interceptor = HttpLoggingInterceptor()
    interceptor.level = HttpLoggingInterceptor.Level.BODY
    return interceptor
}

fun provideRequestInterceptor(context : Context): RequestInterceptor {
    return RequestInterceptor()
}

fun provideOkHttpClient(requestInterceptor: RequestInterceptor , httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
    return OkHttpClient.Builder().apply {
        connectTimeout(60, TimeUnit.SECONDS)
        readTimeout(60, TimeUnit.SECONDS)
        writeTimeout(60, TimeUnit.SECONDS)
        addInterceptor(requestInterceptor)
        addInterceptor(httpLoggingInterceptor)
    }.build()
}

fun provideRetrofit(okHttp: OkHttpClient): Retrofit {
    return Retrofit.Builder().apply {
        addConverterFactory(GsonConverterFactory.create())
        client(okHttp)
        baseUrl(BuildConfig.BASE_URL)
    }.build()
}