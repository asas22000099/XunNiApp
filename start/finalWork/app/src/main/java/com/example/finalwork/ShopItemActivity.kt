package com.example.finalwork

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.example.finalwork.data.AppDatabase
import com.example.finalwork.data.Shop
import kotlinx.android.synthetic.main.activity_new_shop.*
import kotlinx.android.synthetic.main.activity_shop_item.*
import kotlinx.android.synthetic.main.activity_show_list.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import java.io.File

class ShopItemActivity : AppCompatActivity() {

    private val applicationScope = CoroutineScope(SupervisorJob())

    private lateinit var  appDatabase : AppDatabase

    //TODO: 2.4: update shop information


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_item)

        appDatabase = AppDatabase.getDatabase(this, applicationScope)

        var shopItem = intent.getSerializableExtra("shopItem") as Shop
        var position = intent.getIntExtra("position", -1)
        if (shopItem.picture != "") {
            shopItemPicture.visibility = View.VISIBLE
            shopItemPicture.setImageURI(Uri.fromFile(File(shopItem.picture)))
        }
        else {
            shopItemPicture.visibility = View.GONE
        }
        shopItemName.text = shopItem.name
        shopItemInfo.text = shopItem.information
        val types = resources.getStringArray(R.array.shop_type)
        val labels = resources.getStringArray(R.array.shop_label)
        shopItemType.text = types[shopItem.type]
        shopItemLabel.text = labels[shopItem.label]

        shopItemPicture.setOnClickListener {
            val intent = Intent(this, ImageShowActivity::class.java)
            intent.putExtra("path", shopItem.picture)
            startActivity(intent)
        }

        enterShopItemDiary.setOnClickListener {
            val intent = Intent(this, DiaryListActivity::class.java)
            intent.putExtra("id", shopItem.id)
            intent.putExtra("type", "shop")
            intent.putExtra("name", shopItem.name)
            startActivity(intent)
        }

        enterItemDish.setOnClickListener {
            val intent = Intent(this, DishListActivity::class.java)
            intent.putExtra("shopID", shopItem.id)
            intent.putExtra("name", shopItem.name)
            startActivity(intent)
        }

        //TODO: 2.4: update shop information


        //TODO: 3.3 delete shop info

    }
}