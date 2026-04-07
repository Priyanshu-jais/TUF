package com.example.tuf.di.modules

import com.example.tuf.data.local.DataStoreManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Koin module for app-wide singleton dependencies.
 */
val appModule = module {
    single { DataStoreManager(androidContext()) }
}
