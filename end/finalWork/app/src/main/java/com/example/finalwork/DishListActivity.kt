package com.example.finalwork

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalwork.data.*
import kotlinx.android.synthetic.main.activity_dish_list.*
import kotlinx.android.synthetic.main.activity_new_dish.*
import kotlinx.android.synthetic.main.activity_show_list.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class DishListActivity : AppCompatActivity() {

    private var dishList = ArrayList<Dish>()

    private var dishListShow = ArrayList<Dish>()

    private val applicationScope = CoroutineScope(SupervisorJob())

    private lateinit var  appDatabase : AppDatabase

    lateinit var dishViewModel: DishViewModel

    var shopID :Long = 0
    var shopName :String = ""

    var types = 0
    var eatens = 3

    var type = -1
    var eaten = -1
    var input = ""

    val startActivityLauncher: ActivityResultLauncher<Intent> =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    val data = it.data
                    val dish = Dish(data?.getStringExtra("name")?: "",
                            data?.getStringExtra("picture")?: "",
                            data?.getStringExtra("info")?: "",
                            data?.getIntExtra("hasEaten", 0)?: 0,
                            data?.getIntExtra("type", 0)?: 0,
                            shopID)
                    var createNew = data?.getBooleanExtra("createNew", true)?:true
                    var dishID = data?.getLongExtra("dishID", 0)?:0
                    if(!createNew) {
                        dish.id = dishID
                        dishViewModel.updateDish(dish, data?.getIntExtra("index", 0)?:0)
                    }
                    else {
                        dishViewModel.insertDish(dish, getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString())
                    }

                } else if (it.resultCode == Activity.RESULT_CANCELED) {
                    //
                }
            }

    val startDishItemLauncher: ActivityResultLauncher<Intent> =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                dishViewModel.refresh()
            }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dish_list)

        shopID = intent.getLongExtra("shopID", 0)

        shopName = intent.getStringExtra("name")?:""

        dishBar.setTitle(shopName + "的美食列表")

        types = this.resources.getStringArray(R.array.dish_type_search)?.size
        eatens = 3

        dishTypeSearchSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                //切换选择
                type = position
                searchDishs(input, type, eaten)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                type = types - 1
            }
        }



        dishEatenSearchSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                //切换选择
                eaten = position
                searchDishs(input, type, eaten)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

                eaten = eatens - 1
            }
        }

        val layOutManager = LinearLayoutManager(this)
        showDishList.layoutManager = layOutManager

        appDatabase = AppDatabase.getDatabase(this, applicationScope)

        val dishDao = appDatabase.dishDao()
        dishList = ArrayList(dishDao.loadShopDish(shopID))

        dishViewModel = ViewModelProvider(this,
                DishViewModelFactory(dishList, appDatabase, shopID)).get(DishViewModel::class.java)

        val adapter = DishAdapter(this, dishListShow)
        showDishList.adapter = adapter

        dishViewModel.dishs.observe(this, Observer { dish->
            dishList = ArrayList(dish)
            initDishs()
            if (dishList.size > 0) {
                noDish.visibility = View.GONE
            }
            else {
                noDish.visibility = View.VISIBLE
            }
            val adapter = DishAdapter(this, dishListShow)
            showDishList.adapter = adapter
        })

        dishTypeSearchSpinner.setSelection(types - 1)

        dishEatenSearchSpinner.setSelection(eatens - 1)

        addNewDish.setOnClickListener {
            startActivityLauncher.launch(Intent(this, NewDishActivity::class.java))
        }
//
        clickSearchDish.setOnClickListener {
            input = editSearchDish.text.toString()
            searchDishs(input, type, eaten)
        }

        resetDish.setOnClickListener {
            input = ""
            editSearchDish.setText("")
            dishTypeSearchSpinner.setSelection(types - 1)
            dishEatenSearchSpinner.setSelection(eatens - 1)
            searchDishs("", types - 1, eatens - 1)
        }
    }

    fun initDishs() {
        dishListShow.clear()
        dishListShow.addAll(dishList)
    }

    fun searchDishs(input: String, type: Int, eaten: Int) {

        var types = this.resources.getStringArray(R.array.dish_type_search)?.size
        var eatens = 3

        var tempDish: ArrayList<Dish>

        if (type == types - 1 && eaten == eatens - 1) {
            tempDish = ArrayList(dishList.filter {
                it.name.contains(input)
            })
        }
        else if (type == types - 1) {
            tempDish = ArrayList(dishList.filter {
                it.name.contains(input) && it.hasEaten == eaten
            })
        }
        else if (eaten == eatens - 1) {
            tempDish = ArrayList(dishList.filter {
                it.name.contains(input) && it.type == type
            })
        }
        else {
            tempDish = ArrayList(dishList.filter {
                it.name.contains(input) && it.type == type && it.hasEaten == eaten
            })
        }
        dishListShow.clear()
        dishListShow.addAll(tempDish)
        showDishList.adapter?.notifyDataSetChanged()
    }

    fun updateEaten(hasEaten: Int, id: Long, index: Int){
        dishViewModel.updateEaten(hasEaten, id, index)
    }

    fun deleteDish(index: Int) {
        dishViewModel.deleteDish(index)
    }

}