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

    //TODO: 4.1 search
    var type = -1
    var label = -1
    var input = ""

    //TODO: 2.2: get activity result


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
                //TODO: 4.1 search
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                type = types - 1
            }
        }



        shopLabelSearchSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                //切换选择
                label = position
                //TODO: 4.1 search
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

        //TODO: 2.2: get activity result


        //TODO: 4.1 search


        //TODO: 4.2 reset

    }

    fun initShops() {
        shopListShow.clear()
        shopListShow.addAll(shopList)
    }

    //TODO: 4.1 search


    //TODO: 3.2 delete shop info


}