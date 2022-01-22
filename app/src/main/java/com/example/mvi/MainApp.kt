package com.example.mvi

import android.app.Application
import com.downloader.PRDownloader
import com.example.mvi.di.modules.networkModule
import com.example.mvi.di.modules.postsModule
import com.example.mvi.di.modules.shareModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainApp: Application() {

    override fun onCreate() {
        super.onCreate()
        PRDownloader.initialize(this)
        startKoin {
            androidContext(this@MainApp)
            modules(listOf(
                networkModule,
                postsModule,
                shareModule
            ))
        }
    }
}