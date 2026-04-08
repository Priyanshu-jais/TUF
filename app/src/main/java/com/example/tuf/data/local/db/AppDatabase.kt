package com.example.tuf.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.tuf.data.local.dao.BudgetDao
import com.example.tuf.data.local.dao.CategoryDao
import com.example.tuf.data.local.dao.RecurringDao
import com.example.tuf.data.local.dao.TransactionDao
import com.example.tuf.data.local.entity.BudgetEntity
import com.example.tuf.data.local.entity.CategoryEntity
import com.example.tuf.data.local.entity.RecurringTransactionEntity
import com.example.tuf.data.local.entity.TransactionEntity
import com.example.tuf.data.local.entity.SplitGroupEntity
import com.example.tuf.data.local.entity.SplitExpenseEntity
import androidx.room.migration.Migration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Main Room database for the Finance Manager application.
 *
 * Manages all local data persistence for transactions, categories, budgets, and recurring rules.
 * Pre-populates default categories on first launch via [DatabaseCallback].
 */
@Database(
    entities = [
        TransactionEntity::class,
        CategoryEntity::class,
        BudgetEntity::class,
        RecurringTransactionEntity::class,
        SplitGroupEntity::class,
        SplitExpenseEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun budgetDao(): BudgetDao
    abstract fun recurringDao(): RecurringDao
    abstract fun splitDao(): com.example.tuf.data.local.dao.SplitDao

    companion object {
        private const val DATABASE_NAME = "finance_db"

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `split_groups` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `members` TEXT NOT NULL)"
                )
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `split_expenses` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `groupId` INTEGER NOT NULL, `description` TEXT NOT NULL, `totalAmount` REAL NOT NULL, `paidBy` TEXT NOT NULL, `splitType` TEXT NOT NULL, `splitsJson` TEXT NOT NULL, `date` INTEGER NOT NULL, FOREIGN KEY(`groupId`) REFERENCES `split_groups`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE)"
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_split_expenses_groupId` ON `split_expenses` (`groupId`)")
            }
        }

        fun create(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                .addMigrations(MIGRATION_1_2)
                .addCallback(DatabaseCallback())
                .build()
        }
    }
}

/**
 * Callback to seed default categories when the database is first created.
 */
private class DatabaseCallback : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        // Seed default categories via raw SQL for simplicity at DB creation time
        val defaultCategories = buildDefaultCategories()
        defaultCategories.forEach { category ->
            db.execSQL(
                """INSERT OR IGNORE INTO categories (name, iconName, colorHex, type, isCustom)
                   VALUES ('${category.name}', '${category.iconName}', '${category.colorHex}', '${category.type}', 0)"""
            )
        }
    }

    private fun buildDefaultCategories(): List<SeedCategory> = listOf(
        // ─── Expense Categories ─────────────────────────────────────────────
        SeedCategory("Food & Dining", "restaurant", "#FF6B6B", "EXPENSE"),
        SeedCategory("Transport", "directions_car", "#4ECDC4", "EXPENSE"),
        SeedCategory("Shopping", "shopping_bag", "#FFBE0B", "EXPENSE"),
        SeedCategory("Entertainment", "movie", "#9B59B6", "EXPENSE"),
        SeedCategory("Health", "local_hospital", "#2ECC71", "EXPENSE"),
        SeedCategory("Utilities", "bolt", "#3498DB", "EXPENSE"),
        SeedCategory("Education", "school", "#E67E22", "EXPENSE"),
        SeedCategory("Travel", "flight", "#1ABC9C", "EXPENSE"),
        SeedCategory("Housing", "home", "#34495E", "EXPENSE"),
        SeedCategory("Personal Care", "spa", "#E91E63", "EXPENSE"),

        // ─── Income Categories ───────────────────────────────────────────────
        SeedCategory("Salary", "work", "#00C897", "INCOME"),
        SeedCategory("Freelance", "laptop", "#6C63FF", "INCOME"),
        SeedCategory("Investment", "trending_up", "#2196F3", "INCOME"),
        SeedCategory("Business", "business", "#FF9800", "INCOME"),
        SeedCategory("Gift", "card_giftcard", "#E91E63", "INCOME"),
        SeedCategory("Other", "category", "#9E9E9E", "INCOME")
    )
}

private data class SeedCategory(
    val name: String,
    val iconName: String,
    val colorHex: String,
    val type: String
)
