package com.example.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

// 1. Entities
@Entity(tableName = "favorite_orders")
data class FavoriteOrder(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val cafeId: String,
    val cafeName: String,
    val itemName: String,
    val customizations: String,
    val totalPrice: Double,
    val pointsToEarn: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "past_orders")
data class PastOrder(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val cafeId: String,
    val cafeName: String,
    val itemName: String,
    val customizations: String,
    val totalPrice: Double,
    val pointsEarned: Int,
    val status: String, // "Brewing" | "Ready for Pickup" | "Picked Up" | "Completed"
    val rating: Int = 0, // 0 = unrated, 1-5 stars
    val feedback: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_rewards")
data class UserRewards(
    @PrimaryKey val id: Int = 1,
    val totalPoints: Int = 120, // default starting points
    val username: String = "Commuter Extraordinaire"
)

// 2. Data Access Object (DAO)
@Dao
interface CoffeeDao {
    @Query("SELECT * FROM favorite_orders ORDER BY timestamp DESC")
    fun getFavorites(): Flow<List<FavoriteOrder>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteOrder)

    @Delete
    suspend fun deleteFavorite(favorite: FavoriteOrder)

    @Query("SELECT * FROM past_orders ORDER BY timestamp DESC")
    fun getPastOrders(): Flow<List<PastOrder>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPastOrder(order: PastOrder): Long

    @Update
    suspend fun updatePastOrder(order: PastOrder)

    @Query("SELECT * FROM user_rewards WHERE id = 1")
    fun getUserRewardsFlow(): Flow<UserRewards?>

    @Query("SELECT * FROM user_rewards WHERE id = 1")
    suspend fun getUserRewardsDirect(): UserRewards?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserRewards(rewards: UserRewards)
}

// 3. Database Abstract Holder
@Database(entities = [FavoriteOrder::class, PastOrder::class, UserRewards::class], version = 1, exportSchema = false)
abstract class CoffeeDatabase : RoomDatabase() {
    abstract fun coffeeDao(): CoffeeDao

    companion object {
        @Volatile
        private var INSTANCE: CoffeeDatabase? = null

        fun getDatabase(context: Context): CoffeeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CoffeeDatabase::class.java,
                    "coffee_rewards_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
