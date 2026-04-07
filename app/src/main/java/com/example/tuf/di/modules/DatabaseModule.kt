package com.example.tuf.di.modules

import com.example.tuf.data.local.db.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Koin module providing Room database and all DAOs.
 */
val databaseModule = module {
    single { AppDatabase.create(androidContext()) }
    single { get<AppDatabase>().transactionDao() }
    single { get<AppDatabase>().categoryDao() }
    single { get<AppDatabase>().budgetDao() }
    single { get<AppDatabase>().recurringDao() }
}
