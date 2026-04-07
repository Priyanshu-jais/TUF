package com.example.tuf

import android.app.Application
import com.example.tuf.di.modules.appModule
import com.example.tuf.di.modules.databaseModule
import com.example.tuf.di.modules.repositoryModule
import com.example.tuf.di.modules.useCaseModule
import com.example.tuf.di.modules.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

/**
 * Application class that initializes Koin dependency injection on app start.
 */
class FinanceApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@FinanceApp)
            modules(
                appModule,
                databaseModule,
                repositoryModule,
                useCaseModule,
                viewModelModule
            )
        }
    }
}
