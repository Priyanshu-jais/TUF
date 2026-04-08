package com.example.tuf.di.modules

import com.example.tuf.data.local.db.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Koin module providing Room database and all DAOs.
 */
val databaseModule = module {
    single { AppDatabase.create(androidContext()) }
    single { get<com.example.tuf.data.local.db.AppDatabase>().transactionDao() }
    single { get<com.example.tuf.data.local.db.AppDatabase>().categoryDao() }
    single { get<com.example.tuf.data.local.db.AppDatabase>().budgetDao() }
    single { get<com.example.tuf.data.local.db.AppDatabase>().recurringDao() }
    single { get<com.example.tuf.data.local.db.AppDatabase>().splitDao() }
}
