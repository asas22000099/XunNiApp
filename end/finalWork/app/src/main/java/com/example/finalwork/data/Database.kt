package com.example.finalwork.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.RoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import java.io.Serializable
import java.util.*

@Entity
data class Shop(var name: String, var picture: String,
                var information: String, var label: Int, var type: Int): Serializable {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @Ignore
    var shopDiary: List<ShopDiary>? = null

    @Ignore
    var dishList: List<Dish>? = null
}


@Entity(foreignKeys = [ForeignKey(
    entity = Shop::class,
    childColumns = ["shopID"],
    parentColumns = ["id"],
        onDelete = ForeignKey.CASCADE
)], indices = [Index("id")])
data class ShopDiary(var publishTime: Date, var diary: String, var shopID: Long): Serializable {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @Ignore
    var shop: Shop? = null

}

@Entity(foreignKeys = [ForeignKey(
    entity = Shop::class,
    childColumns = ["shopID"],
    parentColumns = ["id"],
        onDelete = ForeignKey.CASCADE
)], indices = [Index("id")])
data class Dish(var name: String, var picture: String,
                var information: String, var hasEaten: Int, var type: Int, var shopID: Long): Serializable {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @Ignore
    var shop: Shop? = null

}

@Entity(foreignKeys = [ForeignKey(
    entity = Dish::class,
    childColumns = ["dishID"],
    parentColumns = ["id"],
        onDelete = ForeignKey.CASCADE
)], indices = [Index("id")])
data class DishDiary(var publishTime: Date, var diary: String, var dishID: Long): Serializable {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @Ignore
    var dish: Dish? = null

}

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }
}

@Dao
interface ShopDao {

    @Insert
    fun insertShop(shop: Shop): Long

    @Update
    fun updateShop(newShop: Shop)

    @Query("select * from Shop")
    fun loadAllShop(): Flow<List<Shop>>

    @Query("select * from Shop")
    fun loadAllShopNO(): List<Shop>

    @Query("select * from Shop where id = :id")
    fun loadShop(id: Long): List<Shop>

    @Delete
    fun deleteShop(shop: Shop)
}

@Dao
interface ShopDiaryDao {

    @Insert
    fun insertShopDiary(shopDiary: ShopDiary): Long

    @Update
    fun updateShopDiary(newShopDiary: ShopDiary)

    @Query("select * from ShopDiary")
    fun loadAllShopDiary(): List<ShopDiary>

    @Query("select * from ShopDiary where shopID = :shopID")
    fun loadShopDiary(shopID: Long): List<ShopDiary>

    @Delete
    fun deleteShopDiary(shopDiary: ShopDiary)

}

@Dao
interface DishDao {

    @Insert
    fun insertDish(dish: Dish): Long

    @Update
    fun updateDish(newDish: Dish)

    @Query("select * from Dish")
    fun loadAllDish(): List<Dish>

    @Query("select * from Dish where shopID = :shopID")
    fun loadShopDish(shopID: Long): List<Dish>

    @Query("update Dish set hasEaten = :hasEaten where id = :id")
    fun updateEaten(hasEaten: Int, id: Long)

    @Query("SELECT * FROM Dish ORDER BY random() LIMIT 1")
    fun loadRandDish():List<Dish>

    @Delete
    fun deleteDish(dish: Dish)

}

@Dao
interface DishDiaryDao {

    @Insert
    fun insertDishDiary(dishDiary: DishDiary): Long

    @Update
    fun updateDishDiary(newDishDiary: DishDiary)

    @Query("select * from DishDiary")
    fun loadAllDishDiary(): List<DishDiary>

    @Query("select * from DishDiary where dishID = :dishID")
    fun loadDishDiary(dishID: Long): List<DishDiary>

    @Delete
    fun deleteDishDiary(dishDiary: DishDiary)

}


@Database(version = 1, entities = [Shop::class, ShopDiary::class, Dish::class, DishDiary::class])
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {

    abstract fun shopDao(): ShopDao
    abstract fun shopDiaryDao(): ShopDiaryDao
    abstract fun dishDao(): DishDao
    abstract fun dishDiaryDao(): DishDiaryDao

    companion object {
        private var instance: AppDatabase? = null

        @Synchronized
        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            instance?.let {
                return it
            }
            return Room.databaseBuilder(context.applicationContext,
                    AppDatabase::class.java, "app_database").allowMainThreadQueries()
                .build().apply {
                instance = this
            }

        }
    }
}