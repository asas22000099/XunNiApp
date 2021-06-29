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

    val startActivityLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data = it.data
                val shopItem = Shop(data?.getStringExtra("name")?: "",
                    data?.getStringExtra("picture")?: "",
                    data?.getStringExtra("info")?: "",
                    data?.getIntExtra("label", 0)?: 0,
                    data?.getIntExtra("type", 0)?: 0)
                shopItem.id = data?.getLongExtra("shopID", 0)?:0
                appDatabase.shopDao().updateShop(shopItem)
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
            } else if (it.resultCode == Activity.RESULT_CANCELED) {
                //
            }
        }

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

        modifyShopItem.setOnClickListener {
            var intent = Intent(this, NewShopActivity::class.java)
            intent.putExtra("createNew", false)
            intent.putExtra("shopID", shopItem.id)
            intent.putExtra("name", shopItem.name)
            intent.putExtra("info", shopItem.information)
            intent.putExtra("type", shopItem.type)
            intent.putExtra("label", shopItem.label)
            intent.putExtra("picture", shopItem.picture)
            intent.putExtra("index", position)
            startActivityLauncher.launch(intent)
        }

        deleteShopItem.setOnClickListener {
            AlertDialog.Builder(this)
                    .setTitle("删除店铺")
                    .setMessage("确定要删除该店铺吗？与店铺相关联的所有菜品和日记都会被删除哦。")
                    .setPositiveButton("确定", DialogInterface.OnClickListener { dialog, which ->
                        appDatabase.shopDao().deleteShop(shopItem)
                        Toast.makeText(this, "店铺已删除", Toast.LENGTH_LONG).show()
                        finish()
                    })
                    .setNegativeButton("取消", DialogInterface.OnClickListener { dialog, which ->
                        Toast.makeText(this, "取消删除操作", Toast.LENGTH_LONG).show()
                    })
                    .show()
        }
    }
}