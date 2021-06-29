package com.example.finalwork.data

import android.os.Environment
import androidx.annotation.WorkerThread
import androidx.lifecycle.*
import com.example.finalwork.AppApplication
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class ShopViewModel(private val shopList: List<Shop>, private val database: AppDatabase) : AndroidViewModel(AppApplication()) {

    val shops = MutableLiveData<MutableList<Shop>>()

    init {
        shops.value = ArrayList(database.shopDao().loadAllShopNO())
    }

    fun refresh() {
        shops.value = ArrayList(database.shopDao().loadAllShopNO())
    }

    fun insertShop(shop: Shop, fileDir: String) {
        shop.id = database.shopDao().insertShop(shop)
        if (shop.picture != "") {
            val oldFile = File(shop.picture)
            val newFilePath = fileDir + "/${shop.id}.png"
            val newFile = File(newFilePath)
            if (oldFile.renameTo(newFile)) {
                shop.picture = newFilePath
                database.shopDao().updateShop(shop)
            }
            else {
                shop.picture = ""
                database.shopDao().updateShop(shop)
            }
        }
        shops.value?.add(shop)
        shops.value = shops.value
    }

    fun updateShop(shop: Shop, index: Int) {
        database.shopDao().updateShop(shop)
        shops.value?.set(index, shop)
        shops.value = shops.value
    }

    fun deleteShop(index: Int) {
        var shop = shops.value?.get(index)
        shop?.let {
            if(it.picture != "") {
                val file = File(it.picture)
                if (file.isFile){
                    file.delete()
                }
            }
            database.shopDao().deleteShop(it)
            shops.value?.removeAt(index)
        }
        shops.value = shops.value
    }

}

class ShopViewModelFactory(private val shopList: List<Shop>, val database: AppDatabase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShopViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ShopViewModel(shopList, database) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class DishViewModel(private val dishList: List<Dish>, private val database: AppDatabase, val shopID: Long) : AndroidViewModel(AppApplication()) {

    val dishs = MutableLiveData<MutableList<Dish>>()

    init {
        dishs.value = ArrayList(database.dishDao().loadShopDish(shopID))
    }

    fun refresh() {
        dishs.value?.clear()
        dishs.value?.addAll(ArrayList(database.dishDao().loadShopDish(shopID)))
    }

    fun insertDish(dish: Dish, fileDir: String) {
        dish.id = database.dishDao().insertDish(dish)
        if(dish.picture != "") {
            val oldFile = File(dish.picture)
            val newFilePath = fileDir + "/dish_${dish.id}.png"
            val newFile = File(newFilePath)
            if (oldFile.renameTo(newFile)) {
                dish.picture = newFilePath
                database.dishDao().updateDish(dish)
            }
            else {
                dish.picture = ""
                database.dishDao().updateDish(dish)
            }
        }
        dishs.value?.add(dish)
        dishs.value = dishs.value
    }

    fun updateDish(dish:Dish, index: Int) {
        database.dishDao().updateDish(dish)
        dishs.value?.set(index, dish)
        dishs.value = dishs.value
    }

    fun updateEaten(hasEaten: Int, id: Long, index: Int) {
        var dish = dishs.value?.get(index)
        dish?.let {
            database.dishDao().updateEaten(hasEaten, id)
            it.hasEaten = hasEaten
            dishs.value?.set(index, it)
        }
        dishs.value = dishs.value
    }

    fun deleteDish(index: Int) {
        var dish = dishs.value?.get(index)
        dish?.let {
            if(it.picture != "") {
                val file = File(it.picture)
                if (file.isFile){
                    file.delete()
                }
            }
            database.dishDao().deleteDish(it)
            dishs.value?.removeAt(index)
        }
        dishs.value = dishs.value
    }

}

class DishViewModelFactory(private val dishList: List<Dish>, val database: AppDatabase, val shopID: Long) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DishViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DishViewModel(dishList, database, shopID) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class ShopDiaryViewModel(private val shopDiaryList: List<ShopDiary>, private val database: AppDatabase) : ViewModel() {

    val shopDiarys = MutableLiveData<MutableList<ShopDiary>>()

    init {
        shopDiarys.value = ArrayList(shopDiaryList)
    }

    fun insertShopDiary(shopDiary: ShopDiary) {
        shopDiary.id = database.shopDiaryDao().insertShopDiary(shopDiary)
        shopDiarys.value?.add(shopDiary)
        shopDiarys.value = shopDiarys.value
    }

    fun updateShopDiary(shopDiary: ShopDiary, index: Int) {
        database.shopDiaryDao().updateShopDiary(shopDiary)
        shopDiarys.value?.set(index, shopDiary)
        shopDiarys.value = shopDiarys.value
    }

    fun deleteShopDiary(index: Int) {
        var delShopDiary = shopDiarys.value?.get(index)?: ShopDiary(Date(), "", 0)
        database.shopDiaryDao().deleteShopDiary(delShopDiary)
        shopDiarys.value?.removeAt(index)
        shopDiarys.value = shopDiarys.value
    }

}

class ShopDiaryViewModelFactory(private val shopDiaryList: List<ShopDiary>, val database: AppDatabase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShopDiaryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ShopDiaryViewModel(shopDiaryList, database) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class DishDiaryViewModel(private val dishDiaryList: List<DishDiary>, private val database: AppDatabase) : ViewModel() {

    val dishDiarys = MutableLiveData<MutableList<DishDiary>>()

    init {
        dishDiarys.value = ArrayList(dishDiaryList)
    }

    fun insertDishDiary(dishDiary: DishDiary) {
        dishDiary.id = database.dishDiaryDao().insertDishDiary(dishDiary)
        dishDiarys.value?.add(dishDiary)
        dishDiarys.value = dishDiarys.value
    }

    fun updateDishDiary(dishDiary: DishDiary, index: Int) {
        database.dishDiaryDao().updateDishDiary(dishDiary)
        dishDiarys.value?.set(index, dishDiary)
        dishDiarys.value = dishDiarys.value
    }

    fun deleteDishDiary(index: Int) {
        var delDishDiary = dishDiarys.value?.get(index) ?: DishDiary(Date(), "", 0)
        database.dishDiaryDao().deleteDishDiary(delDishDiary)
        dishDiarys.value?.removeAt(index)
        dishDiarys.value = dishDiarys.value
    }

}

class DishDiaryViewModelFactory(private val dishDiaryList: List<DishDiary>, val database: AppDatabase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DishDiaryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DishDiaryViewModel(dishDiaryList, database) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}