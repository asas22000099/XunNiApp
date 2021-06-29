package com.example.finalwork

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
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
import com.example.finalwork.data.Dish
import com.example.finalwork.data.Shop
import kotlinx.android.synthetic.main.activity_dish_item.*
import kotlinx.android.synthetic.main.activity_dish_list.*
import kotlinx.android.synthetic.main.activity_shop_item.*
import kotlinx.android.synthetic.main.dish_item.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import java.io.File

class DishItemActivity : AppCompatActivity() {

    private lateinit var  appDatabase : AppDatabase

    private val applicationScope = CoroutineScope(SupervisorJob())

    lateinit var dishItem:Dish

    val startActivityLauncher: ActivityResultLauncher<Intent> =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    val data = it.data
                    val dish = Dish(data?.getStringExtra("name") ?: "",
                            data?.getStringExtra("picture") ?: "",
                            data?.getStringExtra("info") ?: "",
                            data?.getIntExtra("hasEaten", 0) ?: 0,
                            data?.getIntExtra("type", 0) ?: 0,
                            dishItem.id)
                    dish.id = dishItem.id
                    appDatabase.dishDao().updateDish(dish)
                    if (dish.picture != "") {
                        dishItemPicture.visibility = View.VISIBLE
                        dishItemPicture.setImageURI(Uri.fromFile(File(dish.picture)))
                    }
                    else {
                        dishItemPicture.visibility = View.GONE
                    }
                    dishItemName.text = dish.name
                    dishItemInfo.text = dish.information
                    val types = resources.getStringArray(R.array.dish_type)
                    val labels = resources.getStringArray(R.array.dish_has_eaten)
                    dishItemType.text = types[dish.type]
                    dishItemEaten.text = labels[dish.hasEaten]
                }
            }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dish_item)

        appDatabase = AppDatabase.getDatabase(this, applicationScope)

        dishItem = intent.getSerializableExtra("dishItem") as Dish
        var position = intent.getIntExtra("position", -1)
        if (dishItem.picture != "") {
            dishItemPicture.setImageURI(Uri.fromFile(File(dishItem.picture)))
        }
        else {
            dishItemPicture.visibility = View.GONE
        }
        dishItemName.text = dishItem.name
        dishItemInfo.text = dishItem.information
        val types = resources.getStringArray(R.array.dish_type)
        val labels = resources.getStringArray(R.array.dish_has_eaten)
        dishItemType.text = types[dishItem.type]
        dishItemEaten.text = labels[dishItem.hasEaten]

        dishItemPicture.setOnClickListener {
            val intent = Intent(this, ImageShowActivity::class.java)
            intent.putExtra("path", dishItem.picture)
            startActivity(intent)
        }

        enterDishItemDiary.setOnClickListener {
            val intent = Intent(this, DiaryListActivity::class.java)
            intent.putExtra("id", dishItem.id)
            intent.putExtra("type", "dish")
            intent.putExtra("name", dishItem.name)
            startActivity(intent)
        }

        var changeEaten = 0

        if(dishItem.hasEaten == 0) {
            changeItemEaten.setText("取消打卡")
            changeEaten = 1
        }

        changeItemEaten.setOnClickListener {
            if (changeEaten == 0) {
                changeItemEaten.setText("取消打卡")
                changeEaten = 1
                dishItem.hasEaten = 1
                Toast.makeText(this, "打卡成功~", Toast.LENGTH_LONG).show()
            }
            else {
                changeItemEaten.setText("打卡菜品")
                changeEaten = 0
                dishItem.hasEaten = 0
                Toast.makeText(this, "取消打卡", Toast.LENGTH_LONG).show()
            }
            appDatabase.dishDao().updateDish(dishItem)
        }

        modifyDishItem.setOnClickListener {
            var intent = Intent(this, NewDishActivity::class.java)
            intent.putExtra("createNew", false)
            intent.putExtra("dishID", dishItem.id)
            intent.putExtra("name", dishItem.name)
            intent.putExtra("info", dishItem.information)
            intent.putExtra("type", dishItem.type)
            intent.putExtra("hasEaten", dishItem.hasEaten)
            intent.putExtra("picture", dishItem.picture)
            intent.putExtra("index", position)
            startActivityLauncher.launch(intent)
        }

        deleteDishItem.setOnClickListener {
            AlertDialog.Builder(this)
                    .setTitle("删除菜品")
                    .setMessage("确定要删除该菜品吗？与菜品相关联的所有日记都会被删除哦。")
                    .setPositiveButton("确定", DialogInterface.OnClickListener { dialog, which ->
                        appDatabase.dishDao().deleteDish(dishItem)
                        Toast.makeText(this, "菜品已删除", Toast.LENGTH_LONG).show()
                        finish()
                    })
                    .setNegativeButton("取消", DialogInterface.OnClickListener { dialog, which ->
                        Toast.makeText(this, "取消删除操作", Toast.LENGTH_LONG).show()
                    })
                    .show()
        }

    }

}