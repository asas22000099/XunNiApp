package com.example.finalwork

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.finalwork.data.AppDatabase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import java.io.File
import java.util.*
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private lateinit var  appDatabase : AppDatabase

    private val applicationScope = CoroutineScope(SupervisorJob())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        appDatabase = AppDatabase.getDatabase(this, applicationScope)

        val dishExist = appDatabase.dishDao().loadRandDish()
        if (dishExist.isNotEmpty()) {
            val dish = dishExist[0]
            val shop = appDatabase.shopDao().loadShop(dish.shopID)[0]

            if (dish.picture != "") {
                nowPicture.setImageURI(Uri.fromFile(File(dish.picture)))
            }
            nowDish.text = dish.name
            nowShop.text = "--来自 " + shop.name
            nowInfo.text = dish.information
        }

        var tms = Calendar.getInstance()

        nowYear.setText(tms.get(Calendar.YEAR).toString() + "年")

        nowDate.setText((tms.get(Calendar.MONTH) + 1).toString() + "月" + tms.get(Calendar.DAY_OF_MONTH).toString() + "日")

        intoList?.setOnClickListener {
           val intent = Intent(this, ShowListActivity::class.java)
            startActivity(intent)
        }

    }
}