package com.example.tuf.di.modules

import com.example.tuf.data.repository.BudgetRepositoryImpl
import com.example.tuf.data.repository.CategoryRepositoryImpl
import com.example.tuf.data.repository.RecurringRepositoryImpl
import com.example.tuf.data.repository.SplitRepositoryImpl
import com.example.tuf.data.repository.TransactionRepositoryImpl
import com.example.tuf.domain.repository.BudgetRepository
import com.example.tuf.domain.repository.CategoryRepository
import com.example.tuf.domain.repository.RecurringRepository
import com.example.tuf.domain.repository.SplitRepository
import com.example.tuf.domain.repository.TransactionRepository
import org.koin.dsl.module

/**
 * Koin module binding repository interfaces to their implementations.
 */
val repositoryModule = module {
    single<TransactionRepository> { TransactionRepositoryImpl(get(), get()) }
    single<CategoryRepository> { CategoryRepositoryImpl(get()) }
    single<BudgetRepository> { BudgetRepositoryImpl(get(), get()) }
    single<RecurringRepository> { RecurringRepositoryImpl(get(), get()) }
    single<SplitRepository> { SplitRepositoryImpl(get()) }
}
