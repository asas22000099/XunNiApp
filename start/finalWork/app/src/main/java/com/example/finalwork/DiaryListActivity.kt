package com.example.finalwork

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalwork.data.*
import kotlinx.android.synthetic.main.activity_diary_list.*
import kotlinx.android.synthetic.main.activity_dish_list.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import java.util.*
import kotlin.collections.ArrayList

class DiaryListActivity : AppCompatActivity() {

    private var shopDiaryList = ArrayList<ShopDiary>()

    private var shopDiaryListShow = ArrayList<ShopDiary>()

    private var dishDiaryList = ArrayList<DishDiary>()

    private var dishDiaryListShow = ArrayList<DishDiary>()

    private val applicationScope = CoroutineScope(SupervisorJob())

    private lateinit var  appDatabase : AppDatabase

    lateinit var dishDiaryViewModel: DishDiaryViewModel

    lateinit var shopDiaryViewModel: ShopDiaryViewModel

    var type = ""
    var id: Long = 0

    var name = ""

    val startActivityLauncher: ActivityResultLauncher<Intent> =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    val data = it.data
                    var createNew = data?.getBooleanExtra("createNew", true)?:true
                    var delete = data?.getBooleanExtra("delete", false)?:false
                    var index = data?.getIntExtra("index", -1)?:-1
                    if (delete && index >= 0) {
                        if (type == "shop") {
                            shopDiaryViewModel.deleteShopDiary(index)
                        }
                        else {
                            dishDiaryViewModel.deleteDishDiary(index)
                        }
                    }
                    else {
                        if (createNew) {
                            if (type == "shop") {
                                var shopDiary = ShopDiary(Date(),
                                        data?.getStringExtra("diary")?:"",
                                        id)
                                shopDiaryViewModel.insertShopDiary(shopDiary)
                            }
                            else {
                                var dishDiary = DishDiary(Date(),
                                        data?.getStringExtra("diary")?:"",
                                        id)
                                dishDiaryViewModel.insertDishDiary(dishDiary)
                            }
                        }
                        else {
                            if (type == "shop") {
                                var shopDiary = ShopDiary(Date(),
                                        data?.getStringExtra("diary")?:"",
                                        id)
                                shopDiary.id = data?.getLongExtra("diaryID", 0)?:0
                                shopDiaryViewModel.updateShopDiary(shopDiary, index)
                            }
                            else {
                                var dishDiary = DishDiary(Date(),
                                        data?.getStringExtra("diary")?:"",
                                        id)
                                dishDiary.id = data?.getLongExtra("diaryID", 0)?:0
                                dishDiaryViewModel.updateDishDiary(dishDiary, index)
                            }
                        }
                    }
                } else if (it.resultCode == Activity.RESULT_CANCELED) {
                    //
                }
            }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary_list)

        name = intent.getStringExtra("name")?:""
        diaryBar.setTitle(name + "的日记列表")

        type = intent.getStringExtra("type")?: ""
        id = intent.getLongExtra("id", 0)

        val layOutManager = LinearLayoutManager(this)
        showDiaryList.layoutManager = layOutManager

        appDatabase = AppDatabase.getDatabase(this, applicationScope)

        if (type == "shop") {
            var shopDiaryDao = appDatabase.shopDiaryDao()
            shopDiaryList = ArrayList(shopDiaryDao.loadShopDiary(id))

            val shopAdapter = ShopDiaryAdapter(this, shopDiaryListShow)
            showDiaryList.adapter = shopAdapter

            shopDiaryViewModel = ViewModelProvider(this,
                    ShopDiaryViewModelFactory(shopDiaryList, appDatabase)).get(ShopDiaryViewModel::class.java)
            shopDiaryViewModel.shopDiarys.observe(this, Observer { diary->
                shopDiaryList = ArrayList(diary)
                initShopDiary()
                if (shopDiaryList.size > 0) {
                    noDiary.visibility = View.GONE
                }
                else {
                    noDiary.visibility = View.VISIBLE
                }
                showDiaryList.adapter?.notifyDataSetChanged()
            })
        }
        else {
            var dishDiaryDao = appDatabase.dishDiaryDao()
            dishDiaryList = ArrayList(dishDiaryDao.loadDishDiary(id))

            val dishAdapter = DishDiaryAdapter(this, dishDiaryListShow)
            showDiaryList.adapter = dishAdapter

            dishDiaryViewModel = ViewModelProvider(this,
                    DishDiaryViewModelFactory(dishDiaryList, appDatabase)).get(DishDiaryViewModel::class.java)
            dishDiaryViewModel.dishDiarys.observe(this, Observer { diary->
                dishDiaryList = ArrayList(diary)
                initDishDiary()
                if (dishDiaryList.size > 0) {
                    noDiary.visibility = View.GONE
                }
                else {
                    noDiary.visibility = View.VISIBLE
                }
                showDiaryList.adapter?.notifyDataSetChanged()
            })
        }

        addNewDiary.setOnClickListener {
            var intent = Intent(this, NewDiaryActivity::class.java)
            intent.putExtra("type", type)
            intent.putExtra("id", id)
            intent.putExtra("createNew", true)
            intent.putExtra("name", name)
            startActivityLauncher.launch(intent)
        }
    }

    fun initShopDiary() {
        shopDiaryListShow.clear()
        shopDiaryListShow.addAll(shopDiaryList)
    }

    fun initDishDiary() {
        dishDiaryListShow.clear()
        dishDiaryListShow.addAll(dishDiaryList)
    }
}