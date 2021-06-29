package com.example.finalwork

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalwork.data.*
import com.example.finalwork.data.AppDatabase.Companion.getDatabase
import kotlinx.android.synthetic.main.activity_dish_list.*
import kotlinx.android.synthetic.main.activity_new_shop.*
import kotlinx.android.synthetic.main.activity_show_list.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob


class ShowListActivity : AppCompatActivity() {

    private var shopList = ArrayList<Shop>()

    private var shopListShow = ArrayList<Shop>()

    private val applicationScope = CoroutineScope(SupervisorJob())

    private lateinit var  appDatabase : AppDatabase

    lateinit var shopViewModel: ShopViewModel

    var types = 0
    var labels = 0

    var type = -1
    var label = -1
    var input = ""

    val startActivityLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data = it.data
                val shop = Shop(data?.getStringExtra("name")?: "",
                        data?.getStringExtra("picture")?: "",
                    data?.getStringExtra("info")?: "",
                    data?.getIntExtra("label", 0)?: 0,
                    data?.getIntExtra("type", 0)?: 0)
                var createNew = data?.getBooleanExtra("createNew", true)?:true
                var shopID = data?.getLongExtra("shopID", 0)?:0
               if (!createNew) {
                    shop.id = shopID
                    shopViewModel.updateShop(shop, data?.getIntExtra("index", 0)?:0)
                }
                else {
                    shopViewModel.insertShop(shop, getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString())
                }
            } else if (it.resultCode == Activity.RESULT_CANCELED) {
                //
            }
        }

    val startShopItemLauncher: ActivityResultLauncher<Intent> =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                shopViewModel.refresh()
            }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_list)

        types = this.resources.getStringArray(R.array.shop_type_search)?.size
        labels = this.resources.getStringArray(R.array.shop_label_search)?.size

        shopTypeSearchSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                //切换选择
                type = position
                searchShops(input, type, label)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                type = types - 1
            }
        }



        shopLabelSearchSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                //切换选择
                label = position
                searchShops(input, type, label)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

                label = labels - 1
            }
        }



        val layOutManager = LinearLayoutManager(this)
        showList.layoutManager = layOutManager

        appDatabase = AppDatabase.getDatabase(this, applicationScope)

        val shopDao = appDatabase.shopDao()
        shopList = ArrayList(shopDao.loadAllShopNO())

        shopViewModel = ViewModelProvider(this,
            ShopViewModelFactory(shopList, appDatabase)).get(ShopViewModel::class.java)

        val adapter = ShopAdapter(this, shopListShow)
        showList.adapter = adapter

        shopViewModel.shops.observe(this, Observer { shop->
            shopList = ArrayList(shop)
            initShops()
//            showList.adapter?.notifyDataSetChanged()
            if (shopList.size > 0) {
                noShop.visibility = View.GONE
            }
            else {
                noShop.visibility = View.VISIBLE
            }
            val adapter = ShopAdapter(this, shopListShow)
            showList.adapter = adapter
        })

        shopTypeSearchSpinner.setSelection(types - 1)

        shopLabelSearchSpinner.setSelection(labels - 1)

        addNewShop.setOnClickListener {
            startActivityLauncher.launch(Intent(this, NewShopActivity::class.java))
        }

        clickSearchShop.setOnClickListener {
            input = editSearchShop.text.toString()
            searchShops(input, type, label)
        }

        resetShop.setOnClickListener {
            input = ""
            editSearchShop.setText("")
            shopTypeSearchSpinner.setSelection(types - 1)
            shopLabelSearchSpinner.setSelection(labels - 1)
            searchShops("", types - 1, labels - 1)
        }
    }

    fun initShops() {
        shopListShow.clear()
        shopListShow.addAll(shopList)
    }

    fun searchShops(input: String, type: Int, label: Int) {

        var types = this.resources.getStringArray(R.array.shop_type_search)?.size
        var labels = this.resources.getStringArray(R.array.shop_label_search)?.size

        var tempShop: ArrayList<Shop>

        if (type == types - 1 && label == labels - 1) {
            tempShop = ArrayList(shopList.filter {
                it.name.contains(input)
            })
        }
        else if (type == types - 1) {
            tempShop = ArrayList(shopList.filter {
                it.name.contains(input) && it.label == label
            })
        }
        else if (label == labels - 1) {
            tempShop = ArrayList(shopList.filter {
                it.name.contains(input) && it.type == type
            })
        }
        else {
            tempShop = ArrayList(shopList.filter {
                it.name.contains(input) && it.type == type && it.label == label
            })
        }
        shopListShow.clear()
        shopListShow.addAll(tempShop)

        showList.adapter?.notifyDataSetChanged()
    }

    fun deleteShop(index: Int) {
        shopViewModel.deleteShop(index)
    }

}